import os

if __name__ == '__main__':
    pattern = '"%s"\t\t\t\t{ return symbol(Type.%s); }'
    with open('types.txt', 'r') as fp:
        types = fp.readlines()
        for type in types:
            type = type.strip()
            if type:
                pair = type.split()
                if len(pair) == 1:
                    print(pattern % (pair[0], pair[0].upper()))
                elif len(pair) == 2:
                    print(pattern % (pair[0], pair[1].upper()))
