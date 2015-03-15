import glob, os, subprocess, sys

cdk_dir = "../../chemistry/cdk"
classpath = "bin:lib/*:" + ":".join([path for path in glob.glob(cdk_dir + "/*/*/target/classes")])
classpath += ":/Users/maclean/.m2/repository/java3d/vecmath/1.5.2/vecmath-1.5.2.jar"
classpath += ":/Users/maclean/.m2/repository/uk/ac/ebi/beam/beam-core/0.8/beam-core-0.8.jar"
classpath += ":/Users/maclean/.m2/repository/uk/ac/ebi/beam/beam-func/0.8/beam-func-0.8.jar"
classpath += ":/Users/maclean/.m2/repository/com/google/collections/google-collections/1.0/google-collections-1.0.jar"

# the file of formula-count pairs
input_file = sys.argv[1]
limit = int(sys.argv[2])
f_dict = {}
line_number = 0
for line in open(input_file):
	line_number += 1
	if line_number < 2: continue
	if line_number > limit: break
	formula, count = line.split()
	f_dict[formula] = int(count.replace(",", ""))
print f_dict
for formula in f_dict:
	expected = f_dict[formula]
	obs = subprocess.check_output(["java", "-cp", classpath, "app.AMG", "-e", formula, "-t"])
	try:
		observed = int(obs.split()[0])
		time = int(obs.split()[3])
		#print expected == observed, formula, expected, observed, time
		print formula, observed, time
	except Exception:
		pass
