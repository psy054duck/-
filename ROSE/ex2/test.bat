@echo off
cd bin
for %%s in (..\testcases\*) do (
    echo %%s
    java Test %%s
    pause
)
cd ..
@echo on