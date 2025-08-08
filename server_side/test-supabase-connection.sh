#!/bin/bash

echo "=== Supabase Connection Test ==="
echo "This script will test the database connection using environment variables"
echo ""

# Check if environment variables are set
if [ -z "$DATABASE_URL" ]; then
    echo "❌ DATABASE_URL is not set"
    echo "Please set it to your Supabase connection string"
    echo "Example: export DATABASE_URL='postgresql://postgres:[password]@[host]:5432/postgres'"
    exit 1
fi

if [ -z "$DATABASE_USERNAME" ]; then
    echo "❌ DATABASE_USERNAME is not set"
    echo "Please set it to your Supabase username (usually 'postgres')"
    exit 1
fi

if [ -z "$DATABASE_PASSWORD" ]; then
    echo "❌ DATABASE_PASSWORD is not set"
    echo "Please set it to your Supabase password"
    exit 1
fi

echo "✅ Environment variables are set"
echo "DATABASE_URL: $DATABASE_URL"
echo "DATABASE_USERNAME: $DATABASE_USERNAME"
echo "DATABASE_PASSWORD: [hidden]"
echo ""

# Test connection using psql if available
if command -v psql &> /dev/null; then
    echo "Testing connection with psql..."
    
    # Extract host and port from DATABASE_URL
    HOST=$(echo $DATABASE_URL | sed -n 's/.*@\([^:]*\):.*/\1/p')
    PORT=$(echo $DATABASE_URL | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')
    DB_NAME=$(echo $DATABASE_URL | sed -n 's/.*\/\([^?]*\).*/\1/p')
    
    echo "Host: $HOST"
    echo "Port: $PORT"
    echo "Database: $DB_NAME"
    echo ""
    
    # Test connection
    PGPASSWORD=$DATABASE_PASSWORD psql -h $HOST -p $PORT -U $DATABASE_USERNAME -d $DB_NAME -c "SELECT version();" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "✅ Database connection successful!"
    else
        echo "❌ Database connection failed!"
        echo "Please check your connection details"
    fi
else
    echo "⚠️  psql not found. Skipping direct database test."
    echo "You can test the connection by running your Spring Boot application with these environment variables."
fi

echo ""
echo "=== Next Steps ==="
echo "1. If the connection test passed, you can run your Spring Boot application:"
echo "   ./mvnw spring-boot:run"
echo ""
echo "2. Or test with the production profile:"
echo "   SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run"
echo ""
echo "3. Check the application logs for any connection errors"
