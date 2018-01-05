@echo off
cd src
java -jar ..\lib\java-cup-11b.jar -nosummary -parser Parser -symbols Symbol oberon.cup
cd ..
pause
@echo on