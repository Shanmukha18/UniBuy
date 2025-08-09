# Render Environment Variables Configuration

This document outlines all the environment variables you need to configure in your Render deployment for the Spring Boot e-commerce application.

## Required Environment Variables

### 1. Database Configuration (Supabase)
```
DATABASE_URL=jdbc:postgresql://your-supabase-host:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_supabase_password
```

**Notes:**
- Replace `your-supabase-host` with your actual Supabase database host
- The format should be: `jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres?sslmode=require`
- If you have connection pooling enabled in Supabase, use port `6543` instead of `5432`

### 2. Spring Configuration
```
SPRING_PROFILES_ACTIVE=prod
PORT=8081
```

### 3. JWT Configuration
```
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-32bytes-minimum
```

**Important:** 
- Generate a strong, random secret key (at least 32 characters)
- Never use the default value in production
- Example: `JWT_SECRET=mySuperSecretJWTKeyThatIsAtLeast256BitsLongForSecurity123456789`

### 4. CORS Configuration
```
ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app,https://your-frontend-domain-git-main.vercel.app
```

**Replace with your actual Vercel domains:**
- Main domain: `https://your-app-name.vercel.app`
- Git branch domain: `https://your-app-name-git-main.vercel.app`

### 5. Razorpay Configuration (CRITICAL FOR PAYMENTS)
```
RAZORPAY_KEY_ID=rzp_test_YOUR_ACTUAL_KEY_ID
RAZORPAY_KEY_SECRET=YOUR_ACTUAL_SECRET_KEY
```

**Important Notes for Razorpay:**
- **These are REQUIRED** for payment functionality to work
- You must get these from your Razorpay dashboard
- Test keys start with `rzp_test_`
- Production keys start with `rzp_live_`
- Never use placeholder values in production

## Complete Environment Variables Example

Here's a complete example of what your environment variables should look like in Render:

```
# Database Configuration
DATABASE_URL=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_actual_supabase_password

# Spring Configuration
SPRING_PROFILES_ACTIVE=prod
PORT=8081

# JWT Configuration
JWT_SECRET=mySuperSecretJWTKeyThatIsAtLeast256BitsLongForSecurity123456789

# CORS Configuration
ALLOWED_ORIGINS=https://unibuy-shanmukha-thadavarthis-projects.vercel.app,https://unibuy-git-main-shanmukha-thadavarthis-projects.vercel.app

# Razorpay Configuration (CRITICAL)
RAZORPAY_KEY_ID=rzp_test_1234567890abcdef
RAZORPAY_KEY_SECRET=abcdef1234567890abcdef1234567890
```

## How to Configure in Render

1. **Go to your Render Dashboard**
2. **Select your Spring Boot service**
3. **Navigate to "Environment" tab**
4. **Add each environment variable:**
   - Click "Add Environment Variable"
   - Enter the key (e.g., `RAZORPAY_KEY_ID`)
   - Enter the value (e.g., your actual Razorpay key ID)
   - Click "Save Changes"

## Troubleshooting Payment Issues

### If you're getting 400 errors on payment:

1. **Check Razorpay Configuration:**
   - Verify `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` are set correctly
   - Make sure you're using test keys for testing and live keys for production
   - Ensure the keys are from the correct Razorpay account

2. **Verify Environment Variables:**
   - Check Render logs for "Razorpay is not configured" errors
   - Ensure all environment variables are saved and deployed

3. **Test Razorpay Keys:**
   - Use Razorpay's test mode for development
   - Test with their provided test cards and UPI IDs

4. **Check Application Logs:**
   - Look for specific error messages in Render logs
   - Verify the payment request is being sent correctly

## Important Notes

### Database Connection
- Make sure your Supabase database allows connections from Render's IP addresses
- Test the connection string locally before deploying
- If using connection pooling, use port `6543` instead of `5432`

### Security
- Never commit real credentials to version control
- Use strong, unique passwords and secrets
- Rotate secrets regularly in production

### CORS Configuration
- Include all your Vercel domains (main and branch deployments)
- Test CORS configuration after deployment
- You can add more domains later if needed

### Razorpay Configuration
- **CRITICAL**: These keys are required for payment functionality
- Use test keys for development and testing
- Use live keys for production
- Never use placeholder values

## Testing Your Configuration

After setting up the environment variables:

1. **Deploy your application to Render**
2. **Check the logs** for any connection errors
3. **Test the API endpoints** using your frontend or tools like Postman
4. **Verify CORS** by testing from your Vercel frontend
5. **Test payment functionality** with Razorpay test cards

## Additional Resources

- [Render Environment Variables Documentation](https://render.com/docs/environment-variables)
- [Supabase Connection Guide](https://supabase.com/docs/guides/database/connecting-to-postgres)
- [Spring Boot External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Razorpay API Documentation](https://razorpay.com/docs/api/)
