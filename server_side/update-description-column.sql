-- SQL script to update the description column to TEXT type
-- This allows for unlimited length descriptions

-- Step 1: Check if the description column exists and update it
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'product' 
        AND column_name = 'description'
    ) THEN
        -- Update the description column to TEXT type
        ALTER TABLE product ALTER COLUMN description TYPE TEXT;
        RAISE NOTICE 'Description column updated to TEXT type successfully';
    ELSE
        RAISE NOTICE 'Description column does not exist';
    END IF;
END $$;
