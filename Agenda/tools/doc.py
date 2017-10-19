import os

packages = 'agenda'

if __name__ == '__main__':
    os.chdir('../src')
    for package in packages.split():
        os.system('javadoc -subpackages ' + package + ' -d ../doc')
