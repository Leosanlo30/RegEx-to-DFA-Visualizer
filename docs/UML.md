# Class Diagram (simplified version)

UML diagram of the **RegEx → DFA Visualizer** system, organized by package:

- `exceptions`
- `ui`
- `persistence`
- `logic`
- `model`

---

## Overview

```mermaid
classDiagram
    direction LR

    %% ===== Package: exceptions =====
    class Exception {
        <<Java>>
    }
    class RegexInvalidException {
        -position: int
    }
    class CorruptFileException {
        -path: String
    }
    Exception <|-- RegexInvalidException
    Exception <|-- CorruptFileException

    %% ===== Package: ui =====
    class MainWindow {
        -converter: Converter
        -repository: Repository~ConversionResult~
        -currentResult: ConversionResult
        -onConvert() void
        -onTestString() void
        -onSave() void
        -onLoad() void
    }
    class SyntaxMenu {
        -targetField: JTextField
        +insert(text: String) void
    }
    class AutomatonRenderer {
        +draw(dfa: DFA) JComponent
    }
    class TransitionsTableModel {
        -dfa: DFA
        +getValueAt(row: int, col: int) Object
    }

    %% ===== Package: persistence =====
    class Repository~T~ {
        <<interface>>
        +save(obj: T, file: File) void
        +load(file: File) T
    }
    class JsonRepository {
        -gson: Gson
        +save(r: ConversionResult, file: File) void
        +load(file: File) ConversionResult
    }
    Repository <|.. JsonRepository

    %% ===== Package: logic =====
    class Converter {
        <<interface>>
        +convert(regex: String) ConversionResult
    }
    class RegexConverter {
        -thompson: ThompsonConverter
        -subsets: SubsetConstructor
        -minimizer: Minimizer
        +convert(regex: String) ConversionResult
    }
    class Lexer {
        -regex: String
        +tokenize() List~Token~
        -insertConcatenation(tokens: List~Token~) List~Token~
    }
    class Parser {
        -tokens: List~Token~
        +parse() RegexNode
        +toPostfix() List~Token~
        -buildTree(postfix: List~Token~) RegexNode
        -precedence(type: TokenType) int
    }
    class Token {
        -type: TokenType
        -value: char
        -position: int
    }
    class TokenType {
        <<enumeration>>
        SYMBOL
        EPSILON
        UNION
        CONCAT
        KLEENE
        LEFT_PAREN
        RIGHT_PAREN
    }
    class ThompsonConverter {
        +build(root: RegexNode) NFA
    }
    class SubsetConstructor {
        +determinize(nfa: NFA) DFA
    }
    class Minimizer {
        +minimize(dfa: DFA) DFA
    }
    class ConversionResult {
        -regex: String
        -nfa: NFA
        -dfa: DFA
        -minimalDfa: DFA
    }

    Converter <|.. RegexConverter
    RegexConverter --> ThompsonConverter : uses
    RegexConverter --> SubsetConstructor : uses
    RegexConverter --> Minimizer : uses
    RegexConverter ..> ConversionResult : creates
    Lexer ..> Token : creates
    Token --> TokenType : type
    Parser ..> Lexer : uses
    Parser ..> RegexNode : builds

    %% ===== Package: model =====
    class RegexNode {
        <<abstract>>
        +buildNFA(nfa: NFA) Fragment
    }
    class SymbolNode {
        -symbol: char
        +buildNFA(nfa: NFA) Fragment
    }
    class EpsilonNode {
        +buildNFA(nfa: NFA) Fragment
    }
    class UnionNode {
        -left: RegexNode
        -right: RegexNode
        +buildNFA(nfa: NFA) Fragment
    }
    class ConcatenationNode {
        -left: RegexNode
        -right: RegexNode
        +buildNFA(nfa: NFA) Fragment
    }
    class KleeneNode {
        -child: RegexNode
        +buildNFA(nfa: NFA) Fragment
    }

    RegexNode <|-- SymbolNode
    RegexNode <|-- EpsilonNode
    RegexNode <|-- UnionNode
    RegexNode <|-- ConcatenationNode
    RegexNode <|-- KleeneNode

    class Simulable {
        <<interface>>
        +accepts(input: String) boolean
    }
    class Automaton {
        <<abstract>>
        #states: Set~State~
        #alphabet: Set~Character~
        #initial: State
        #finals: Set~State~
        +addState(s: State) void
        +markFinal(s: State) void
        +isFinal(s: State) boolean
        +accepts(input: String) boolean
    }
    class NFA {
        +EPSILON: char = 'ε'
        -delta: Map~State, Map~Character, Set~State~~~
        +newState() State
        +addTransition(src: State, c: char, dst: State) void
        +epsilonClosure(s: Set~State~) Set~State~
        +move(s: Set~State~, c: char) Set~State~
        +accepts(input: String) boolean
    }
    class DFA {
        -delta: Map~State, Map~Character, State~~
        +addTransition(src: State, c: char, dst: State) void
        +transition(s: State, c: char) State
        +complete() void
        +accepts(input: String) boolean
    }
    class State {
        -id: int
        +equals(o: Object) boolean
        +hashCode() int
    }
    class Fragment {
        -start: State
        -end: State
    }

    Simulable <|.. Automaton
    Automaton <|-- NFA
    Automaton <|-- DFA
    Automaton "1" --> "1..*" State
    Fragment --> State : start, end

    %% ===== Cross-package relations =====
    MainWindow --> Converter
    MainWindow --> Repository
    MainWindow --> ConversionResult
    MainWindow --> SyntaxMenu
    MainWindow --> AutomatonRenderer
    MainWindow --> TransitionsTableModel
    JsonRepository ..> ConversionResult
    TransitionsTableModel --> DFA
    AutomatonRenderer ..> DFA
```

