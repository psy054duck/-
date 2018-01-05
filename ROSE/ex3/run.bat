@echo off
cd bin
java -Djava.ext.dirs=..\lib Main ../testcases/case1.obr
cd ..
pause
@echo on