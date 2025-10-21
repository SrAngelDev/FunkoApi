-- Insertamos las categorías que antes eran Enums
INSERT INTO categorias (nombre) VALUES ('MARVEL');
INSERT INTO categorias (nombre) VALUES ('STAR_WARS');
INSERT INTO categorias (nombre) VALUES ('DISNEY');
INSERT INTO categorias (nombre) VALUES ('ANIME');
INSERT INTO categorias (nombre) VALUES ('OTROS');

INSERT INTO funkos (nombre, precio, categoria_id) VALUES ('Iron Man', 14.99, 1);
INSERT INTO funkos (nombre, precio, categoria_id) VALUES ('Darth Vader', 15.99, 2);