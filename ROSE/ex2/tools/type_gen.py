import os

if __name__ == '__main__':
    declaration_pattern = 'public static final int %s = %d;'
    program = 'public class Type {'

    with open('../src/Type.java', 'w') as fp:
        with open('types.txt', 'r') as fp2:
            types = fp2.readlines()
        i = 0
        for type in types:
            type = type.strip()
            if type:
                pair = type.split()

                program += '\n\t'
                if len(pair) == 2:
                    program += declaration_pattern % (pair[1].upper(), i)               
                    i += 1
                elif len(pair) == 1:
                    program += declaration_pattern % (pair[0].upper(), i)
                    i += 1
        program += '\n}\n'
        fp.write(program)
        