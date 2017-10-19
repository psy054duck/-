import os

root = 'agenda/'
folders = ['../', 'business/', 'presentation/', 'presentation/command/']

if __name__ == '__main__':

    if (not os.path.exists('../bin')):
        os.mkdir('../bin')

    os.chdir('../src')
    for folder in folders:
        os.system('javac ' + root + folder + '*.java -d ../bin')
    os.chdir('../bin')
    os.system('java AgendaService')