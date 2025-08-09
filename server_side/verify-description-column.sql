-- Verification script to check the description column type
-- Run this to confirm the column was successfully updated to TEXT

SELECT 
    column_name, 
    data_type, 
    character_maximum_length,
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'product' 
AND column_name = 'description';
