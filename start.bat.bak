@echo off
chcp 65001 >nul
echo ========================================
echo    User Management System Launcher
echo ========================================
echo.

set PROJECT_DIR=D:\ai-work\ltt

echo [1/3] Starting backend (SpringBoot)...
start "SpringBoot" cmd /k "cd /d %PROJECT_DIR%\backend && mvn spring-boot:run"

echo [2/3] Waiting for backend to start...
timeout /t 15 /nobreak >nul

echo [3/3] Starting frontend (Vue)...
start "Vue Frontend" cmd /k "cd /d %PROJECT_DIR%\frontend && npm run dev"

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
