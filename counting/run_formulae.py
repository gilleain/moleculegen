import re, sys
from subprocess import check_output

outputRe = re.compile(r"([0-9]*) structures in ([0-9]*) ms")
formulae = list([f.rstrip() for f in open(sys.argv[1])])

CDK_DIR = "../../cdk/cdk"
DIST = CDK_DIR + "/dist/jar"
CLASSPATH = DIST + "/*:" + CDK_DIR + "/jar/*:bin:lib/*"
for f in formulae:
    result = check_output(['java', '-cp', CLASSPATH, 'app.AMG', '-e', f, '-t'])
    match = outputRe.match(result)
    count, time = match.groups()
    print f, count, time
