-- Очистка данных
DELETE FROM deal_product;
DELETE FROM deals;
DELETE FROM products;
DELETE FROM contacts;
DELETE FROM leads;
DELETE FROM companies;

-- 1. Компании
INSERT INTO companies (id, name, industry, created_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Tech Solutions Inc.', 'IT', NOW()),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Green Energy Corp', 'Energy', NOW());

-- 2. Лиды
INSERT INTO leads (id, email, company_id, status, created_at, version) VALUES
    ('10000000-0000-0000-0000-000000000001', 'john@techsolutions.com', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'NEW', NOW(), 0),
    ('10000000-0000-0000-0000-000000000002', 'alice@techsolutions.com', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CONTACTED', NOW(), 0),
    ('10000000-0000-0000-0000-000000000003', 'bob@greenenergy.com', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'QUALIFIED', NOW(), 0),
    ('10000000-0000-0000-0000-000000000004', 'charlie@greenenergy.com', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'NEW', NOW(), 0),
    ('10000000-0000-0000-0000-000000000005', 'eve@startup.io', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CONTACTED', NOW(), 0);

-- 3. Продукты
INSERT INTO products (id, name, sku, price) VALUES
    ('20000000-0000-0000-0000-000000000001', 'CRM License', 'CRM-001', 500.00),
    ('20000000-0000-0000-0000-000000000002', 'Consulting Hour', 'CONS-001', 150.00),
    ('20000000-0000-0000-0000-000000000003', 'Cloud Storage 1TB', 'CLD-1TB', 50.00);

-- 4. Сделки (Статусы NEW и NEGOTIATION)
INSERT INTO deals (id, lead_id, amount, status, created_at, version) VALUES
    ('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 2500.00, 'NEW', NOW(), 0),
    ('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000003', 750.00, 'NEGOTIATION', NOW(), 0);

-- 5. Товары в сделках (ИСПРАВЛЕНО: одинарные кавычки для UUID)
INSERT INTO deal_product (id, deal_id, product_id, quantity, unit_price) VALUES
    ('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 5, 500.00),
    ('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 5, 150.00);