
> Ce fichier est lu par l'agent Claude avant toute génération de code lié à la persistance.
> Sources de référence : Hibernate ORM 6.x docs, Spring Data JPA Reference, Flyway docs,
> "High-Performance Java Persistence" (Vlad Mihalcea), Baeldung best practices.

---

## 1. FLYWAY — Migrations SQL

### Conventions de nommage obligatoires
```
V{version}__{description_snake_case}.sql   ← migration versionnée
U{version}__{description_snake_case}.sql   ← undo (si Flyway Teams)
R__{description_snake_case}.sql            ← repeatable
```
- Version : `V1__`, `V2__`, `V1_1__` (jamais de date seule comme `V20240101__`)
- Description : snake_case, explicite (`create_user_table`, pas `init`)
- Double underscore (`__`) obligatoire entre version et description

### Structure d'un fichier d'init (`V1__init_schema.sql`)
```sql
-- 1. Extensions si PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Tables sans FK d'abord (tables "feuilles" en dernier)
-- Ordre : parent → enfant → table de jonction

-- 3. Chaque table suit ce patron :
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,          -- ou UUID si distribué
    -- colonnes métier
    email       VARCHAR(255) NOT NULL,
    -- audit columns TOUJOURS présentes
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     BIGINT      NOT NULL DEFAULT 0  -- pour @Version Hibernate
);

-- 4. Contraintes d'unicité déclarées DANS la table
CONSTRAINT uq_users_email UNIQUE (email),

-- 5. FK déclarées APRÈS toutes les tables (ALTER TABLE ou CONSTRAINT inline)
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE RESTRICT   -- jamais CASCADE sans réflexion
    ON UPDATE CASCADE;

-- 6. Index explicites sur toutes les FK et colonnes de recherche fréquentes
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status  ON orders(status);
-- Index composites si requêtes multi-colonnes courantes
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
```

### Règles absolues Flyway
- **Jamais modifier** un script `V` déjà appliqué en production → créer un nouveau `V`
- **Idempotence** : utiliser `CREATE TABLE IF NOT EXISTS`, `CREATE INDEX IF NOT EXISTS`
- **Pas de logique applicative** dans les migrations (pas de SELECT complexe, pas de procédures stockées sauf nécessité absolue)
- Chaque migration doit être **transactionnelle** (Flyway l'encapsule par défaut)
- Tester en local : `./mvnw flyway:migrate -Dflyway.url=...`

---

## 2. MODÉLISATION — Règles Hibernate / JPA

### Types d'identifiants
```java
// Préférer SEQUENCE (performance) à AUTO ou IDENTITY
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
@SequenceGenerator(name = "user_seq", sequenceName = "users_id_seq", allocationSize = 50)
private Long id;

// UUID distribué si nécessaire
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(columnDefinition = "uuid", updatable = false, nullable = false)
private UUID id;
```
- **Ne jamais utiliser** `GenerationType.AUTO` en production (comportement imprévisible selon le dialecte)
- `allocationSize = 50` par défaut pour limiter les allers-retours DB

### @Version — Verrou optimiste TOUJOURS présent
```java
@Version
@Column(nullable = false)
private Long version = 0L;
// → correspond à la colonne `version BIGINT NOT NULL DEFAULT 0` en SQL
```

### Audit — utiliser Spring Data Auditing
```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;
}
```

---

## 3. FETCH TYPE — LAZY vs EAGER (règle critique de performance)

### Règle générale : TOUT en LAZY par défaut
```java
// CORRECT ✓
@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
@ManyToMany(fetch = FetchType.LAZY)
@OneToOne(fetch = FetchType.LAZY)   // même OneToOne !

// INCORRECT ✗ (EAGER = N+1 assurés)
@OneToMany(fetch = FetchType.EAGER)
@ManyToMany(fetch = FetchType.EAGER)
```

### Exceptions tolérées pour EAGER
- `@ManyToOne` : `EAGER` est le défaut JPA mais **à surcharger en LAZY** si l'entité parente est lourde
- Jamais plus de **1 collection** en JOIN FETCH dans la même requête (HibernateException: cannot simultaneously fetch multiple bags)

### Charger les données nécessaires via JPQL / @EntityGraph
```java
// Préférer à EAGER global
@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
Optional<User> findWithRolesById(@Param("id") Long id);

// Ou @EntityGraph
@EntityGraph(attributePaths = {"roles", "address"})
Optional<User> findById(Long id);
```

### Problème N+1 — détecter et corriger
```yaml
# application.properties — ACTIVER en développement
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

---

## 4. RELATIONS BIDIRECTIONNELLES — Synchronisation obligatoire

### Règle : toujours synchroniser LES DEUX CÔTÉS en mémoire
```java
// @OneToMany / @ManyToOne bidirectionnel
@Entity
public class User {
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // Helper method OBLIGATOIRE
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);     // sync côté ManyToOne
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);     // sync côté ManyToOne
    }
}

@Entity
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

### @ManyToMany — toujours via table de jonction explicite
```java
// EVITER @ManyToMany direct → utiliser entité de jonction explicite
@Entity
@Table(name = "user_roles")
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    private Role role;

    private Instant assignedAt;
}

@Embeddable
public class UserRoleId implements Serializable {
    private Long userId;
    private Long roleId;
    // equals() et hashCode() basés sur les deux champs
}
```

### equals() et hashCode() — règle Vlad Mihalcea
```java
// Baser sur un identifiant métier (business key), PAS sur l'id auto-généré
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return email != null && email.equals(user.getEmail());
}

@Override
public int hashCode() {
    return getClass().hashCode(); // constant hashCode pour la cohérence Set
}
```

