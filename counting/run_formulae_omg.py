import re, sys
from subprocess import call, check_output

outputCountRe = re.compile("molecules ([0-9]*)")
outputTimeRe = re.compile("Duration: ([0-9]*) miliseconds")
formulae = list([f.rstrip() for f in open(sys.argv[1])])

CDK_DIR = "../../../cdk/cdk"
DIST = CDK_DIR + "/dist/jar"
OMG_DIR = "../../openmg-code"
CLASSPATH = DIST + "/*:" + CDK_DIR + "/jar/*:" + OMG_DIR + "/src"
for f in formulae:
    result = check_output(['java', '-cp', CLASSPATH, 'org.omg.OMG', '-e', f])
    lines = result.split("\n")
    matchCount = outputCountRe.match(lines[2])
    count = matchCount.group(1)
    matchTime = outputTimeRe.match(lines[3])
    time = matchTime.group(1)
    print f, count, time
call(["rm", "default_out.sdf"])
