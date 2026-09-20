-- Product/category seed data.
-- NOTE: Seed USERS (admin/seller/buyer) are inserted at application startup by
-- SeedDataInitializer (listener package) using jBCrypt at runtime, so real
-- bcrypt hashes are always generated correctly rather than hardcoded here.
-- This file only seeds non-sensitive demo product data, inserted after users exist.

-- Run manually against a fresh DB (after the app has started once and seeded users),
-- or adapt seller_id values to match your seeded seller's actual id.

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT id, 'Wireless Mouse', 'Ergonomic wireless mouse, 2.4GHz', 599.00, 50, 'Electronics', 'https://placehold.co/300x300?text=Mouse'
FROM users WHERE email = 'seller@rageshmart.com';

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT id, 'Mechanical Keyboard', 'RGB backlit mechanical keyboard', 2499.00, 30, 'Electronics', 'https://placehold.co/300x300?text=Keyboard'
FROM users WHERE email = 'seller@rageshmart.com';

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT id, 'Cotton T-Shirt', 'Plain round-neck cotton t-shirt', 399.00, 100, 'Apparel', 'https://placehold.co/300x300?text=T-Shirt'
FROM users WHERE email = 'seller@rageshmart.com';
