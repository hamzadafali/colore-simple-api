CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_bookings_room_time ON bookings (room_id, start_time, end_time);
