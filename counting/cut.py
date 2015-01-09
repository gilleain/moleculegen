import sys

line_counter = 0
max_count = int(sys.argv[2])
for line in open(sys.argv[1]):
    if line_counter == 0: 
        #for i, bit in enumerate(line.split()): print i, bit
        line_counter += 1
        continue 
    line = line.strip()
    bits = line.split()
    n1 = int("".join(bits[4].split(',')))
    n2 = int("".join(bits[11].split(',')))
    if n1 != n2:
	    print "\t".join([str(x) for x in [n1, n2, bits[3]]])
	    line_counter += 1
    if max_count > 0 and line_counter > max_count: break
