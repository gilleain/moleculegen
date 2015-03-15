import sys
import matplotlib.pyplot as plt

def avg(l):
	return float(sum(l)) / float(len(l))

values = {}
for line in open(sys.argv[1]):
	count, time = line.split()
	if count in values:
		values[count].append(int(time))
	else:
		values[count] = [int(time)]
avgd = {}
for k in values:
	avgd[k] = avg(values[k])
print avgd
plt.scatter(avgd.keys(), avgd.values())
plt.show()
