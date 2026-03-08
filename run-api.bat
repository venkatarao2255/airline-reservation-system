@echo off
cd /d "%~dp0"
if not exist out-api mkdir out-api
javac -cp "lib\mysql-connector-j-9.6.0.jar" -sourcepath src -d out-api src\com\airline\api\ApiMain.java
if errorlevel 1 exit /b 1
java -cp "lib\mysql-connector-j-9.6.0.jar;out-api" com.airline.api.ApiMain

