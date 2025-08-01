#!/bin/bash

# Run script for PolynomialSolver
echo "Running PolynomialSolver..."

# Check if class file exists
if [ ! -f "PolynomialSolver.class" ]; then
    echo "Class file not found. Running build script..."
    ./build.sh
fi

# Run the program
java -cp .:gson-2.10.1.jar PolynomialSolver
