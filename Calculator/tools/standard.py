import os

if __name__ == "__main__":
    os.chdir('../bin')
    os.system('java test.ExprEvalTest ..\testcases\standard1.xml')
