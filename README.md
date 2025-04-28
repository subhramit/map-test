https://github.com/JabRef/jabref/pull/13012#discussion_r2063189189

## Steps:
For bytecode generation
```bash
javac TwoMapsApproach.java
javap -c -v TwoMapsApproach.class > two_maps_bytecode.txt
javac OneMapApproach.java
javap -c -v OneMapApproach.class > one_map_bytecode.txt
```
For performance comparison
```bash
java MapPerformanceTest.java
```
