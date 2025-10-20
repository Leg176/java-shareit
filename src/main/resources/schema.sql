DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
              name VARCHAR(100) NOT NULL,
              email VARCHAR(320) NOT NULL,
              UNIQUE(email)
            );

CREATE TABLE IF NOT EXISTS requests (
              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
              description VARCHAR(2000) NOT NULL,
              requestor_id BIGINT NOT NULL,
              CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id)
            );

CREATE TABLE IF NOT EXISTS items (
              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
              name VARCHAR(100) NOT NULL,
              description VARCHAR(2000) NOT NULL,
              is_available Boolean,
              owner_id BIGINT NOT NULL,
              request_id BIGINT,
              CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
              CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
            );

CREATE TABLE IF NOT EXISTS bookings (
              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
              start_date TIMESTAMP WITHOUT TIME ZONE,
              end_date TIMESTAMP WITHOUT TIME ZONE,
              item_id BIGINT NOT NULL,
              booker_id BIGINT NOT NULL,
              status VARCHAR CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
              CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
              CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
              text VARCHAR(1000),
              item_id BIGINT NOT NULL,
              author_id  BIGINT NOT NULL,
              CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
              CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);