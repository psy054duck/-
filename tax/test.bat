@echo off

cd src
javac business/*.java -d ../bin
javac presentation/*.java -d ../bin
javac storage/*.java -d ../bin
javac Main.java -d ../bin
cd ../bin

java Test