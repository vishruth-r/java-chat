@echo off
echo ================================
echo      🔍 Port Availability Checker
echo ================================
echo.
echo Checking common ports for availability...
echo.

echo Testing port 8080...
netstat -an | find ":8080" >nul
if %errorlevel% == 0 (
    echo ❌ Port 8080 is in use
) else (
    echo ✅ Port 8080 is available
)

echo Testing port 8888...
netstat -an | find ":8888" >nul
if %errorlevel% == 0 (
    echo ❌ Port 8888 is in use
) else (
    echo ✅ Port 8888 is available
)

echo Testing port 9090...
netstat -an | find ":9090" >nul
if %errorlevel% == 0 (
    echo ❌ Port 9090 is in use
) else (
    echo ✅ Port 9090 is available
)

echo Testing port 3333...
netstat -an | find ":3333" >nul
if %errorlevel% == 0 (
    echo ❌ Port 3333 is in use
) else (
    echo ✅ Port 3333 is available
)

echo Testing port 4444...
netstat -an | find ":4444" >nul
if %errorlevel% == 0 (
    echo ❌ Port 4444 is in use
) else (
    echo ✅ Port 4444 is available
)

echo.
echo Current application is configured for port 8080
echo Use change-port.bat to switch to a different port if needed.
echo.
pause
