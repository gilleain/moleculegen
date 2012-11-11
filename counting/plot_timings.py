from matplotlib import pyplot as plt
import sys

def readData(filename):
    formulae = []
    counts = []
    times = []
    fh = open(filename)
    for line in fh:
        formula, count, time = line.rstrip().split()
        formulae.append(int(formula))
        counts.append(int(count))
        times.append(int(time))
    fh.close()
    return (formulae, counts, times)

AMG_data_file = sys.argv[1]
OMG_data_file = sys.argv[2]

f_AMG, c_AMG, t_AMG = readData(AMG_data_file)
f_OMG, c_OMG, t_OMG = readData(OMG_data_file)

line_format_AMG = 'b-'
line_format_OMG = 'r-'

# TODO : check that f_AMG = f_OMG...
fig = plt.figure()
ax = fig.add_subplot(111)
ax.plot(f_AMG, t_AMG, line_format_AMG, f_OMG, t_OMG, line_format_OMG)
fig.savefig("test.png")
