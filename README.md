# Simulation of vending machines using functional programming and parallelization

## Introduction

This project entails simulating vending machines to conduct sales simultaneously, followed by an analysis of the sales and final states of the machines. What distinguishes this project is that it utilizes functional programming and parallelization for its development.

## Program development

The program was developed in Clojure, a functional programming language that also allows for the execution of parallelizable processes. The challenge of this project was the paradigm shift from traditional imperative programming to functional programming.

## How it works

The program initiates by prompting the user to enter the number of vending machines they intend to simulate, as well as the number of transactions or sales to be conducted by each machine.

Upon completion of these inputs, the program generates the specified number of machines, each with randomly allocated product quantities and prices. A parallelization process is then executed to simulate the machines' simultaneous sales.

Finally, reports on sales and states of the machines are obtained.

## Installation

1. Clone this repository gith the command: `git clone https://github.com/marcotorresx/clojure-vending-machines.git`

2. Navigate to the root folder and execute the `lein run` command. As the Clojure executable is located in the repository, this command should be sufficient to initiate and run the program.
