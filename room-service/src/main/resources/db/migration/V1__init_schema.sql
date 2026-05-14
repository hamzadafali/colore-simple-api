CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    capacity INTEGER NOT NULL
);

CREATE TABLE features (
    id UUID PRIMARY KEY,
    label VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE room_features (
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    feature_id UUID NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, feature_id)
);
