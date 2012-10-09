An implementation of a molecule generation algorithm similar to that of the Open Molecule Generator (OMG) [Source here](http://sourceforge.net/p/openmg/wiki/Home). There are a couple of differences:

- OMG uses nauty (a fast c-library for graph automorphism and canonical labelling computation), this uses [signatures](https://github.com/gilleain/signatures).
- OMG augments [by bond](http://gilleain.blogspot.com/2012/09/open-molecule-generators-algorithm.html) while this augments by vertex.
- There is an alterntative listing method for candidate children of a parent molecule, that uses the automorphism group of the parent to eliminate symmetric duplicates.

It is, however, slower than OMG - which is slower than [molgen](http://molgen.de/?src=documents/molgen5.html). So it should be considered as a proof-of-concept rather than a useable tool (for now...). Comments and suggestions are welcome.
