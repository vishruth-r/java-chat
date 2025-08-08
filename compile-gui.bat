@echo off
echo Compiling GUI Chat Application...
javac ChatServerGUI.java ChatClientGUI.java
if %errorlevel% == 0 (
    echo GUI compilation successful!
) else (
    echo GUI compilation failed!
    pause
)
