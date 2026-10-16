# Regex DFA Visualizer

**A regular-expression to finite-automaton visualizer.**

A desktop tool that takes a regular expression, builds its non-deterministic finite automaton (NFA), converts it into a deterministic one (DFA), minimizes it, and renders the **state diagram** and **transition table** of the minimal DFA on screen. It also lets you run test strings against the automaton to check whether they are accepted or rejected.

It is intended as support for students solving finite-automaton exercises who need a visual way to verify their work.

## What it does

Given a regular expression such as `(a|b)*abb`, the application runs the following pipeline:

```
Regular expression
      │
      ▼
Lexical & syntactic analysis   →  syntax tree
      │
      ▼
Thompson's construction        →  NFA (with ε-transitions)
      │
      ▼
Subset construction            →  equivalent DFA
      │
      ▼
Minimization (distinguishable-pairs table)
      │
      ▼
Minimal DFA  ──▶  state diagram + transition table
                 string simulation (accept / reject)
```

The NFA is built internally as an intermediate step; the primary output is the **minimal DFA**.

## Features

- Regex input from the UI or loaded from a `.txt` file.
- Supported syntax: letters and digits, union (`|`), implicit concatenation, Kleene star (`*`), grouping with parentheses, and the empty string (`ε`).
- Regex validation with user-friendly error messages indicating position and type of error.
- State diagram of the minimal DFA with the initial state marked and accepting states shown with a double circle.
- Transition table of the minimal DFA.
- String simulation against the automaton.
- Dropdown menu with syntax operators and example expressions.
- Save and load conversions as `.json` files.

## Scope

- Regular expressions over an alphabet of letters and digits.
- Operators: union, concatenation, Kleene star, grouping, and the empty string.
- The primary output is the minimal DFA; the NFA exists only as an internal step.
- Conversions can be saved and loaded for later review.

## Stack

- **Language:** Java 21
- **UI:** Swing
- **Build / dependencies:** Maven
- **Testing:** JUnit

## Documentation

- [`docs/REQUIREMENTS.md`](docs/REQUIREMENTS.md) — problem statement, inputs / processes / outputs, functional and non-functional requirements.
- [`docs/DESIGN.md`](docs/DESIGN.md) — packages, classes, attributes, methods, and the application of inheritance, polymorphism, and interfaces.

## Team

Integrating project for the Object-Oriented Programming course — Facultad de Matemáticas, Universidad Autónoma de Yucatán. Instructor: Dr. Juan Pablo Ucán Pech.

- Alcocer Castilla David Misael
- Padilla Cortes German de Jesus
- Peña Garcia Guillermo
- San Martín Lopez Leonardo Ariel

Mérida, Yucatán — October 15, 2026.
