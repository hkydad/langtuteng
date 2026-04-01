@echo off
chcp 437 >nul
echo ========================================
echo    Stopping All Services
echo ========================================
echo.

echo Stopping Java (backend)...
taskkill /F /IM java.exe >nul 2>&1
echo Done.

echo Stopping Node (frontend)...
taskkill /F /IM node.exe >nul 2>&1
echo Done.

echo.
echo ========================================
echo    All services stopped!
echo ========================================
pause
