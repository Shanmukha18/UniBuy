-- Enable Row Level Security (RLS) on all tables
-- This script will enable RLS and create appropriate policies for your e-commerce application

-- Enable RLS on all tables
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_item ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_products ENABLE ROW LEVEL SECURITY;

-- Create policies for users table
CREATE POLICY "Users can view their own data" ON public.users
    FOR SELECT USING (auth.uid()::text = id::text);

CREATE POLICY "Users can update their own data" ON public.users
    FOR UPDATE USING (auth.uid()::text = id::text);

CREATE POLICY "Allow user registration" ON public.users
    FOR INSERT WITH CHECK (true);

-- Create policies for product table (public read access)
CREATE POLICY "Anyone can view products" ON public.product
    FOR SELECT USING (true);

CREATE POLICY "Only authenticated users can create products" ON public.product
    FOR INSERT WITH CHECK (auth.role() = 'authenticated');

CREATE POLICY "Only authenticated users can update products" ON public.product
    FOR UPDATE USING (auth.role() = 'authenticated');

-- Create policies for cart table
CREATE POLICY "Users can view their own cart" ON public.cart
    FOR SELECT USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can insert their own cart" ON public.cart
    FOR INSERT WITH CHECK (auth.uid()::text = user_id::text);

CREATE POLICY "Users can update their own cart" ON public.cart
    FOR UPDATE USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can delete their own cart" ON public.cart
    FOR DELETE USING (auth.uid()::text = user_id::text);

-- Create policies for cart_item table
CREATE POLICY "Users can view their own cart items" ON public.cart_item
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.cart 
            WHERE cart.id = cart_item.cart_id 
            AND cart.user_id::text = auth.uid()::text
        )
    );

CREATE POLICY "Users can insert their own cart items" ON public.cart_item
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.cart 
            WHERE cart.id = cart_item.cart_id 
            AND cart.user_id::text = auth.uid()::text
        )
    );

CREATE POLICY "Users can update their own cart items" ON public.cart_item
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM public.cart 
            WHERE cart.id = cart_item.cart_id 
            AND cart.user_id::text = auth.uid()::text
        )
    );

CREATE POLICY "Users can delete their own cart items" ON public.cart_item
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM public.cart 
            WHERE cart.id = cart_item.cart_id 
            AND cart.user_id::text = auth.uid()::text
        )
    );

-- Create policies for orders table
CREATE POLICY "Users can view their own orders" ON public.orders
    FOR SELECT USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can insert their own orders" ON public.orders
    FOR INSERT WITH CHECK (auth.uid()::text = user_id::text);

CREATE POLICY "Users can update their own orders" ON public.orders
    FOR UPDATE USING (auth.uid()::text = user_id::text);

-- Create policies for order_products table
CREATE POLICY "Users can view their own order products" ON public.order_products
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.orders 
            WHERE orders.id = order_products.order_id 
            AND orders.user_id::text = auth.uid()::text
        )
    );

CREATE POLICY "Users can insert their own order products" ON public.order_products
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.orders 
            WHERE orders.id = order_products.order_id 
            AND orders.user_id::text = auth.uid()::text
        )
    );

-- Alternative: If you want to disable RLS temporarily for testing
-- Uncomment the following lines if you want to disable RLS completely:

-- ALTER TABLE public.users DISABLE ROW LEVEL SECURITY;
-- ALTER TABLE public.product DISABLE ROW LEVEL SECURITY;
-- ALTER TABLE public.cart DISABLE ROW LEVEL SECURITY;
-- ALTER TABLE public.cart_item DISABLE ROW LEVEL SECURITY;
-- ALTER TABLE public.orders DISABLE ROW LEVEL SECURITY;
-- ALTER TABLE public.order_products DISABLE ROW LEVEL SECURITY;
