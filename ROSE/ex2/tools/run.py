import os
import shutil
import sys
import type_gen
import build

def run(index = -1):
    type_gen.gen()
    build.build()
    os.chdir('../bin')
    if os.path.exists('../result'):
        shutil.rmtree('../result')
    os.mkdir('../result')
    sources = []
    for _, _, filenames in os.walk('../testcases', False):
        for filename in filenames:
            sources.append('../testcases/' + filename)
    sources.sort(key=lambda x: len(x))
    if index == -1:
        for source in sources:
            res = os.system('java Test ' + source + ' 1> /dev/null 2> ../result/' + os.path.basename(source))
            print(source + ': ' + str(res >> 8))
    else:
        res = os.system('java Test ' + sources[index])
        print(sources[index] + ': ' + str(res >> 8))

if __name__ == '__main__':
    if len(sys.argv) == 2:
        run(int(sys.argv[1]))
    else:
        run()