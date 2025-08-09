import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { productsAPI } from '../services/api';
import { useCart } from '../context/CartContext';
import { 
  ShoppingCartIcon, 
  ArrowLeftIcon,
  StarIcon,
  TruckIcon,
  ShieldCheckIcon,
  ArrowPathIcon
} from '@heroicons/react/24/outline';

const ProductDetail = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const { addToCart } = useCart();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const response = await productsAPI.getById(id);
        setProduct(response.data);
      } catch (error) {
        console.error('Error fetching product:', error);
        setError('Failed to load product details');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const handleAddToCart = async () => {
    if (quantity > 0) {
      await addToCart(product.id, quantity);
    }
  };

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity >= 1 && newQuantity <= product.stock) {
      setQuantity(newQuantity);
    }
  };

  // Handle categories - support both old single category and new multiple categories
  const getCategories = () => {
    if (product?.categories && Array.isArray(product.categories)) {
      return product.categories.filter(category => category); // Filter out null/undefined categories
    } else if (product?.category) {
      return [product.category];
    }
    return [];
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Product Not Found</h2>
          <p className="text-gray-600 mb-6">{error || 'The product you are looking for does not exist.'}</p>
          <Link
            to="/products"
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-2" />
            Back to Products
          </Link>
        </div>
      </div>
    );
  }

  const categories = getCategories();

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Breadcrumb */}
        <nav className="mb-8">
          <Link
            to="/products"
            className="inline-flex items-center text-indigo-600 hover:text-indigo-500"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-1" />
            Back to Products
          </Link>
        </nav>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Product Image */}
          <div className="bg-white rounded-lg shadow-sm overflow-hidden">
            {product.imageUrl ? (
              <img
                src={product.imageUrl}
                alt={product.name}
                className="w-full h-96 object-cover"
              />
            ) : (
              <div className="w-full h-96 bg-gray-200 flex items-center justify-center">
                <svg className="w-24 h-24 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd" />
                </svg>
              </div>
            )}
          </div>

          {/* Product Info */}
          <div className="space-y-6">
            {/* Product Header */}
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                {product.name || 'Unnamed Product'}
              </h1>
              {categories.length > 0 && (
                <div className="mb-4">
                  <div className="flex flex-wrap gap-2">
                    {categories.map((category, index) => (
                      <span key={index} className="inline-block bg-indigo-100 text-indigo-800 text-sm px-3 py-1 rounded-full">
                        {category}
                      </span>
                    ))}
                  </div>
                </div>
              )}
              <div className="flex items-center mb-4">
                <div className="flex items-center">
                  {[...Array(5)].map((_, i) => (
                    <StarIcon
                      key={i}
                      className={`h-5 w-5 ${
                        i < 4 ? 'text-yellow-400 fill-current' : 'text-gray-300'
                      }`}
                    />
                  ))}
                </div>
                <span className="ml-2 text-sm text-gray-600">(4.5 out of 5)</span>
              </div>
            </div>

            {/* Price */}
            <div className="flex items-baseline">
              <span className="text-4xl font-bold text-indigo-600">
                ${(product.price || 0).toFixed(2)}
              </span>
              {(product.stock || 0) <= 0 && (
                <span className="ml-4 text-red-600 font-medium">Out of Stock</span>
              )}
            </div>

            {/* Description */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">Description</h3>
              <p className="text-gray-600 leading-relaxed">
                {product.description || 'No description available'}
              </p>
            </div>

            {/* Stock Status */}
            <div className="flex items-center justify-between">
              <span className="text-sm text-gray-600">
                Stock: {product.stock || 0} units available
              </span>
            </div>

            {/* Add to Cart Section */}
            {(product.stock || 0) > 0 && (
              <div className="space-y-4">
                <div className="flex items-center space-x-4">
                  <label className="text-sm font-medium text-gray-700">Quantity:</label>
                  <div className="flex items-center border border-gray-300 rounded-md">
                    <button
                      onClick={() => handleQuantityChange(quantity - 1)}
                      className="px-3 py-1 text-gray-600 hover:text-gray-800"
                      disabled={quantity <= 1}
                    >
                      -
                    </button>
                    <span className="px-4 py-1 border-x border-gray-300">
                      {quantity}
                    </span>
                    <button
                      onClick={() => handleQuantityChange(quantity + 1)}
                      className="px-3 py-1 text-gray-600 hover:text-gray-800"
                      disabled={quantity >= (product.stock || 0)}
                    >
                      +
                    </button>
                  </div>
                </div>

                <button
                  onClick={handleAddToCart}
                  className="w-full flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
                >
                  <ShoppingCartIcon className="h-5 w-5 mr-2" />
                  Add to Cart
                </button>
              </div>
            )}

            {/* Features */}
            <div className="border-t border-gray-200 pt-6">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Features</h3>
              <div className="space-y-3">
                <div className="flex items-center">
                  <TruckIcon className="h-5 w-5 text-green-500 mr-3" />
                  <span className="text-sm text-gray-600">Free shipping on orders over $50</span>
                </div>
                <div className="flex items-center">
                  <ShieldCheckIcon className="h-5 w-5 text-green-500 mr-3" />
                  <span className="text-sm text-gray-600">30-day money-back guarantee</span>
                </div>
                <div className="flex items-center">
                  <ArrowPathIcon className="h-5 w-5 text-green-500 mr-3" />
                  <span className="text-sm text-gray-600">Easy returns and exchanges</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
