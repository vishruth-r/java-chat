@echo off
:start
echo ================================
echo    🎨 Java GUI Chat Application
echo ================================
echo.
echo Choose an option:
echo [1] 🖥️  Start Server Dashboard
echo [2] 💬 Start Client Interface  
echo [3] 🔧 Compile Application
echo [4] ❌ Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo Starting Server Dashboard...
    java ChatServerGUI
) else if "%choice%"=="2" (
    echo Starting Client Interface...
    java ChatClientGUI
) else if "%choice%"=="3" (
    echo Compiling application...
    javac ChatServerGUI.java ChatClientGUI.java
    if %errorlevel% == 0 (
        echo ✅ Compilation successful!
    ) else (
        echo ❌ Compilation failed!
    )
    pause
    goto start
) else if "%choice%"=="4" (
    echo Goodbye! 👋
    exit
) else (
    echo Invalid choice. Please try again.
    pause
    goto start
)
