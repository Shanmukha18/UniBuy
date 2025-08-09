#!/bin/bash

echo "=== Razorpay Configuration Checker ==="
echo ""

# Check if application.properties exists
if [ ! -f "src/main/resources/application.properties" ]; then
    echo "❌ application.properties not found!"
    exit 1
fi

echo "📁 Checking application.properties..."

# Check for Razorpay configuration
KEY_ID=$(grep "razorpay.key.id" src/main/resources/application.properties | cut -d'=' -f2)
KEY_SECRET=$(grep "razorpay.key.secret" src/main/resources/application.properties | cut -d'=' -f2)

echo "🔑 Current Razorpay Key ID: $KEY_ID"
echo "🔐 Current Razorpay Key Secret: ${KEY_SECRET:0:10}..."

# Check if keys are placeholder values
if [[ "$KEY_ID" == *"YOUR_KEY_ID"* ]] || [[ "$KEY_ID" == *"placeholder"* ]]; then
    echo "❌ Razorpay Key ID is not configured (using placeholder)"
    echo ""
    echo "🔧 To fix this:"
    echo "1. Go to https://dashboard.razorpay.com/settings/api-keys"
    echo "2. Generate a new key pair for test mode"
    echo "3. Update src/main/resources/application.properties:"
    echo "   razorpay.key.id=rzp_test_YOUR_ACTUAL_KEY_ID"
    echo "   razorpay.key.secret=YOUR_ACTUAL_SECRET_KEY"
    echo ""
    echo "4. Restart your Spring Boot application"
else
    echo "✅ Razorpay Key ID is configured"
fi

if [[ "$KEY_SECRET" == *"YOUR_SECRET_KEY"* ]] || [[ "$KEY_SECRET" == *"placeholder"* ]]; then
    echo "❌ Razorpay Key Secret is not configured (using placeholder)"
    echo ""
    echo "🔧 To fix this:"
    echo "1. Go to https://dashboard.razorpay.com/settings/api-keys"
    echo "2. Generate a new key pair for test mode"
    echo "3. Update src/main/resources/application.properties:"
    echo "   razorpay.key.id=rzp_test_YOUR_ACTUAL_KEY_ID"
    echo "   razorpay.key.secret=YOUR_ACTUAL_SECRET_KEY"
    echo ""
    echo "4. Restart your Spring Boot application"
else
    echo "✅ Razorpay Key Secret is configured"
fi

echo ""
echo "🌐 To test the configuration, visit:"
echo "   http://localhost:8081/api/payments/debug-config"
echo ""
echo "📝 For more help, see: PAYMENT_SETUP.md"
