@echo off
echo === Razorpay Configuration Checker ===
echo.

REM Check if application.properties exists
if not exist "src\main\resources\application.properties" (
    echo ❌ application.properties not found!
    pause
    exit /b 1
)

echo 📁 Checking application.properties...

REM Check for Razorpay configuration
for /f "tokens=2 delims==" %%i in ('findstr "razorpay.key.id" src\main\resources\application.properties') do set KEY_ID=%%i
for /f "tokens=2 delims==" %%i in ('findstr "razorpay.key.secret" src\main\resources\application.properties') do set KEY_SECRET=%%i

echo 🔑 Current Razorpay Key ID: %KEY_ID%
echo 🔐 Current Razorpay Key Secret: %KEY_SECRET:~0,10%...

REM Check if keys are placeholder values
echo %KEY_ID% | findstr /i "YOUR_KEY_ID" >nul
if %errorlevel% equ 0 (
    echo ❌ Razorpay Key ID is not configured (using placeholder)
    echo.
    echo 🔧 To fix this:
    echo 1. Go to https://dashboard.razorpay.com/settings/api-keys
    echo 2. Generate a new key pair for test mode
    echo 3. Update src\main\resources\application.properties:
    echo    razorpay.key.id=rzp_test_YOUR_ACTUAL_KEY_ID
    echo    razorpay.key.secret=YOUR_ACTUAL_SECRET_KEY
    echo.
    echo 4. Restart your Spring Boot application
) else (
    echo ✅ Razorpay Key ID is configured
)

echo %KEY_SECRET% | findstr /i "YOUR_SECRET_KEY" >nul
if %errorlevel% equ 0 (
    echo ❌ Razorpay Key Secret is not configured (using placeholder)
    echo.
    echo 🔧 To fix this:
    echo 1. Go to https://dashboard.razorpay.com/settings/api-keys
    echo 2. Generate a new key pair for test mode
    echo 3. Update src\main\resources\application.properties:
    echo    razorpay.key.id=rzp_test_YOUR_ACTUAL_KEY_ID
    echo    razorpay.key.secret=YOUR_ACTUAL_SECRET_KEY
    echo.
    echo 4. Restart your Spring Boot application
) else (
    echo ✅ Razorpay Key Secret is configured
)

echo.
echo 🌐 To test the configuration, visit:
echo    http://localhost:8081/api/payments/debug-config
echo.
echo 📝 For more help, see: PAYMENT_SETUP.md
pause
