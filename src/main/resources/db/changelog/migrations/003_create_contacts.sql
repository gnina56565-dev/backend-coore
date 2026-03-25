--liquibase formatted sql
--changeset your-name:BCORE-32-3
CREATE TABLE contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address_city VARCHAR(100) NOT NULL,
    address_street VARCHAR(255) NOT NULL,
    address_zip VARCHAR(20),
    company_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contacts_company FOREIGN KEY (company_id) REFERENCES companies(id)
);