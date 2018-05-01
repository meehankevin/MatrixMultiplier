# Contents:
1. An executable application which computes the product of two N x N matrices using three different methods - two of which implement Java threads - and outputs the time taken for each method. 
2. The Java files used to build the application.

## Application
The user inputs three different variables (from a list of options):
  1. The size N of each matrix
  2. The number of threads to be used in threadpool 1 (used in methods two and three)
  3. The number of threads to be used in threadpool 2 (used in method three only)

## Java files:
### 1. setup.java
   randomly generates the two matrices A and B to be multiplied (as well as A transpose for method one)
 
### 2. NoThreads.java
   implements method one: a basic matrix multiplication algorithm without the use of threads
 
### 3. Naive.java
   implements method two: a basic matrix multiplication algorithm used in conjunction with java threads
  
### 4. Strassen.java
   implements method three: the Strassen algorithm for matrix multiplication used in conjunction with java threads
  
### 5. matrixGUI.java
   creates the GUI for the application using Java Swing