import os
from gen_parser import *

def build_lexer():
    cwd = os.getcwd()
    os.chdir('../src')
    os.system('jflex oberon.flex')
    os.chdir(cwd)
def build():
    gen_parser()
    build_lexer()
    cwd = os.getcwd()
    os.chdir('../src')
    if not os.path.exists('../bin'):
        os.mkdir('../bin')
    os.system('javac -cp ../lib/java-cup-11b-runtime.jar:../lib/callgraph.jar:. *.java -d ../bin')
    os.chdir(cwd)

if __name__ == '__main__':
    build()