---

## Package `exceptions`

```mermaid
classDiagram
    class Exception {
        <<Java>>
    }
    class RegexInvalidException {
        -position: int
    }
    class CorruptFileException {
        -path: String
    }
    Exception <|-- RegexInvalidException
    Exception <|-- CorruptFileException
```

| Class | Role |
|---|---|
| `RegexInvalidException` | Syntax error in the regular expression; carries the failing `position`. |
| `CorruptFileException` | Error reading a persisted file; carries the `path`. |

---

## Package `ui`

```mermaid
classDiagram
    class MainWindow {
        -converter: Converter
        -repository: Repository~ConversionResult~
        -currentResult: ConversionResult
        -onConvert() void
        -onTestString() void
        -onSave() void
        -onLoad() void
    }
    class SyntaxMenu {
        -targetField: JTextField
        +insert(text: String) void
    }
    class AutomatonRenderer {
        +draw(dfa: DFA) JComponent
    }
    class TransitionsTableModel {
        -dfa: DFA
        +getValueAt(row: int, col: int) Object
    }

    MainWindow --> SyntaxMenu
    MainWindow --> AutomatonRenderer
    MainWindow --> TransitionsTableModel
```

| Class | Role |
|---|---|
| `MainWindow` | Root UI controller; orchestrates conversion, string testing and persistence. |
| `SyntaxMenu` | Inserts regex alphabet symbols into the input field. |
| `AutomatonRenderer` | Draws the DFA as a Swing component. |
| `TransitionsTableModel` | `TableModel` for the DFA's transitions table. |

---

## Package `persistence`

```mermaid
classDiagram
    class Repository~T~ {
        <<interface>>
        +save(obj: T, file: File) void
        +load(file: File) T
    }
    class JsonRepository {
        -gson: Gson
        +save(r: ConversionResult, file: File) void
        +load(file: File) ConversionResult
    }
    Repository <|.. JsonRepository
```

| Class | Role |
|---|---|
| `Repository<T>` | Generic save/load-by-file contract. |
| `JsonRepository` | Gson-based implementation for `ConversionResult`. |

---

## Package `logic`

```mermaid
classDiagram
    direction LR

    class Converter {
        <<interface>>
        +convert(regex: String) ConversionResult
    }
    class RegexConverter {
        -thompson: ThompsonConverter
        -subsets: SubsetConstructor
        -minimizer: Minimizer
        +convert(regex: String) ConversionResult
    }
    class Lexer {
        -regex: String
        +tokenize() List~Token~
        -insertConcatenation(tokens: List~Token~) List~Token~
    }
    class Parser {
        -tokens: List~Token~
        +parse() RegexNode
        +toPostfix() List~Token~
        -buildTree(postfix: List~Token~) RegexNode
        -precedence(type: TokenType) int
    }
    class Token {
        -type: TokenType
        -value: char
        -position: int
    }
    class TokenType {
        <<enumeration>>
        SYMBOL
        EPSILON
        UNION
        CONCAT
        KLEENE
        LEFT_PAREN
        RIGHT_PAREN
    }
    class ThompsonConverter {
        +build(root: RegexNode) NFA
    }
    class SubsetConstructor {
        +determinize(nfa: NFA) DFA
    }
    class Minimizer {
        +minimize(dfa: DFA) DFA
    }
    class ConversionResult {
        -regex: String
        -nfa: NFA
        -dfa: DFA
        -minimalDfa: DFA
    }

    Converter <|.. RegexConverter
    RegexConverter --> ThompsonConverter : uses
    RegexConverter --> SubsetConstructor : uses
    RegexConverter --> Minimizer : uses
    RegexConverter ..> ConversionResult : creates
    Lexer ..> Token : creates
    Token --> TokenType : type
    Parser ..> Lexer : uses
```

