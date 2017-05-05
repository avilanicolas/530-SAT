CSC 431 Compiler
================
- [About](#about)
- [Usage](#usage)

About
-----
Binary Decision Diagram Implemention of a Sat solver for Cal Poly CSC 530.

Usage
-------
```
mvn install
java -jar target/sat-0.0.1-jar-with-dependencies.jar <cnf_file>
```
This will output all possible mappings of variables that satisfy the boolean equation specified by cnf_file which must be a file specifiying a boolean function in dimacs cnf format  see https://www.dwheeler.com/essays/minisat-user-guide.html

Also checkout the cnf files in the cnf directory one of them is unsatisifable!

