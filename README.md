# Knn using concurrency
## Author: Leonandro Gurgel

## Idea
The basic idea is implement the K-Nearest-Neighbours algorithm (using serial and concurrent aproachs) for classification using a large data as input. The main focus is on the concurrent ways of implement the KNN, and how this is will increase the efficiency at all.

## Testing
For tests i made use of the Java Microbenchmark Harness and Jmeter. 

## JMC and JFR
For a better feedback of the differences of the implemententions, i used the Java Mission Control and the Java Flight Recorder
 
## Implementations
1 - Serial *KnnClassifier* the standart version of the KNN.

2 - Mutex *MutexClassifier* a variaton of the serial version where the data is divided in **N** parts and every part is proccessed conccurently, the list of classification is a shared area and is used Mutex for control.

3 - Atomic *AtomicClassifier* very similar to the Mutex version, but the list of classifications is controlled by an Atomic integer array.

4 - Callable *CallableClassifier* the aproach is similar to the Mutex and Atomic, but now all Threads are controlled by an ExecutorService using Callables.

5 - ForkJoin

6 - ParallelStream

7 - Spark

8 - Reactor
