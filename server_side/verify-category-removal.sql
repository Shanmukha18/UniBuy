-- Verification script to check if category column was removed
-- Run this to confirm the column was successfully removed

SELECT 
    column_name, 
    data_type, 
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'product' 
ORDER BY ordinal_position;