**Conversion pipeline**

1. `Lexer` → produces `List<Token>`.
2. `Parser` → builds a `RegexNode` tree (postfix → tree).
3. `ThompsonConverter` → tree to `NFA`.
4. `SubsetConstructor` → `NFA` to `DFA`.
5. `Minimizer` → minimal `DFA`.
6. `RegexConverter` packages everything into `ConversionResult`.

---

## Package `model`

### Regex syntax tree

```mermaid
classDiagram
    class RegexNode {
        <<abstract>>
        +buildNFA(nfa: NFA) Fragment
    }
    class SymbolNode {
        -symbol: char
        +buildNFA(nfa: NFA) Fragment
    }
    class EpsilonNode {
        +buildNFA(nfa: NFA) Fragment
    }
    class UnionNode {
        -left: RegexNode
        -right: RegexNode
        +buildNFA(nfa: NFA) Fragment
    }
    class ConcatenationNode {
        -left: RegexNode
        -right: RegexNode
        +buildNFA(nfa: NFA) Fragment
    }
    class KleeneNode {
        -child: RegexNode
        +buildNFA(nfa: NFA) Fragment
    }

    RegexNode <|-- SymbolNode
    RegexNode <|-- EpsilonNode
    RegexNode <|-- UnionNode
    RegexNode <|-- ConcatenationNode
    RegexNode <|-- KleeneNode
```

### Automata

```mermaid
classDiagram
    class Simulable {
        <<interface>>
        +accepts(input: String) boolean
    }
    class Automaton {
        <<abstract>>
        #states: Set~State~
        #alphabet: Set~Character~
        #initial: State
        #finals: Set~State~
        +addState(s: State) void
        +markFinal(s: State) void
        +isFinal(s: State) boolean
        +accepts(input: String) boolean
    }
    class NFA {
        +EPSILON: char = 'ε'
        -delta: Map~State, Map~Character, Set~State~~~
        +newState() State
        +addTransition(src: State, c: char, dst: State) void
        +epsilonClosure(s: Set~State~) Set~State~
        +move(s: Set~State~, c: char) Set~State~
        +accepts(input: String) boolean
    }
    class DFA {
        -delta: Map~State, Map~Character, State~~
        +addTransition(src: State, c: char, dst: State) void
        +transition(s: State, c: char) State
        +complete() void
        +accepts(input: String) boolean
    }
    class State {
        -id: int
        +equals(o: Object) boolean
        +hashCode() int
    }
    class Fragment {
        -start: State
        -end: State
    }

    Simulable <|.. Automaton
    Automaton <|-- NFA
    Automaton <|-- DFA
    Automaton "1" --> "1..*" State
    Fragment --> State : start, end
```

| Class | Role |
|---|---|
| `Simulable` | Contract for accepting/rejecting input strings. |
| `Automaton` | Common base: states, alphabet, initial and final states. |
| `NFA` | Nondeterministic automaton with ε-transitions. |
| `DFA` | Deterministic automaton; `transition` is a total function after `complete()`. |
| `State` | Identity by `id`. |
| `Fragment` | `(start, end)` pair used by Thompson's construction when building the NFA. |

---

## Key cross-package relations

| Source | Target | Type |
|---|---|---|
| `ui.MainWindow` | `logic.Converter` | association |
| `ui.MainWindow` | `persistence.Repository<ConversionResult>` | association |
| `ui.MainWindow` | `logic.ConversionResult` | association |
| `ui.AutomatonRenderer` | `model.DFA` | dependency |
| `ui.TransitionsTableModel` | `model.DFA` | association |
| `persistence.JsonRepository` | `logic.ConversionResult` | dependency |
| `logic.RegexConverter` | `model.RegexNode`, `NFA`, `DFA` | dependency |
