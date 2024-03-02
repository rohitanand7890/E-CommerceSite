-- DO NOT DELETE THIS FILE
-- Write down database schema below.
-- This file will be loaded at initialization.
CREATE TABLE IF NOT EXISTS items (id CHAR(8) NOT NULL PRIMARY KEY, name TEXT, description TEXT, price INTEGER, stock INTEGER);
CREATE TABLE IF NOT EXISTS users (id CHAR(8) NOT NULL PRIMARY KEY, name TEXT, savings INTEGER);
-- Create orders table to store order records
CREATE TABLE IF NOT EXISTS orders (id CHAR(8) NOT NULL PRIMARY KEY, item_id CHAR(8) NOT NULL, user_id CHAR(8) NOT NULL, quantity INTEGER, amount INTEGER);
