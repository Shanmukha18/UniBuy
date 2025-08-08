#!/bin/bash

echo "=== Supabase Environment Setup ==="
echo "This script will help you set up environment variables for local testing"
echo ""

# Function to prompt for input with default value
prompt_with_default() {
    local prompt="$1"
    local default="$2"
    local var_name="$3"
    
    if [ -n "$default" ]; then
        read -p "$prompt [$default]: " input
        if [ -z "$input" ]; then
            input="$default"
        fi
    else
        read -p "$prompt: " input
    fi
    
    export "$var_name=$input"
}

echo "Please enter your Supabase database connection details:"
echo ""

# Get Supabase connection details
prompt_with_default "Enter your Supabase DATABASE_URL" "" "DATABASE_URL"
prompt_with_default "Enter your Supabase username" "postgres" "DATABASE_USERNAME"
prompt_with_default "Enter your Supabase password" "" "DATABASE_PASSWORD"

# Set other required environment variables
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET="your-super-secret-jwt-key-change-this-in-production-32bytes-minimum"
export ALLOWED_ORIGINS="http://localhost:5173"

echo ""
echo "✅ Environment variables set:"
echo "DATABASE_URL: $DATABASE_URL"
echo "DATABASE_USERNAME: $DATABASE_USERNAME"
echo "DATABASE_PASSWORD: [hidden]"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "JWT_SECRET: [hidden]"
echo "ALLOWED_ORIGINS: $ALLOWED_ORIGINS"
echo ""

echo "=== Next Steps ==="
echo "1. Test the connection:"
echo "   ./test-supabase-connection.sh"
echo ""
echo "2. Run the application:"
echo "   ./mvnw spring-boot:run"
echo ""
echo "3. Or run the database test:"
echo "   ./mvnw test -Dtest=DatabaseConnectionTest"
echo ""
echo "Note: These environment variables are only set for this terminal session."
echo "For permanent setup, add them to your shell profile (.bashrc, .zshrc, etc.)"
