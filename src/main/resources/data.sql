INSERT INTO categoria (nombre, created_at, updated_at) VALUES ('MARVEL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO categoria (nombre, created_at, updated_at) VALUES ('STAR_WARS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO categoria (nombre, created_at, updated_at) VALUES ('DISNEY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO categoria (nombre, created_at, updated_at) VALUES ('ANIME', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO categoria (nombre, created_at, updated_at) VALUES ('OTROS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO funko (nombre, precio, categoria_id, fecha_lanzamiento, uuid, created_at, updated_at)
VALUES ('Iron Man', 14.99, 1, '2018-06-15', RANDOM_UUID(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO funko (nombre, precio, categoria_id, fecha_lanzamiento, uuid, created_at, updated_at)
VALUES ('Darth Vader', 15.99, 2, '2017-05-04', RANDOM_UUID(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);