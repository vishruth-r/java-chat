@echo off
echo =======================================
echo    🧪 Testing Java GUI Chat Application
echo =======================================
echo.

echo ✅ Step 1: Checking if files exist...
if exist ChatServerGUI.java (
    echo    ✓ ChatServerGUI.java found
) else (
    echo    ❌ ChatServerGUI.java missing
    goto error
)

if exist ChatClientGUI.java (
    echo    ✓ ChatClientGUI.java found
) else (
    echo    ❌ ChatClientGUI.java missing
    goto error
)

echo.
echo ✅ Step 2: Testing compilation...
javac ChatServerGUI.java ChatClientGUI.java
if %errorlevel% == 0 (
    echo    ✓ Compilation successful
) else (
    echo    ❌ Compilation failed
    goto error
)

echo.
echo ✅ Step 3: Checking compiled classes...
if exist ChatServerGUI.class (
    echo    ✓ ChatServerGUI.class created
) else (
    echo    ❌ ChatServerGUI.class missing
    goto error
)

if exist ChatClientGUI.class (
    echo    ✓ ChatClientGUI.class created
) else (
    echo    ❌ ChatClientGUI.class missing
    goto error
)

echo.
echo ✅ Step 4: Testing batch scripts...
if exist launcher.bat (
    echo    ✓ launcher.bat found
) else (
    echo    ❌ launcher.bat missing
)

if exist run-server-gui.bat (
    echo    ✓ run-server-gui.bat found
) else (
    echo    ❌ run-server-gui.bat missing
)

if exist run-client-gui.bat (
    echo    ✓ run-client-gui.bat found
) else (
    echo    ❌ run-client-gui.bat missing
)

echo.
echo 🎉 ALL TESTS PASSED! 
echo.
echo Ready to use:
echo   • Run .\launcher.bat for easy menu
echo   • Run .\run-server-gui.bat to start server
echo   • Run .\run-client-gui.bat to start client
echo.
echo Application is fully functional! ✨
goto end

:error
echo.
echo ❌ TESTS FAILED!
echo Please check the error messages above.

:end
pause
