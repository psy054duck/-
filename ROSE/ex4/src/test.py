import re

grammar = {}
First = {}
Follow = {}
start = ''

def isTerminal(symbol):
    return not symbol in grammar.keys()

def first(symbol):
    if isTerminal(symbol):
        return {symbol}

    if symbol in First:
        return First[symbol]

    tmp = First.setdefault(symbol, set())
    
    for body in grammar[symbol]:
        for body_symbol in body.split():
            if isTerminal(body_symbol):
                tmp.add(body_symbol)
                break
            else:
                tmp |= first(body_symbol)
                tmp.discard('')
                if not '' in first(body_symbol):
                    break
        else:
            tmp.add('')
    if '' in grammar[symbol]:
        tmp.add('')
    return First[symbol]

def first_list(symbol_list):
    res = set()
    for symbol in symbol_list:
        res |= first(symbol)
        if not '' in first(symbol):
            break
    else:
        res.discard('')
        if len(symbol_list) > 0 and '' in first(symbol_list[-1]):
            res.add('')
        return res
    res.discard('')
    return res

def countFollow():
    cnt = 0
    for s in Follow.values():
        cnt += len(s)
    return cnt

def follow():
    Follow[start] = {'$'}
    while True:
        cnt = countFollow()
        for head, bodies in grammar.items():
            for body in bodies:
                symbols = body.split()
                for i, symbol in enumerate(symbols):
                    if not isTerminal(symbol):
                        j = i + 1
                        while j < len(symbols):
                            s = Follow.setdefault(symbol, set())
                            s |= first(symbols[j])
                            if not '' in first(symbols[j]):
                                break
                            j += 1
                        if j == len(symbols):
                            s = Follow.setdefault(symbol, set())
                            s |= Follow.setdefault(head, set())
        if cnt == countFollow():
            break
    for s in Follow.values():
        s.discard('')
    
def isLL1():
    for head in grammar.keys():
        first(head)
    follow()

    for head, bodies in grammar.items():
        for alpha, beta in ((a, b) for a in bodies for b in bodies):
            if alpha != beta:
                if len(first_list(alpha.split()) & first_list(beta.split())) != 0:
                    print('Error: %s' % head)
                    print('{:50}{}'.format(alpha, first_list(alpha.split())))
                    print('{:50}{}'.format(beta, first_list(beta.split())))
                    return False
                if '' in first_list(beta):
                    if len(first_list(alpha) & Follow[head]) != 0:
                        print('Error: %s' % head)
                        print('{:50}{}'.format(alpha, first_list(alpha.split())))
                        print('{:50}{}'.format(beta, first_list(beta.split())))
                        return False
    return True

def convert(line):
    global start
    dirty_pattern = '\[\d*\]'

    line = re.sub(dirty_pattern, '', line).strip()

    head, body = line.split('::=')
    grammar.setdefault(head.strip(), set()).add(body.strip())
    if start == '':
        start = head.strip()

def genProcedure():
    for head, bodies in grammar.items():
        print('private String %s() throws Exception {' % head)
        print('\tString res = "";')
        if len(bodies) == 1:
            for body in bodies:
                for symbol in body.split():
                    if isTerminal(symbol):
                        print('\tres += match(sym.%s);' % symbol)
                    else:
                        print('\tres += %s();' % symbol)
        else:
            isfirst = True
            for i, body in enumerate(bodies):
                symbols = body.split()
                if body == '':
                    continue
                if isfirst:
                    if isTerminal(symbols[0]):
                        print('\tif (lookahead.getType() == sym.%s) {' % symbols[0])
                    else:
                        print('\tif (getFirst("%s").contains(sym.terminalNames[lookahead.getType()])) {' % symbols[0])
                    isfirst = False
                elif i == len(bodies)-1 and not '' in bodies:
                    print(' else {')
                else:
                    if isTerminal(symbols[0]):
                        print(' else if (lookahead.getType() == sym.%s) {' % symbols[0])
                    else:
                        print(' else if (getFirst("%s").contains(sym.terminalNames[lookahead.getType()])) {' % symbols[0])

                for symbol in symbols:
                    if isTerminal(symbol):
                        print('\t\tres += match(sym.%s);' % symbol)
                    else:
                        print('\t\tres += %s();' % symbol)
                print('\t}', end='')

        print('\n\treturn res;')
        print('}\n')

if __name__ == '__main__':
    with open('grammarTest') as fp:
        for line in fp:
            convert(line)

    for nonTerminal in grammar.keys():
        print('{:25}{}'.format(nonTerminal, first(nonTerminal)))

    # print('*' * 90)
    follow()
    for nonTerminal in grammar.keys():
        print('{:25}{}'.format(nonTerminal, Follow[nonTerminal]))
    # genProcedure()
    print(isLL1())
