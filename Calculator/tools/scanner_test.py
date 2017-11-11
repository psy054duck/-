import os

if __name__ == '__main__':
    folders = ''
    os.chdir('../src/parser')
    os.system('javac -cp ../../bin token/*.java -d ../../bin')
    os.system('javac -cp ../../bin production/*.java -d ../../bin')
    os.system('javac -cp ../../bin Scanner.java -d ../../bin')

    os.chdir('../../bin')
    os.system('java parser/Scanner')
