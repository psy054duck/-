import os
from gen_scanner import *

def build():
    cwd = os.getcwd()
    os.chdir('../src')
    if not os.path.exists('../bin'):
        os.mkdir('../bin')
    gen_scanner()
    os.system('javac -cp ../lib/flowchart.jar:../lib/callgraph.jar:. *.java -d ../bin')
    os.chdir(cwd)

if __name__ == '__main__':
    build()
