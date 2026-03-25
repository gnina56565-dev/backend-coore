--liquibase formatted sql
--changeset your-name:BCORE-32-2
CREATE TABLE leads (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50),
    company_id UUID NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leads_company FOREIGN KEY (company_id) REFERENCES companies(id)
);