---

## 5. CASCADE — Règles de sécurité

```java
// AUTORISÉ : cascade depuis le parent propriétaire
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Address> addresses;   // adresses appartiennent à l'user

// DANGEREUX : ne JAMAIS cascader vers des entités partagées
@ManyToOne
// → PAS de cascade ici, un Order ne "possède" pas un Product

// INTERDIT : CascadeType.REMOVE sur @ManyToMany
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // jamais REMOVE
private Set<Tag> tags;
```

---

## 6. CONTRAINTES D'UNICITÉ

### Au niveau SQL (source de vérité)
```sql
-- Unicité simple
CONSTRAINT uq_users_email UNIQUE (email)

-- Unicité composite
CONSTRAINT uq_product_sku_warehouse UNIQUE (sku, warehouse_id)

-- Unicité partielle (PostgreSQL)
CREATE UNIQUE INDEX uq_active_subscription
    ON subscriptions(user_id)
    WHERE status = 'ACTIVE';
```

### Au niveau JPA (cohérence applicative)
```java
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_email", columnNames = {"email"}),
        @UniqueConstraint(name = "uq_users_phone_country",
                          columnNames = {"phone", "country_code"})
    }
)
```

### Validation Bean — couche applicative
```java
@Column(nullable = false, unique = true, length = 255)
@Email
@NotBlank
private String email;
```

---

## 7. NOMMAGE DES COLONNES ET TABLES

```java
// Toujours déclarer explicitement — ne pas dépendre du naming strategy par défaut
@Table(name = "users")
@Column(name = "first_name", nullable = false, length = 100)
@JoinColumn(name = "user_id", nullable = false)

// Colonnes discriminantes pour l'héritage
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
```

---

## 8. COLLECTIONS — Choisir le bon type

```java
// List : si l'ordre compte, si doublons possibles
@OneToMany(mappedBy = "user")
private List<Order> orders = new ArrayList<>();

// Set : si unicité sémantique, si @ManyToMany
@ManyToMany
private Set<Role> roles = new HashSet<>();

// Map : si accès par clé
@OneToMany(mappedBy = "user")
@MapKey(name = "type")
private Map<AddressType, Address> addresses = new HashMap<>();

// EVITER Bag (List sans @OrderColumn) avec plusieurs JOIN FETCH simultanés
```

---

## 9. CONFIGURATION `application.properties` / `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    hikari:
      maximum-pool-size: 10        # CPU * 2 + nb disques (règle HikariCP)
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: validate            # JAMAIS create-drop en prod ; validate avec Flyway
    open-in-view: false             # TOUJOURS false — évite les lazy load en vue
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 25            # INSERT/UPDATE en batch
          order_inserts: true
          order_updates: true
        query:
          fail_on_pagination_over_collection_fetch: true  # détecte HHH90003004

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false
    validate-on-migrate: true
    out-of-order: false
```

---

## 10. PATTERNS À ÉVITER (anti-patterns critiques)

| Anti-pattern | Conséquence | Solution |
|---|---|---|
| `ddl-auto: create` en prod | Perte de données | `validate` + Flyway |
| `FetchType.EAGER` sur collections | N+1, OutOfMemory | LAZY + JOIN FETCH ciblé |
| `CascadeType.ALL` sur @ManyToOne | Suppressions en cascade non voulues | Limiter aux @OneToMany ownership |
| `open-in-view: true` | Connexion DB tenue pendant rendu vue | `false` toujours |
| equals/hashCode sur `id` auto | Comportement erratique dans les Sets | Business key ou hashCode constant |
| Pas de colonne `version` | Pas de verrou optimiste | `@Version` sur toutes les entités |
| Migration SQL modifiée après apply | Flyway checksum mismatch | Nouvelle migration versionnée |
| SELECT * (pas de DTO projection) | Transfert inutile de données | Interface-based projections ou records |

---

## 11. GÉNÉRATION D'UN FICHIER FLYWAY — CHECKLIST

Quand je demande de générer un fichier Flyway, l'agent DOIT :

1. **Nommer** le fichier `V{N}__{description}.sql` avec double underscore
2. **Ordonner** les tables : parents avant enfants, tables de jonction en dernier
3. **Ajouter** sur chaque table : `created_at`, `updated_at`, `version` (audit + optimistic lock)
4. **Déclarer** toutes les contraintes `UNIQUE` avec un nom explicite (`uq_table_colonne`)
5. **Nommer** toutes les FK (`fk_table_reference`)
6. **Créer** les index sur chaque FK et chaque colonne de recherche fréquente
7. **Préciser** `ON DELETE` et `ON UPDATE` sur chaque FK (jamais laisser par défaut implicite)
8. **Utiliser** `IF NOT EXISTS` pour l'idempotence
9. **Commenter** chaque bloc de table avec `-- ==== TABLE xxx ====`
10. **Ne pas inclure** de logique applicative (pas de INSERT de données de référence dans V1, sauf si explicitement demandé → fichier séparé `V1_1__seed_data.sql`)

---

## 12. EXEMPLE DE SORTIE ATTENDUE

Quand l'utilisateur dit :
> "Crée-moi le fichier Flyway d'init pour User, Order, Product, OrderItem"

L'agent génère `V1__init_schema.sql` avec :
- `users` → `orders` (FK `fk_orders_user`) → `order_items` (FK vers `orders` ET `products`) → `products`
- Index sur `order_items.order_id`, `order_items.product_id`, `orders.user_id`, `orders.status`
- Unicité : `uq_users_email`, `uq_products_sku`
- Audit complet sur chaque table
- `version BIGINT NOT NULL DEFAULT 0` sur chaque table