-- SQL script to remove the category column from the product table
-- Run this script in your database to remove the old category column

-- Step 1: Check if the category column exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'product' 
        AND column_name = 'category'
    ) THEN
        -- Step 2: Remove the category column
        ALTER TABLE product DROP COLUMN category;
        RAISE NOTICE 'Category column removed successfully';
    ELSE
        RAISE NOTICE 'Category column does not exist';
    END IF;
END $$;
