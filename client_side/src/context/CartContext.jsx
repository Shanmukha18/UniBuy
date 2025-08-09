import { createContext, useContext, useState, useEffect } from 'react';
import { cartAPI } from '../services/api';
import { useAuth } from './AuthContext';
import toast from 'react-hot-toast';

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({ items: [] });
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();

  const fetchCart = async () => {
    if (!user) return;
    
    try {
      setLoading(true);
      const userId = user.id;
      const response = await cartAPI.getCart(userId);
      // Ensure the cart has the correct structure
      const cartData = response.data && response.data.items ? response.data : { items: [] };
      setCart(cartData);
    } catch (error) {
      toast.error('Failed to load cart');
      // Set cart to empty structure if API call fails
      setCart({ items: [] });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      fetchCart();
    }
  }, [user]);

  const addToCart = async (productId, quantity = 1) => {
    if (!user) {
      toast.error('Please login to add items to cart');
      return false;
    }

    try {
      setLoading(true);
      const userId = user.id;
      const response = await cartAPI.addToCart(userId, productId, quantity);
      // Ensure the cart has the correct structure
      const cartData = response.data && response.data.items ? response.data : { items: [] };
      setCart(cartData);
      toast.success('Item added to cart!');
      return true;
    } catch (error) {
      toast.error('Failed to add item to cart');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (productId, quantity) => {
    if (!user) return false;

    try {
      setLoading(true);
      const userId = user.id;
      const response = await cartAPI.updateItem(userId, productId, quantity);
      // Ensure the cart has the correct structure
      const cartData = response.data && response.data.items ? response.data : { items: [] };
      setCart(cartData);
      toast.success('Cart updated!');
      return true;
    } catch (error) {
      toast.error('Failed to update cart');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const removeFromCart = async (productId) => {
    if (!user) return false;

    try {
      setLoading(true);
      const userId = user.id;
      const response = await cartAPI.removeItem(userId, productId);
      // Ensure the cart has the correct structure
      const cartData = response.data && response.data.items ? response.data : { items: [] };
      setCart(cartData);
      toast.success('Item removed from cart!');
      return true;
    } catch (error) {
      toast.error('Failed to remove item from cart');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const clearCart = async () => {
    if (!user) return false;

    try {
      setLoading(true);
      const userId = user.id;
      const response = await cartAPI.clearCart(userId);
      // Ensure the cart has the correct structure even if the API returns unexpected data
      const clearedCart = response.data && response.data.items ? response.data : { items: [] };
      setCart(clearedCart);
      toast.success('Cart cleared!');
      return true;
    } catch (error) {
      toast.error('Failed to clear cart');
      // Set cart to empty structure even if API call fails
      setCart({ items: [] });
      return false;
    } finally {
      setLoading(false);
    }
  };

  const getCartTotal = () => {
    return cart.items?.reduce((total, item) => total + ((item.price || 0) * (item.quantity || 0)), 0) || 0;
  };

  const getCartItemCount = () => {
    return cart.items?.reduce((count, item) => count + (item.quantity || 0), 0) || 0;
  };

  const value = {
    cart,
    loading,
    addToCart,
    updateQuantity,
    removeFromCart,
    clearCart,
    getCartTotal,
    getCartItemCount,
    fetchCart
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};
