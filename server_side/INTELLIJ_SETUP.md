# IntelliJ IDEA Setup for Supabase Testing

This guide will help you configure IntelliJ IDEA to test your Supabase database connection locally.

## Prerequisites

1. **IntelliJ IDEA** (Community or Ultimate edition)
2. **Java 17** installed and configured
3. **Maven** project properly imported
4. **Supabase project** with database connection details

## Step-by-Step Setup

### 1. Get Your Supabase Connection Details

1. Go to your Supabase project dashboard
2. Navigate to **Settings** → **Database**
3. Find the **Connection string** section
4. Copy the connection string (it should look like: `postgresql://postgres:[password]@[host]:5432/postgres`)

### 2. Configure Environment Variables in IntelliJ

#### Method A: Using Run Configuration (Recommended)

1. **Open Run/Debug Configurations**:
   - Click on the dropdown next to the run button (usually shows "ServerSideApplication")
   - Select "Edit Configurations..."

2. **Find your Spring Boot configuration**:
   - Look for "ServerSideApplication" or create a new Spring Boot configuration
   - Make sure the main class is set to: `com.ecommerce.server_side.ServerSideApplication`

3. **Add Environment Variables**:
   - In the configuration window, find the "Environment variables" section
   - Click the folder icon (📁) to open the environment variables editor
   - Add these variables one by one:

```
DATABASE_URL=jdbc:postgresql://your-supabase-host:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_supabase_password
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-32bytes-minimum
ALLOWED_ORIGINS=http://localhost:5173
```

4. **Save the configuration** (click "Apply" and "OK")

#### Method B: Using VM Options

Alternatively, you can add environment variables as VM options:

1. In the Run Configuration, find "VM options"
2. Add these lines:
```
-DDATABASE_URL=jdbc:postgresql://your-supabase-host:5432/postgres?sslmode=require
-DDATABASE_USERNAME=postgres
-DDATABASE_PASSWORD=your_supabase_password
-DSPRING_PROFILES_ACTIVE=prod
-DJWT_SECRET=your-super-secret-jwt-key-change-this-in-production-32bytes-minimum
-DALLOWED_ORIGINS=http://localhost:5173
```

### 3. Test the Connection

1. **Run the Application**:
   - Click the green "Run" button (▶️) in IntelliJ
   - Or press `Shift + F10`

2. **Check the Console Output**:
   - Look for the environment variables debug output:
   ```
   === Environment Variables Debug ===
   DATABASE_URL: jdbc:postgresql://...
   DATABASE_USERNAME: postgres
   DATABASE_PASSWORD: ***SET***
   JWT_SECRET: ***SET***
   ALLOWED_ORIGINS: http://localhost:5173
   ==================================
   ```

3. **Look for Database Connection Messages**:
   - Successful connection: No database errors, application starts normally
   - Failed connection: Look for errors like:
     - "Network is unreachable"
     - "The connection attempt failed"
     - "'url' must start with 'jdbc'"

### 4. Run the Database Test

You can also run the database connection test directly:

1. **Open the Test Class**:
   - Navigate to `src/test/java/com/ecommerce/server_side/DatabaseConnectionTest.java`

2. **Run the Test**:
   - Right-click on the test method or class
   - Select "Run 'DatabaseConnectionTest.testDatabaseConnection'"

3. **Check Test Results**:
   - Look for the green checkmark (✅) indicating success
   - Or red X (❌) indicating failure

## Example Configuration

Here's what your environment variables should look like (replace with your actual values):

```
DATABASE_URL=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_actual_password_here
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=my-super-secret-jwt-key-32bytes-minimum-12345
ALLOWED_ORIGINS=http://localhost:5173
```

## Troubleshooting

### Common Issues:

1. **"Network is unreachable"**:
   - Check if your IP is whitelisted in Supabase
   - Verify the host and port are correct
   - Try connecting from a different network

2. **"'url' must start with 'jdbc'"**:
   - Make sure your DATABASE_URL starts with `jdbc:postgresql://`
   - Check for any extra quotes or spaces

3. **"Authentication failed"**:
   - Double-check your username and password
   - Ensure you're using the correct Supabase credentials

4. **"SSL connection required"**:
   - Add `?sslmode=require` to your DATABASE_URL

### Debug Steps:

1. **Check Environment Variables**:
   - The application will print environment variables at startup
   - Verify they match your Supabase connection details

2. **Enable Debug Logging**:
   - The application already has debug logging enabled for database connections
   - Look for detailed connection attempts in the console

3. **Test with Different Tools**:
   - Try connecting with pgAdmin or DBeaver using the same credentials
   - This helps isolate if it's a code issue or connection issue

## Next Steps

Once the connection works in IntelliJ:

1. **Test Full Application**: Make sure all features work with the remote database
2. **Update Render Environment Variables**: Use the same values in your Render deployment
3. **Deploy to Render**: The connection should work the same way in production

## Tips

- **Save Multiple Configurations**: You can create different run configurations for local vs Supabase testing
- **Use Environment Variables**: Never hardcode credentials in your code
- **Test Regularly**: Run the connection test before each deployment
- **Keep Credentials Secure**: Don't commit real credentials to version control
