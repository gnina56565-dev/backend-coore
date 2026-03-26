--liquibase formatted sql
--changeset your-name:BCORE-32-1
CREATE TABLE companies (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);