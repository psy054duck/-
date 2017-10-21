import os
import random
import timeit
import numpy as np
import matplotlib.pyplot as plt

# groups = [str(10 ** i) for i in range(1, 4)]
# groups.append('5000')
groups = ['5000']

def build():
    if not os.path.exists('../benchmark'):
        os.mkdir('../benchmark')
    for group in groups:
        if not os.path.exists('../benchmark/' + group):
            os.mkdir('../benchmark/' + group)

    os.system('javac -d ../bin ../src/Postfix.java')
    os.system('javac -d ../bin ../src/PostfixRecursion.java')

def generate():
    for group in groups:
        for filename in range(10):
            with open('../benchmark/' + group + '/' + str(filename), 'w') as fp:
                for i in range(int(group)-1):
                    fp.write(str(random.choice(range(10))))
                    fp.write(random.choice(['+', '-']))
                fp.write(str(random.choice(range(10))))
                fp.write('\n')

def run_recursion(input_file):
    command = 'java PostfixRecursion < ../benchmark/' + input_file
    command += ' > /dev/null'
    os.system(command)

def run_iteration(input_file):
    command = 'java Postfix < ../benchmark/' + input_file
    command += ' > /dev/null'
    os.system(command)

def test_performance():
    iteration_res = []
    recursion_res = []

    for group in groups:
        iteration_t = 0
        recursion_t = 0
        for filename in range(10):
            iteration_t += timeit.timeit("run_iteration('{}/{}')".format(group, filename),
                                         "from __main__ import run_iteration", number=20)
            recursion_t += timeit.timeit("run_recursion('{}/{}')".format(group, filename),
                                         "from __main__ import run_recursion", number=20)
        iteration_res.append(iteration_t / 10)
        recursion_res.append(recursion_t / 10)

    return (iteration_res, recursion_res)

def draw(data1, data2):
    bar_width = 0.3
    plt.ylabel('average time')
    plt.xlabel('number of operands')
    plt.bar(np.arange(len(groups)), data1, bar_width, color='b', label='iteration')
    plt.bar(np.arange(len(groups))+bar_width, data2, bar_width, color='r', label='recursion')
    plt.xticks(np.arange(len(groups))+bar_width/2, groups)
    plt.legend()
    plt.show()

if __name__ == '__main__':
    build()
    generate()
    os.chdir('../bin')

    performance = test_performance()

    print(performance[0])
    print(performance[1])
    print((performance[1][0]-performance[0][0]) / performance[0][0])
    draw(performance[0], performance[1])
