@echo off
chcp 437 >nul
echo ========================================
echo    Football Attendance System Launcher
echo ========================================
echo.

set PROJECT_DIR=D:\ai-work\workspace\langtuteng

echo [1/3] Starting backend...
start "FootballBackend" cmd /k "cd /d %PROJECT_DIR%\backend && java -jar target\attendance-1.0.0.jar"

echo [2/3] Waiting for backend to start...
timeout /t 10 /nobreak >nul

echo [3/3] Starting frontend...
start "FootballFrontend" cmd /k "cd /d %PROJECT_DIR%\frontend && npm run dev"

echo Waiting for frontend to start...
timeout /t 5 /nobreak >nul

echo Opening browser...
start http://localhost:5173

echo.
echo ========================================
echo    Started successfully!
echo    Frontend: http://localhost:5173
echo    Backend:  http://localhost:8080
echo ========================================
pause
