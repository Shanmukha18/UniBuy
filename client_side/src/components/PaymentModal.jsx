import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { paymentAPI, ordersAPI } from '../services/api';
import toast from 'react-hot-toast';
import { XMarkIcon, CreditCardIcon } from '@heroicons/react/24/outline';

const PaymentModal = ({ isOpen, onClose, orderId, amount, onPaymentSuccess }) => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [paymentOrder, setPaymentOrder] = useState(null);

  useEffect(() => {
    if (isOpen && amount) {
      createPaymentOrder();
    }
  }, [isOpen, amount]);

  const createPaymentOrder = async () => {
    setLoading(true);
    try {
      const paymentRequest = {
        userId: user.id,
        amount: amount,
        currency: 'INR',
        receipt: `order_${orderId}_${Date.now()}`,
        notes: `Payment for order ${orderId}`
      };

      const response = await paymentAPI.createOrder(paymentRequest);
      setPaymentOrder(response.data);
    } catch (error) {
      console.error('Error creating payment order:', error);
      toast.error('Failed to create payment order');
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = () => {
    if (!paymentOrder) {
      toast.error('Payment order not created');
      return;
    }

    if (!window.Razorpay) {
      toast.error('Razorpay is not loaded. Please refresh the page.');
      return;
    }

    const options = {
      key: paymentOrder.key,
      amount: paymentOrder.amount * 100, // Convert to paise
      currency: paymentOrder.currency,
      name: paymentOrder.name,
      description: paymentOrder.description,
      order_id: paymentOrder.orderId,
      prefill: {
        email: user.email || '',
        contact: user.phone || ''
      },
      notes: {
        order_id: orderId
      },
      theme: {
        color: paymentOrder.theme
      },
      handler: async function (response) {
        try {
          // Verify payment
          const verificationRequest = {
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
            userId: user.id
          };

          const verificationResponse = await paymentAPI.verifyPayment(verificationRequest);
          
          if (verificationResponse.data) {
            // Update order payment status
            await ordersAPI.updatePaymentStatus(orderId, response.razorpay_payment_id, 'COMPLETED');
            toast.success('Payment successful!');
            onPaymentSuccess();
            onClose();
          } else {
            toast.error('Payment verification failed');
          }
        } catch (error) {
          console.error('Payment verification error:', error);
          toast.error('Payment verification failed');
        }
      },
      modal: {
        ondismiss: function() {
          onClose();
        }
      }
    };

    try {
      const rzp = new window.Razorpay(options);
      rzp.open();
    } catch (error) {
      console.error('Error opening Razorpay:', error);
      toast.error('Failed to open payment gateway');
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold text-gray-900">Complete Payment</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <XMarkIcon className="h-6 w-6" />
          </button>
        </div>

        <div className="mb-6">
          <div className="flex items-center justify-between mb-4">
            <span className="text-gray-600">Order ID:</span>
            <span className="font-medium">#{orderId}</span>
          </div>
          <div className="flex items-center justify-between mb-4">
            <span className="text-gray-600">Amount:</span>
            <span className="font-semibold text-lg text-indigo-600">₹{amount?.toFixed(2)}</span>
          </div>
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-4">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
            <span className="ml-2 text-gray-600">Preparing payment...</span>
          </div>
        ) : (
          <div className="space-y-4">
            <div className="bg-gray-50 rounded-lg p-4">
              <div className="flex items-center">
                <CreditCardIcon className="h-5 w-5 text-gray-400 mr-2" />
                <span className="text-sm text-gray-600">
                  Secure payment powered by Razorpay
                </span>
              </div>
            </div>

            <button
              onClick={handlePayment}
              className="w-full bg-indigo-600 text-white py-3 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 font-medium"
            >
              Pay ₹{amount?.toFixed(2)}
            </button>

            <p className="text-xs text-gray-500 text-center">
              By clicking pay, you agree to our terms and conditions
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentModal;
