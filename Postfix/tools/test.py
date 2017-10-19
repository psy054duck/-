import os

def testcase(num):
    def func():
        testfile = '../testcases/tc-00' + str(num) + '.infix'
        stdfile = '../testcases/tc-00' + str(num) + '.postfix'
        back = os.popen('java Postfix < ' + testfile)
        res = back.readlines()[1]
        back.close()
        with open(stdfile) as fp:
            std = fp.readline()
        print('Stdandard:\t%s' % std, end='')
        print('Your Answer:\t%s' % res, end='')
    return func

if __name__ == '__main__':
    os.chdir('../bin')
    for i in range(1, 5):
        print(' '*10 + ('case %d' % i))
        print('='*25)
        testcase(i)()
    