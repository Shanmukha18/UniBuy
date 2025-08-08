@echo off
echo === Supabase Environment Setup ===
echo This script will help you set up environment variables for local testing
echo.

set /p DATABASE_URL="Enter your Supabase DATABASE_URL: "
set /p DATABASE_USERNAME="Enter your Supabase username [postgres]: "
if "%DATABASE_USERNAME%"=="" set DATABASE_USERNAME=postgres
set /p DATABASE_PASSWORD="Enter your Supabase password: "

set SPRING_PROFILES_ACTIVE=prod
set JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-32bytes-minimum
set ALLOWED_ORIGINS=http://localhost:5173

echo.
echo ✅ Environment variables set:
echo DATABASE_URL: %DATABASE_URL%
echo DATABASE_USERNAME: %DATABASE_USERNAME%
echo DATABASE_PASSWORD: [hidden]
echo SPRING_PROFILES_ACTIVE: %SPRING_PROFILES_ACTIVE%
echo JWT_SECRET: [hidden]
echo ALLOWED_ORIGINS: %ALLOWED_ORIGINS%
echo.

echo === Next Steps ===
echo 1. Test the connection by running your Spring Boot application:
echo    mvnw.cmd spring-boot:run
echo.
echo 2. Or run the database test:
echo    mvnw.cmd test -Dtest=DatabaseConnectionTest
echo.
echo 3. Check the application logs for any connection errors
echo.
echo Note: These environment variables are only set for this terminal session.
echo For permanent setup, add them to your system environment variables.
pause
