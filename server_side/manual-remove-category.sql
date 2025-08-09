-- Manual command to remove category column
-- Run this in your database client (pgAdmin, DBeaver, etc.)

ALTER TABLE product DROP COLUMN IF EXISTS category;
