import os

def build():
    cwd = os.getcwd()
    os.chdir('../src')
    if not os.path.exists('../bin'):
        os.mkdir('../bin')
    os.system('jflex oberon.flex')
    os.system('javac -cp ../lib/flowchart.jar:../lib/callgraph.jar:. *.java -d ../bin')
    os.chdir(cwd)

if __name__ == '__main__':
    build()
