import os

def build():
    cur_path = os.curdir
    os.system('python type_gen.py')
    os.chdir('../src')
    os.system('jflex oberon.flex')
    os.system('javac *.java -d ../bin')
    os.chdir(cur_path)

if __name__ == '__main__':
    build()