import os

if __name__ == '__main__':
    os.chdir('../src')
    os.system('javac tools/Tools.java -d ../bin')
    os.chdir('../src/parser')
    os.system('javac -classpath ../../bin Calculator.java -d ../../bin')
