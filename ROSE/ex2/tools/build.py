import os

if __name__ == '__main__':
    os.system('python type_gen.py')
    os.chdir('../src')
    os.system('jflex oberon.flex')
    os.system('javac *.java -d ../bin')
