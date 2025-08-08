-- Temporarily disable Row Level Security (RLS) for testing
-- This will allow your application to connect without authentication issues

-- Disable RLS on all tables
ALTER TABLE public.users DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.product DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_item DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_products DISABLE ROW LEVEL SECURITY;

-- Verify RLS is disabled
SELECT schemaname, tablename, rowsecurity 
FROM pg_tables 
WHERE schemaname = 'public' 
AND tablename IN ('users', 'product', 'cart', 'cart_item', 'orders', 'order_products');
