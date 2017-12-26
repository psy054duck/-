import os

def gen_parser(grammar = False):
    cwd = os.getcwd()
    os.chdir('../src')
    if grammar:
        os.system('java -jar ../lib/java-cup-11b.jar -nosummary -dump_grammar -parser OberonParser oberon.cup')
    else:
        os.system('java -jar ../lib/java-cup-11b.jar -nosummary -parser OberonParser oberon.cup')
    os.chdir(cwd)

if __name__ == '__main__':
    gen_parser(True)
