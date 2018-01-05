import os

def gen_scanner():
    cwd = os.getcwd()
    os.chdir('../src')
    os.system('jflex oberon.flex')
    os.chdir(cwd)

if __name__ == '__main__':
    gen_scanner()
