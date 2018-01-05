@echo off
cd bin
for %%s in (..\testcases\*) do (
    java -Djava.ext.dirs=..\lib Main %%s
    pause
)
@echo on