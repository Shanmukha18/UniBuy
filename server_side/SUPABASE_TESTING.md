# Supabase Database Connection Testing

This guide will help you test your Supabase PostgreSQL database connection locally before deploying to Render.

## Prerequisites

1. **Supabase Project**: You should have a Supabase project set up with a PostgreSQL database
2. **Connection Details**: Your Supabase database connection string, username, and password
3. **Java 17**: Make sure you have Java 17 installed
4. **Maven**: The project uses Maven for building and testing

## Getting Supabase Connection Details

1. Go to your Supabase project dashboard
2. Navigate to **Settings** → **Database**
3. Find the **Connection string** section
4. Copy the connection string (it should look like: `postgresql://postgres:[password]@[host]:5432/postgres`)

## Method 1: Using the Setup Script (Recommended)

### For Windows:
```bash
# Run the setup script
setup-supabase-env.bat
```

### For Linux/Mac:
```bash
# Make the script executable (if not already done)
chmod +x setup-supabase-env.sh

# Run the setup script
./setup-supabase-env.sh
```

The script will prompt you for:
- `DATABASE_URL`: Your Supabase connection string
- `DATABASE_USERNAME`: Usually "postgres"
- `DATABASE_PASSWORD`: Your Supabase database password

## Method 2: Manual Environment Variable Setup

Set these environment variables in your terminal:

### Windows (PowerShell):
```powershell
$env:DATABASE_URL="postgresql://postgres:[password]@[host]:5432/postgres"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="your_password"
$env:SPRING_PROFILES_ACTIVE="prod"
$env:JWT_SECRET="your-super-secret-jwt-key-change-this-in-production-32bytes-minimum"
$env:ALLOWED_ORIGINS="http://localhost:5173"
```

### Linux/Mac:
```bash
export DATABASE_URL="postgresql://postgres:[password]@[host]:5432/postgres"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="your_password"
export SPRING_PROFILES_ACTIVE="prod"
export JWT_SECRET="your-super-secret-jwt-key-change-this-in-production-32bytes-minimum"
export ALLOWED_ORIGINS="http://localhost:5173"
```

## Testing the Connection

### Option 1: Run the Application
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Option 2: Run the Database Test
```bash
# Windows
mvnw.cmd test -Dtest=DatabaseConnectionTest

# Linux/Mac
./mvnw test -Dtest=DatabaseConnectionTest
```

### Option 3: Test with psql (if available)
```bash
# Extract connection details from DATABASE_URL
# Then run:
psql -h [host] -p 5432 -U postgres -d postgres
```

## Expected Results

### Successful Connection:
- Application starts without database errors
- You should see logs like:
  ```
  ✅ Database connection successful!
  Database: PostgreSQL
  Version: [version number]
  ```

### Failed Connection:
- You might see errors like:
  ```
  ❌ Database connection failed!
  ERROR: Network is unreachable
  ERROR: The connection attempt failed
  ERROR: 'url' must start with "jdbc"
  ```

## Common Issues and Solutions

### 1. "Network is unreachable"
- **Cause**: Firewall blocking the connection or incorrect host/port
- **Solution**: 
  - Check if the host and port are correct
  - Ensure your IP is whitelisted in Supabase
  - Try connecting from a different network

### 2. "'url' must start with 'jdbc'"
- **Cause**: DATABASE_URL format is incorrect
- **Solution**: Make sure the URL starts with `jdbc:postgresql://`

### 3. "Authentication failed"
- **Cause**: Wrong username/password
- **Solution**: Double-check your Supabase credentials

### 4. "SSL connection required"
- **Cause**: Supabase requires SSL connections
- **Solution**: Add `?sslmode=require` to your DATABASE_URL

## Example DATABASE_URL Format

```
jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres?sslmode=require
```

## Next Steps

Once the connection is working locally:

1. **Update Render Environment Variables**: Use the same values in your Render deployment
2. **Test Full Application**: Make sure all features work with the remote database
3. **Deploy to Render**: The connection should work the same way in production

## Troubleshooting

If you're still having issues:

1. **Check Supabase Dashboard**: Ensure your database is active and accessible
2. **Verify IP Whitelist**: Make sure your IP is allowed in Supabase
3. **Test with Different Tools**: Try connecting with pgAdmin or DBeaver
4. **Check Logs**: Look for detailed error messages in the application logs

## Security Notes

- Never commit your actual database credentials to version control
- Use environment variables for all sensitive information
- Consider using Supabase's connection pooling for production
- Regularly rotate your database passwords
