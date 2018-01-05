import os
import shutil
import sys
from build import *

def run(index = -1):
    pattern = '../testcases/%s'
    for _, _, filenames in os.walk('../testcases', False):
        sources = [pattern % filename for filename in filenames]
    sources.sort(key=len)
    build()
    cwd = os.getcwd()
    os.chdir('../bin')
    if os.path.exists('../result'):
        shutil.rmtree('../result')
    os.mkdir('../result')
    if index == -1:
        for source in sources:
            res = os.system('java -cp ../lib/java-cup-11b.jar:../lib/callgraph.jar:../lib/jgraph.jar:. Main ' + source + " 1> /dev/null 2> ../result/" + os.path.basename(source))
            print('{:<10s}: {}'.format(os.path.basename(source), res >> 8))
    else:
        res = os.system('java -cp ../lib/java-cup-11b.jar:../lib/callgraph.jar:../lib/jgraph.jar:. Main ' + sources[index] + " 1> /dev/null 2> ../result/" + os.path.basename(sources[index]))
        print('{:<10s}: {}'.format(os.path.basename(sources[index]), res >> 8))
    os.chdir(cwd)

if __name__ == '__main__':
    if len(sys.argv) == 2:
        run(int(sys.argv[1]))
    else:
        run()