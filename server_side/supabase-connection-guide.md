# Supabase Connection Fix for Render Deployment

## Current Issue
"Network is unreachable" error when deploying to Render - Render's servers cannot connect to your Supabase database.

## Step-by-Step Solution

### Step 1: Check Supabase Connection Pooling

1. **Go to Supabase Dashboard** → Your Project → Settings → Database
2. **Look for "Connection pooling"** section
3. **If available, enable it** and note the connection string

### Step 2: Try Connection Pooling URL

If connection pooling is available, use this format in Render:

```
DATABASE_URL=jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:6543/postgres?sslmode=require
```

Note: Port 6543 instead of 5432

### Step 3: Check Supabase IP Restrictions

1. **Go to Supabase Dashboard** → Settings → Database
2. **Look for "IP allowlist" or "Network restrictions"**
3. **If enabled, either:**
   - Disable it temporarily for testing
   - Add Render's IP ranges (you may need to contact Supabase support for this)

### Step 4: Try Different SSL Configurations

Try these DATABASE_URL formats in order:

#### Option 1: Standard SSL
```
jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:5432/postgres?sslmode=require
```

#### Option 2: SSL with Non-Validating Factory
```
jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:5432/postgres?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory
```

#### Option 3: SSL with Trust Server Certificate
```
jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:5432/postgres?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=true&sslmode=require
```

### Step 5: Check Supabase Region

1. **Go to Supabase Dashboard** → Settings → General
2. **Note your project's region**
3. **Check if there are any regional connectivity issues**

### Step 6: Alternative Solutions

If the above doesn't work:

#### Option A: Use Supabase Connection Pooling (Recommended)
- Enable connection pooling in Supabase
- Use port 6543 in your connection string
- This often resolves connectivity issues

#### Option B: Contact Supabase Support
- The issue might be regional connectivity
- Supabase support can help with specific network configurations

#### Option C: Consider Different Database
- If the issue persists, consider using a different database provider
- Options: Railway PostgreSQL, PlanetScale, or AWS RDS

### Step 7: Test Locally First

Before deploying to Render:
1. Test the connection string locally in IntelliJ
2. Make sure it works with your local environment
3. Only then deploy to Render

## Environment Variables for Render

Update your Render environment variables with the working connection string:

```
DATABASE_URL=jdbc:postgresql://db.pmtsqcgvlirxpylcdekn.supabase.co:6543/postgres?sslmode=require
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=oSFBR43Ll94s8kIS
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=d3chq2bc_JfNVsS
ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app
```

## Troubleshooting Commands

### Check if RLS is disabled:
```sql
SELECT schemaname, tablename, rowsecurity 
FROM pg_tables 
WHERE schemaname = 'public';
```

### Test connection from Supabase SQL Editor:
```sql
SELECT version();
```

## Next Steps

1. Try connection pooling first (most likely solution)
2. If that doesn't work, try the different SSL configurations
3. If still failing, contact Supabase support
4. Consider alternative database providers if needed
