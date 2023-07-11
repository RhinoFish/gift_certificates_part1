DROP TABLE IF EXISTS gift_certificate;
CREATE TABLE gift_certificate (
       gift_id		 int not null auto_increment,
       name		      varchar(30) UNIQUE,
       description 	      varchar(50),
       price		      numeric,
       duration		      int,
       create_date	      timestamp,
       last_update_date	      timestamp,
       PRIMARY KEY(gift_id),
       constraint uname unique (name)
);
DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
       tag_id		      int not null auto_increment,
       name		      varchar(30) UNIQUE,
       PRIMARY KEY(tag_id),
       constraint unametag unique(name)
);
DROP TABLE IF EXISTS gift_tag;
CREATE TABLE gift_tag (
       gift_id		      int not null,
       tag_id		      int not null,
       PRIMARY KEY(gift_id, tag_id),
       FOREIGN KEY(gift_id) REFERENCES gift_certificate(gift_id),
       FOREIGN KEY(tag_id)  REFERENCES tag(tag_id)
);