@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -cp "lib\mysql-connector-j-9.6.0.jar" -sourcepath src -d out src\com\airline\Main.java
if errorlevel 1 exit /b 1
java -cp "lib\mysql-connector-j-9.6.0.jar;out" com.airline.Main
