import os

if __name__ == '__main__':
    folders = ''
    os.chdir('../src/parser')
    os.system('javac -cp ../../bin token/*.java -d ../../bin')
    os.system('javac -cp ../../bin production/*.java -d ../../bin')
    os.system('javac -cp ../../bin *.java -d ../../bin -Xlint:unchecked')

    os.chdir('../../bin')
    os.system('java parser/Parser')
