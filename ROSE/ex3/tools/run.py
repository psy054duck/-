import os
import sys
from build import *

def run(debug = False):
    source = '../testcases/case1.obr'
    build()
    cwd = os.getcwd()
    os.chdir('../bin')
    if debug:
        os.system('java -cp ../lib/java-cup-11b.jar:../lib/callgraph.jar:../lib/jgraph.jar:. Main ' + source + " d")
    else:
        os.system('java -cp ../lib/java-cup-11b.jar:../lib/callgraph.jar:../lib/jgraph.jar:. Main ' + source + " ")
    os.chdir(cwd)

if __name__ == '__main__':
    if len(sys.argv) == 2:
        run(True)
    else:
        run()