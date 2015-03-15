import os, sys

def expand(formula):
	atoms = []
	for i, e in enumerate(formula):
		if i < len(formula) - 1:
			next = formula[i + 1]
		if str.isalpha(e) and str.isdigit(next):
			atoms.extend(int(next) * e)
		elif str.isalpha(e) and str.isalpha(next):
			atoms.append(e)
		elif next is None and str.isalpha(e):
			atoms.append(e)
	return "".join(atoms)

input_file = sys.argv[1]
for line in open(input_file):
	formula, obs, time = line.split()
	expanded_formula = expand(formula)
	heavy_atom_count = len([x for x in expanded_formula if x != "H"]) 
	#print formula, heavy_atom_count, expanded_formula, time
	print heavy_atom_count, time
	#print obs, time
