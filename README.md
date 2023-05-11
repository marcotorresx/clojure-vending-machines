# Simulation of vending machines using functional programming and parallelization

## Introduction

This project entails simulating vending machines to conduct sales simultaneously, followed by an analysis of the sales and final states of the machines. What distinguishes this project is that it utilizes functional programming and parallelization for its development.

## Program development

The program was developed in Clojure, a functional programming language that also allows for the execution of parallelizable processes. The challenge of this project was the paradigm shift from traditional imperative programming to functional programming.

## How it works

- The program initiates by prompting the user to enter the number of vending machines they intend to simulate, as well as the number of transactions or sales to be conducted by each machine.

<img width="387" alt="image" src="https://github.com/marcotorresx/clojure-vending-machines/assets/90577455/2678d860-592f-461a-856b-88de1a1ebc5d">

- Upon completion of these inputs, the program generates the specified number of machines, each with randomly allocated product quantities and prices. A parallelization process is then executed to simulate the machines' simultaneous sales.

<img width="695" alt="image" src="https://github.com/marcotorresx/clojure-vending-machines/assets/90577455/c086db7e-1735-4cd1-856c-2b8291f85aec">

- Finally, reports on sales and states of the machines are obtained.

<img width="563" alt="image" src="https://github.com/marcotorresx/clojure-vending-machines/assets/90577455/cc75e5fa-8a97-413a-bdb8-0aeab11f6916">

## Installation

1. Clone this repository gith the command: `git clone https://github.com/marcotorresx/clojure-vending-machines.git`

2. Navigate to the root folder and execute the `lein run` command. As the Clojure executable is located in the repository, this command should be sufficient to initiate and run the program.
