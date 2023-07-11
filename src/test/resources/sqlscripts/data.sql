DELETE FROM gift_certificate;
DELETE FROM tag;
DELETE FROM gift_tag;


INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES
  ('Flower Vase', 'A beautiful ceramic flower vase', 49.99, 7, '2023-06-24 10:00:00', '2023-06-24 10:00:00'),
  ('Cufflinks', 'Elegant silver cufflinks for men', 79.99, 5, '2023-06-23 15:30:00', '2023-06-23 15:30:00'),
  ('Scented Candle', 'A lavender-scented candle in a glass jar', 24.99, 3, '2023-06-22 09:45:00', '2023-06-22 09:45:00'),
  ('Photo Frame', 'A wooden photo frame for 4x6 pictures', 14.99, 7, '2023-06-21 14:15:00', '2023-06-21 14:15:00'),
  ('Wine Glasses Set', 'A set of two crystal wine glasses', 59.99, 7, '2023-06-20 18:20:00', '2023-06-20 18:20:00'),
  ('Travel Mug', 'Stainless steel insulated travel mug', 19.99, 5, '2023-06-19 11:30:00', '2023-06-19 11:30:00'),
  ('Perfume Set', 'A collection of three designer perfumes', 129.99, 7, '2023-06-18 16:45:00', '2023-06-18 16:45:00'),
  ('Chocolates Assortment', 'An assortment of delicious chocolates', 39.99, 5, '2023-06-17 13:00:00', '2023-06-17 13:00:00'),
  ('Teddy Bear', 'A fluffy teddy bear for cuddling', 29.99, 7, '2023-06-16 09:30:00', '2023-06-16 09:30:00'),
  ('Jewelry Box', 'A wooden jewelry box with compartments', 49.99, 7, '2023-06-15 16:15:00', '2023-06-15 16:15:00');

INSERT INTO tag (name) VALUES ('Friendship'),
 ('Love'),
 ('Birthday'),
 ('Anniversary'),
 ('Congratulations'),
 ('Graduation'),
 ('Thank You'),
 ('Sympathy'),
 ('Get Well Soon'),
 ('New Baby');

 INSERT INTO gift_tag (gift_id,tag_id) VALUES (1,1),
  (1,2),(2,2),
  (2,3),(3,3),
  (3,4),(4,4),
  (4,5),(5,5),
  (5,6),(6,6),
  (6,7),(7,7),
  (7,8),(8,8),
  (8,9),(9,9),
  (9,10),(10,10),
  (10,1);