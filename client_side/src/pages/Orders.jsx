import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { ordersAPI } from '../services/api';
import { 
  ClockIcon, 
  CheckCircleIcon, 
  XCircleIcon,
  TruckIcon,
  ShoppingBagIcon
} from '@heroicons/react/24/outline';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    const fetchOrders = async () => {
      if (!user?.id) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const response = await ordersAPI.getByUser(user.id);
        // Ensure orders is always an array
        const ordersData = Array.isArray(response.data) ? response.data : [];
        setOrders(ordersData);
      } catch (error) {
        console.error('Failed to load orders:', error);
        toast.error('Failed to load orders');
        setOrders([]);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [user]);

  const getStatusIcon = (status) => {
    if (!status) return <ShoppingBagIcon className="h-5 w-5 text-gray-500" />;
    
    switch (status.toUpperCase()) {
      case 'PENDING':
        return <ClockIcon className="h-5 w-5 text-yellow-500" />;
      case 'CONFIRMED':
        return <CheckCircleIcon className="h-5 w-5 text-blue-500" />;
      case 'SHIPPED':
        return <TruckIcon className="h-5 w-5 text-indigo-500" />;
      case 'DELIVERED':
        return <CheckCircleIcon className="h-5 w-5 text-green-500" />;
      case 'CANCELLED':
        return <XCircleIcon className="h-5 w-5 text-red-500" />;
      default:
        return <ShoppingBagIcon className="h-5 w-5 text-gray-500" />;
    }
  };

  const getStatusColor = (status) => {
    if (!status) return 'bg-gray-100 text-gray-800';
    
    switch (status.toUpperCase()) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'CONFIRMED':
        return 'bg-blue-100 text-blue-800';
      case 'SHIPPED':
        return 'bg-indigo-100 text-indigo-800';
      case 'DELIVERED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      console.error('Error formatting date:', error);
      return 'Invalid Date';
    }
  };

  const formatPrice = (price) => {
    if (!price || isNaN(price)) return '₹0.00';
    
    try {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
      }).format(price);
    } catch (error) {
      console.error('Error formatting price:', error);
      return '₹0.00';
    }
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-gray-400 mb-4">
            <ShoppingBagIcon className="mx-auto h-16 w-16" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">Please login to view orders</h3>
          <p className="text-gray-600 mb-4">
            You need to be logged in to see your order history.
          </p>
          <Link
            to="/login"
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Login
          </Link>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">My Orders</h1>
          <p className="text-gray-600">Track your order history and status</p>
        </div>

        {!orders || orders.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-gray-400 mb-4">
              <ShoppingBagIcon className="mx-auto h-16 w-16" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No orders yet</h3>
            <p className="text-gray-600">
              You haven't placed any orders yet. Start shopping to see your orders here.
            </p>
          </div>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order?.id || Math.random()} className="bg-white rounded-lg shadow-sm overflow-hidden">
                {/* Order Header */}
                <div className="px-6 py-4 border-b border-gray-200">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                      <div className="flex items-center space-x-2">
                        {getStatusIcon(order?.status)}
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(order?.status)}`}>
                          {order?.status || 'UNKNOWN'}
                        </span>
                      </div>
                      <div>
                        <p className="text-sm text-gray-600">Order #{order?.id || 'N/A'}</p>
                        <p className="text-sm text-gray-500">
                          Placed on {formatDate(order?.orderDate)}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-sm font-medium text-gray-900">
                        {order?.orderItems?.length || 0} items
                      </p>
                      {order?.totalAmount && (
                        <p className="text-sm text-gray-600">
                          Total: {formatPrice(order.totalAmount)}
                        </p>
                      )}
                    </div>
                  </div>
                </div>

                {/* Order Details */}
                <div className="px-6 py-4">
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm mb-4">
                    <div>
                      <p className="text-gray-600">Order ID</p>
                      <p className="font-medium text-gray-900">#{order?.id || 'N/A'}</p>
                    </div>
                    <div>
                      <p className="text-gray-600">Order Date</p>
                      <p className="font-medium text-gray-900">
                        {formatDate(order?.orderDate)}
                      </p>
                    </div>
                    <div>
                      <p className="text-gray-600">Status</p>
                      <div className="flex items-center space-x-2">
                        {getStatusIcon(order?.status)}
                        <span className="font-medium text-gray-900">
                          {order?.status || 'UNKNOWN'}
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Product Details */}
                  {order?.orderItems && order.orderItems.length > 0 && (
                    <div className="mt-4 pt-4 border-t border-gray-200">
                      <p className="text-sm font-medium text-gray-900 mb-3">Ordered Products:</p>
                      <div className="space-y-3">
                        {order.orderItems.map((orderItem) => (
                          <div key={orderItem?.id || Math.random()} className="flex items-center space-x-4 p-3 bg-gray-50 rounded-lg">
                            {/* Product Image */}
                            <div className="flex-shrink-0">
                              <img
                                src={orderItem?.productImageUrl || 'https://via.placeholder.com/60x60?text=Product'}
                                alt={orderItem?.productName || 'Product'}
                                className="h-16 w-16 object-cover rounded-md"
                                onError={(e) => {
                                  e.target.src = 'https://via.placeholder.com/60x60?text=Product';
                                }}
                              />
                            </div>
                            
                            {/* Product Details */}
                            <div className="flex-1 min-w-0">
                              <p className="text-sm font-medium text-gray-900 truncate">
                                {orderItem?.productName || 'Unknown Product'}
                              </p>
                              <p className="text-sm text-gray-500">
                                Quantity: {orderItem?.quantity || 0}
                              </p>
                              <p className="text-sm font-medium text-indigo-600">
                                {formatPrice(orderItem?.productPrice)}
                              </p>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Orders;
