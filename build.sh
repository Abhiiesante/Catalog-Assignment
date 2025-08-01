#!/bin/bash

# Build script for PolynomialSolver
echo "Building PolynomialSolver..."

# Check if Gson library exists
if [ ! -f "gson-2.10.1.jar" ]; then
    echo "Downloading Gson library..."
    wget https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
fi

# Compile the Java program
echo "Compiling PolynomialSolver.java..."
javac -cp gson-2.10.1.jar PolynomialSolver.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the program: java -cp .:gson-2.10.1.jar PolynomialSolver"
else
    echo "Compilation failed!"
    exit 1
fi
