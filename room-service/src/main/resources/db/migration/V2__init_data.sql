INSERT INTO rooms (id, name, capacity) VALUES ('11111111-1111-1111-1111-111111111111', 'Atlas', 4) ON CONFLICT DO NOTHING;
INSERT INTO rooms (id, name, capacity) VALUES ('22222222-2222-2222-2222-222222222222', 'Boreal', 8) ON CONFLICT DO NOTHING;
INSERT INTO rooms (id, name, capacity) VALUES ('33333333-3333-3333-3333-333333333333', 'Cirrus', 12) ON CONFLICT DO NOTHING;

INSERT INTO features (id, label) VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Projector') ON CONFLICT DO NOTHING;
INSERT INTO features (id, label) VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Whiteboard') ON CONFLICT DO NOTHING;
INSERT INTO features (id, label) VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Video conferencing') ON CONFLICT DO NOTHING;

INSERT INTO room_features (room_id, feature_id) VALUES ('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa') ON CONFLICT DO NOTHING;
INSERT INTO room_features (room_id, feature_id) VALUES ('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb') ON CONFLICT DO NOTHING;
INSERT INTO room_features (room_id, feature_id) VALUES ('33333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc') ON CONFLICT DO NOTHING;
