-- Migration: Remove category column from product table
-- This migration removes the old category column since we now use categories (JSON)

-- Check if category column exists and remove it
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'product' 
        AND column_name = 'category'
    ) THEN
        ALTER TABLE product DROP COLUMN category;
    END IF;
END $$;
