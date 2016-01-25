# Building and running
Use maven to build a jar with dependencies:

````mvn package -DskipTests````

and then run :

````
java -cp target/moleculegen-1.0-jar-with-dependencies.jar app.AMG -e "C4H11N" -O SMI
NC(C)(C)C
NCC(C)C
NC(C)CC
NCCCC
N(C)C(C)C
N(C)CCC
N(CC)CC
N(C)(C)CC
````

For a full list of commands, run with `-h` or no arguments:

````
java -cp target/moleculegen-1.0-jar-with-dependencies.jar app.AMG
Usage: java -jar AMG.jar -e <formula>

Basic options :
-c              Compare the output to the input file
-e <formula>    Elemental Formula
-h              Print help
-i <path>       Input Filepath
-I <format>     Input Format (SMI, SIG, SDF, MOL)
-n              Number output lines
-o <path>       Output Filepath
-O <format>     Output Format (SMI, SIG, SDF, MOL)
-p              Show parent of each molecule
-r <min:max>    Range of input file to use
-t              Time the run
-z              Compress (zip) the output

Advanced options :
--augmentation=<method>  Augmentation method (ATOM, BOND)
````

# Background

An implementation of a molecule generation algorithm similar to that of the Open Molecule Generator (OMG) [Source here](http://sourceforge.net/p/openmg/wiki/Home). There are a couple of differences:

- OMG uses nauty (a fast c-library for graph automorphism and canonical labelling computation), this uses [signatures](https://github.com/gilleain/signatures).
- OMG augments [by bond](http://gilleain.blogspot.com/2012/09/open-molecule-generators-algorithm.html) while this augments by vertex.
- There is an alterntative listing method for candidate children of a parent molecule, that uses the automorphism group of the parent to eliminate symmetric duplicates.

It is, however, slower than OMG - which is slower than [molgen](http://molgen.de/?src=documents/molgen5.html). So it should be considered as a proof-of-concept rather than a useable tool (for now...). Comments and suggestions are welcome.
