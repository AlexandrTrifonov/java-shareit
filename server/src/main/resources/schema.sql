DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  available boolean DEFAULT FALSE NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT,
  booker_id BIGINT,
  status varchar(20),
  CONSTRAINT pk_booking PRIMARY KEY (id),
  CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
  CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text varchar(255),
  item_id BIGINT,
  author_id BIGINT,
  created TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_comment PRIMARY KEY (id),
  CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
  CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(255),
  user_id BIGINT,
  created TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_request PRIMARY KEY (id),
  CONSTRAINT fk_request_to_users FOREIGN KEY(user_id) REFERENCES users(id)
);