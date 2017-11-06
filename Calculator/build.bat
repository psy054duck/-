@echo off
cd src
javac -d ..\bin -classpath ..\bin parser\*.java
javac -d ..\bin -classpath ..\bin tools\*.java
cd ..
pause
@echo on
