import os

packages = 'business storage presentation'

if __name__ == '__main__':
    os.chdir('../src');
    os.system('javadoc ' + packages + ' -d ../doc')
