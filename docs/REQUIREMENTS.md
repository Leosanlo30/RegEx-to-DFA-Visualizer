# Requirements

Analysis document for **Regex DFA Visualizer**. Covers the problem being solved, the input / process / output breakdown, and the functional and non-functional requirements.

## 1. Problem statement

Solving finite-automaton exercises is a reasoning-heavy process: starting from a regular expression, the student builds an automaton, converts it into a deterministic one, and often minimizes it. When they finish, there is no simple way to check that the result is correct — especially for the minimized version, where an extra state or a mis-pointed transition changes the language the automaton recognizes.

**Regex DFA Visualizer** addresses this through visual verification. The user enters a valid regular expression and the program builds its NFA, converts it into a DFA, minimizes it, and displays the state diagram and transition table of the minimal DFA on screen. It also lets the user test strings to see whether the automaton accepts them, so the student can compare their own answer against the program's.

### Scope

- Regular expressions with union, concatenation, Kleene star, grouping, and the empty string, over an alphabet of letters and digits.
- The NFA is built internally as an intermediate step; the primary output is the minimal DFA.
- Conversions can be saved and loaded again later for review.

## 2. Analysis

### 2.1 Inputs, processes, and outputs

| Stage | Element | Description |
|-------|---------|-------------|
| Input | Regular expression | Text written in the UI or loaded from a `.txt` file. Example: `(a\|b)*abb`. |
| Input | Test string | Text the user wants to evaluate against the generated automaton. |
| Input | Conversion file | A `.json` file with a previously saved conversion. |
| Input | Syntax menu | Operator or example selected and inserted into the expression field. |
| Process | Lexical analysis | Converts the text into tokens and inserts implicit concatenation. |
| Process | Syntactic analysis | Converts tokens to postfix notation (shunting-yard) and builds the syntax tree. Detects syntax errors. |
| Process | Thompson's construction | Converts the syntax tree into an NFA with ε-transitions. |
| Process | Subset construction | Converts the NFA into an equivalent DFA using ε-closure and the `move` function. |
| Process | Minimization | Reduces the DFA to its minimum number of states using the distinguishable-pairs table. |
| Process | Simulation | Walks the DFA with the test string to decide whether it is accepted. |
| Process | Persistence | Serializes the conversion to JSON for saving and reconstructs it on load. |
| Output | Transition table | Table of the minimal DFA with states as rows and alphabet symbols as columns. |
| Output | State diagram | Drawing of the minimal DFA with the initial state marked and accepting states shown with a double circle. |
| Output | Test result | "Accepted" or "rejected" message for the evaluated string. |
| Output | Error messages | Description of the error and its position in the expression, or of the failure to read a file. |
| Output | `.json` file | Saved conversion (regular expression and automaton). |

### 2.2 Functional requirements

| ID | Functional requirement |
|----|------------------------|
| **RF01** | The system must allow writing a regular expression in the UI or loading it from a text file. |
| **RF02** | The system must accept regular expressions with the following syntax: letters and digits as alphabet symbols, union (`\|`), implicit concatenation, Kleene star (`*`), grouping with parentheses, and the empty string (`ε`). Precedence, from highest to lowest: `*`, concatenation, `\|`. |
| **RF03** | The system must validate the regular expression and, if invalid, display a message indicating the type of error and its position, without building the automaton. |
| **RF04** | The system must build the NFA equivalent to the regular expression, convert it into a DFA, and minimize the DFA. |
| **RF05** | The system must display the transition table of the minimal DFA. |
| **RF06** | The system must render the state diagram of the minimal DFA graphically, indicating the initial state and the accepting states. |
| **RF07** | The system must allow evaluating strings and indicate whether the automaton accepts or rejects them. |
| **RF08** | The system must provide a dropdown menu with the syntax operators and example expressions that get inserted into the input. |
| **RF09** | The system must allow saving the conversion (regular expression and automaton) in a JSON file and loading it back. |

### 2.3 Non-functional requirements

| ID | Non-functional requirement |
|----|----------------------------|
| **RNF01** | The application must be developed in Java 21, applying the OOP principles: encapsulation, composition, inheritance, polymorphism, and interfaces. |
| **RNF02** | The code must be split into packages: `interfaz`, `logica`, `modelo`, `persistencia`, and `Exceptions`. The model and logic layers must not depend on the UI. |
| **RNF03** | The system must not crash on invalid input or file failures; such situations must be handled with custom exceptions. |
| **RNF04** | The application must have a desktop GUI developed in Swing. |
| **RNF05** | Error messages must be in Spanish and understandable to the user, without exposing internal program traces. |
| **RNF06** | The conversion logic must be covered by unit tests in JUnit. |
| **RNF07** | The project must manage its dependencies with Maven. |
