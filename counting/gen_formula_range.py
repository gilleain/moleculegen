""" Overly-complex script to generate formulae from a formula pattern and ranges of values.

For example, with an input of "C[n]H[2n]O[m]" "n=3:4,m=2:3" the output should be:

C3H6O2
C4H8O2
C3H6O3
C4H8O3

which is the set of formulae that conform to this pattern and range.

One minor flaw is that all ranges have to be specified, so if one of the variables ('n', 'm', etc) does
not vary it still has to be specified. So "C[n]H[2n]O[m]" "n=3:5,m=3:3" specifies that the number of 
oxygens (m) remains constant at 3, while the carbons vary from 3 to 5.
"""

import sys, re

def cross(*args):
    ans = [[]]
    for arg in args:
        ans = [x+[y] for x in ans for y in arg]
    return ans

def parseRanges(rangeStr):
    vars = rangeStr.split(",")
    rMap = {}
    for var in vars:
        name, rangeStr = var.split("=")
        minX, maxX = [int(x) for x in rangeStr.split(":")]
        rMap[name] = list(range(minX, maxX + 1))
    return rMap

def makeF(varName, multiplier, adder):
    def f(var):
        return (multiplier * var) + adder
    return f

formulaString = sys.argv[1]
rangeString = sys.argv[2]

formula_pattern = re.compile("(.+?)\[(.+?)\]")
symbolVarMap = dict(formula_pattern.findall(formulaString))
#print symbolVarMap

rangeMap = parseRanges(rangeString)
#print rangeMap

single = "^(.)$"
mult = "^([2-9])([a-z])$"
multPlus = "^([2-9])([a-z])\+([1-9])$"
multMinus = "^([2-9])([a-z])-([1-9])$"
pDict = dict([("single", single), ("mult", mult), ("multPlus", multPlus), ("multMinus", multMinus)])
#print pDict
patterns = dict([(n, re.compile(s)) for n, s in pDict.items()])
funcDict = {}
for symbol in symbolVarMap:
    var = symbolVarMap[symbol]
    for patternName in patterns:
        pattern = patterns[patternName]
        match = pattern.search(var)
        if match:
            groups = match.groups()
            #print var, "=", pattern.pattern, groups, patternName
            varName = ""
            multiplier = 1
            adder = 0
            if patternName == "single":
                varName = groups[0]
            elif patternName == "mult":
                multiplier = int(groups[0])
                varName = groups[1]
            elif patternName == "multPlus":
                multiplier = int(groups[0])
                varName = groups[1]
                adder = int(groups[2])
            elif patternName == "multMinus":
                multiplier = int(groups[0])
                varName = groups[1]
                adder = -(int(groups[2]))
            funcDict[symbol] = makeF(varName, multiplier, adder)
            symbolVarMap[symbol] = varName

keys = sorted(rangeMap.keys())
ranges = [rangeMap[x] for x in keys]
#print keys, ranges 
valueSets = cross(*ranges)
#print valueSets

for valueSet in valueSets:
    formula = ""
    for symbol in sorted(symbolVarMap.keys()):
        var = symbolVarMap[symbol]
        value = valueSet[keys.index(var)]
        derivedValue = funcDict[symbol](value)
        formula += "%s%s" % (symbol, derivedValue)
    print formula
