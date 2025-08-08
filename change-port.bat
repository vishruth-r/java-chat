@echo off
echo ================================
echo    🔧 Port Configuration Tool
echo ================================
echo.
echo Current port: 8080
echo.
set /p newPort="Enter new port number (1024-65535): "

if "%newPort%"=="" (
    echo No port entered. Keeping current port.
    pause
    exit
)

echo.
echo Updating ChatServerGUI.java...
powershell -Command "(Get-Content ChatServerGUI.java) -replace 'private static final int PORT = \d+;', 'private static final int PORT = %newPort%;' | Set-Content ChatServerGUI.java"

echo Updating ChatClientGUI.java...
powershell -Command "(Get-Content ChatClientGUI.java) -replace 'private static final int SERVER_PORT = \d+;', 'private static final int SERVER_PORT = %newPort%;' | Set-Content ChatClientGUI.java"

echo.
echo Recompiling applications...
javac ChatServerGUI.java ChatClientGUI.java

if %errorlevel% == 0 (
    echo ✅ Port updated successfully to %newPort%!
    echo Applications recompiled and ready to use.
) else (
    echo ❌ Compilation failed. Please check for errors.
)

echo.
pause
