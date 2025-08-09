-- Manual command to update description column to TEXT type
-- Run this in your database client (pgAdmin, DBeaver, etc.)

ALTER TABLE product ALTER COLUMN description TYPE TEXT;
