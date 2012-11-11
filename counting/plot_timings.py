from matplotlib import pyplot as plt
import sys

def readData(filename):
    formulae = []
    counts = []
    times = []
    fh = open(filename)
    for line in fh:
        formula, count, time = line.rstrip().split()
        formulae.append(formula)
        counts.append(int(count))
        times.append(int(time))
    fh.close()
    return (formulae, counts, times)

AMG_data_file = sys.argv[1]
OMG_data_file = sys.argv[2]
use_log = sys.argv[3]
output_filename = sys.argv[4]
title = sys.argv[5]

f_AMG, c_AMG, t_AMG = readData(AMG_data_file)
f_OMG, c_OMG, t_OMG = readData(OMG_data_file)

line_format_AMG = 'b-'
line_format_OMG = 'r-'

# TODO : check that f_AMG = f_OMG...
fig = plt.figure()
ax = fig.add_subplot(111)
ax.set_title(title)
if use_log == "log":
    ax.set_yscale("log")
indices = list(range(0, len(f_AMG)))
ax.plot(indices, t_AMG, line_format_AMG, label="AMG")
ax.plot(indices, t_OMG, line_format_OMG, label="OMG")
ax.set_xticklabels(f_AMG)
ax.legend()
fig.savefig(output_filename)
