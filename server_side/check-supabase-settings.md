# Supabase Connection Troubleshooting for Render Deployment

## Step 1: Check Supabase Database Settings

1. **Go to Supabase Dashboard** → Your Project → Settings → Database
2. **Check these settings**:

### Connection Pooling
- Look for "Connection pooling" settings
- Make sure it's enabled if available
- Note the connection string format

### IP Restrictions
- Check if there are any IP allowlist settings
- If enabled, you may need to add Render's IP ranges
- Or temporarily disable IP restrictions for testing

### SSL Settings
- Verify SSL is required (should be enabled by default)
- Note the SSL mode requirements

## Step 2: Try Different Connection String Formats

### Option 1: Standard with SSL
```
jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:5432/postgres?sslmode=require
```

### Option 2: With SSL Factory
```
jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:5432/postgres?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory
```

### Option 3: With Connection Pooling (if available)
```
jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:6543/postgres?sslmode=require
```
(Note: Port 6543 for connection pooling)

## Step 3: Update Render Environment Variables

In your Render dashboard, update the environment variables:

```
DATABASE_URL=jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:5432/postgres?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=oSFBR43Ll94s8kIS
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=d3chq2bc_JfNVsS
ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app
```

## Step 4: Alternative Solutions

### If still failing, try:

1. **Use Supabase Connection Pooling**:
   - Go to Supabase Dashboard → Settings → Database
   - Enable connection pooling
   - Use the connection pooling URL (usually port 6543)

2. **Check Supabase Status**:
   - Visit https://status.supabase.com
   - Ensure there are no regional outages

3. **Contact Supabase Support**:
   - If the issue persists, it might be a regional connectivity issue
   - Supabase support can help with specific network configurations

## Step 5: Test Connection Locally First

Before deploying to Render, test the connection string locally:

1. **Update your local environment variables** with the new format
2. **Run the application locally** to ensure it works
3. **Only then deploy to Render**

## Common Issues and Solutions

### "Network is unreachable"
- Usually indicates IP restrictions or regional connectivity issues
- Try disabling IP restrictions temporarily
- Check if your Supabase region is accessible from Render's servers

### "SSL connection required"
- Make sure `sslmode=require` is in the connection string
- Try adding `sslfactory=org.postgresql.ssl.NonValidatingFactory`

### "Connection timeout"
- Increase connection timeout in application properties
- Check if Supabase is experiencing high load
