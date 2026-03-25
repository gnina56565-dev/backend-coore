--liquibase formatted sql
--changeset your-name:BCORE-32-5
CREATE TABLE products(
   id UUID PRIMARY KEY NOT NULL,
   name VARCHAR(255) NOT NULL,
   sku VARCHAR(100) UNIQUE NOT NULL,
   price DECIMAL(19, 2) NOT NULL,
   active BOOLEAN NOT NULL DEFAULT TRUE
);