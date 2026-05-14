-- Ajoute le statut métier des réservations existantes et futures avec CONFIRMED comme valeur par défaut.
ALTER TABLE bookings ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED';
