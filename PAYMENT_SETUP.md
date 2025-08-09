# Razorpay Payment Integration Setup

This guide will help you set up Razorpay payment integration in your e-commerce application.

## Prerequisites

1. Razorpay account (sign up at https://razorpay.com)
2. Test API keys from Razorpay dashboard

## Server-Side Setup

### 1. Update Application Properties

Edit `server_side/src/main/resources/application.properties` and replace the placeholder values with your actual Razorpay keys:

```properties
# Razorpay Configuration
razorpay.key.id=rzp_test_YOUR_ACTUAL_KEY_ID
razorpay.key.secret=YOUR_ACTUAL_SECRET_KEY
```

### 2. Get Your Razorpay Keys

1. Log in to your Razorpay Dashboard
2. Go to Settings → API Keys
3. Generate a new key pair for test mode
4. Copy the Key ID and Key Secret
5. Replace the placeholder values in `application.properties`

### 3. Install Dependencies

The Razorpay Java SDK dependency has been added to `pom.xml`. Run:

```bash
cd server_side
mvn clean install
```

## Client-Side Setup

### 1. Install Razorpay Package

The Razorpay package has been added to `package.json`. Install dependencies:

```bash
cd client_side
npm install
```

### 2. Razorpay Script

The Razorpay checkout script has been added to `index.html`. It will be loaded automatically.

## How It Works

### Payment Flow

1. **User clicks "Proceed to Payment"** in the cart
2. **Order is created** in the database with status "PENDING"
3. **Payment order is created** via Razorpay API
4. **Payment modal opens** with Razorpay checkout
5. **User completes payment** through Razorpay
6. **Payment is verified** on the server
7. **Order status is updated** to "CONFIRMED"
8. **Cart is cleared** and user is redirected to orders page

### API Endpoints

- `POST /api/payments/create-order` - Create Razorpay payment order
- `POST /api/payments/verify` - Verify payment signature
- `PUT /api/orders/{id}/payment` - Update order payment status
- `GET /api/payments/debug-config` - Debug Razorpay configuration

## Testing

### Test Cards

Use these test card numbers for testing:

- **Success**: 4111 1111 1111 1111
- **Failure**: 4000 0000 0000 0002
- **Expiry**: Any future date
- **CVV**: Any 3 digits

### Test UPI

- **Success**: success@razorpay
- **Failure**: failure@razorpay

## Security Notes

1. **Never expose your secret key** in client-side code
2. **Always verify payment signatures** on the server
3. **Use HTTPS** in production
4. **Store sensitive data securely**

## Production Deployment

1. Switch to production Razorpay keys
2. Update CORS settings for your domain
3. Use HTTPS
4. Set up webhook endpoints for payment notifications
5. Implement proper error handling and logging

## Troubleshooting

### Common Issues

1. **Payment verification fails**: Check if secret key is correct
2. **CORS errors**: Ensure CORS is configured properly
3. **Amount mismatch**: Ensure amount is in paise (multiply by 100)
4. **Order not found**: Check if order ID is being passed correctly

### Debug Mode

Enable debug logging in `application.properties`:

```properties
logging.level.com.razorpay=DEBUG
```

### Payment Verification Issues

If you're getting "Payment verification failed" errors:

1. **Check Razorpay Configuration**:
   - Visit `http://localhost:8081/api/payments/debug-config` to see if keys are configured
   - Ensure `razorpayConfigured` is `true`
   - Ensure `secretConfigured` is `true`

2. **Verify Environment Variables**:
   - Make sure `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` are set
   - Check that the keys are not placeholder values

3. **Check Application Properties**:
   - Ensure `razorpay.key.id` and `razorpay.key.secret` are set correctly
   - Restart the application after changing properties

4. **Common Fixes**:
   ```bash
   # Set environment variables (Linux/Mac)
   export RAZORPAY_KEY_ID=rzp_test_YOUR_ACTUAL_KEY_ID
   export RAZORPAY_KEY_SECRET=YOUR_ACTUAL_SECRET_KEY
   
   # Or update application.properties directly
   razorpay.key.id=rzp_test_YOUR_ACTUAL_KEY_ID
   razorpay.key.secret=YOUR_ACTUAL_SECRET_KEY
   ```

5. **Test the Configuration**:
   - Restart your Spring Boot application
   - Check the logs for "Razorpay configuration found" message
   - Try a test payment with the debug endpoint

### Order Status Issues

If orders remain in "PENDING" status:

1. **Check Payment Verification**:
   - Look for "Payment verification failed" in logs
   - Ensure signature verification is working

2. **Check Order Update**:
   - Verify the `updatePaymentStatus` endpoint is being called
   - Check if the order status is being updated to "CONFIRMED"

3. **Database Issues**:
   - Ensure the `orders` table has the required payment fields
   - Check if the `paymentStatus` and `razorpayPaymentId` are being saved

## Support

For Razorpay-specific issues, refer to:
- [Razorpay Documentation](https://razorpay.com/docs/)
- [Razorpay Support](https://razorpay.com/support/)
