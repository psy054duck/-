import os

if __name__ == '__main__':
    os.chdir('../src')
    os.system('javac tools/Tools.java -d ../bin')
    os.system('javac -classpath ../bin parser/token/*.java -d ../bin')
    os.system('javac -classpath ../bin parser/Scanner.java -d ../bin')
    os.chdir('../bin')
    os.system('java -classpath ../bin parser.Scanner')
