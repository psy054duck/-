import os
import type_gen
import build

def run():
    type_gen.gen()
    build.build()
    os.chdir('../bin')
    os.system('java Test ../testcases/case1.obr')

if __name__ == '__main__':
    run()