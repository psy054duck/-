import os

folders = ['./', 'business/', 'presentation/', 'storage/']

if __name__ == '__main__':

    if (not os.path.exists('../bin')):
        os.mkdir('../bin')

    os.chdir('../src')
    for folder in folders:
        os.system('javac ' + folder + '*.java -d ../bin')
    os.chdir('../bin')
    os.system('java Test')