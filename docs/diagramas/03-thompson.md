# 3. Construcción de Thompson

`ThompsonConverter.build` le pide a la raíz que se construya. Cada `RegexNode` implementa `buildNFA(nfa)` (**polimorfismo**):
crea sus estados con `nfa.newState()`, agrega sus transiciones y retorna un `Fragment(start, end)`.

## 3.1 `ThompsonConverter.build(root)`

```mermaid
flowchart TD
    A([Inicio]) --> B[nfa ← new NFA]
    B --> C[frag ← root.buildNFA nfa]
    C --> D[inicial de nfa ← frag.start]
    D --> E[nfa.markFinal frag.end]
    E --> F[/Retornar nfa/]
    F --> Z([Fin])
```

## 3.2 `SymbolNode.buildNFA(nfa)`

```mermaid
flowchart TD
    A([Inicio]) --> B[i ← nfa.newState<br/>f ← nfa.newState]
    B --> C[nfa.addTransition i, symbol, f]
    C --> D[/Retornar new Fragment i, f/]
    D --> Z([Fin])
```

## 3.3 `EpsilonNode.buildNFA(nfa)`

```mermaid
flowchart TD
    A([Inicio]) --> B[i ← nfa.newState<br/>f ← nfa.newState]
    B --> C[nfa.addTransition i, NFA.EPSILON, f]
    C --> D[/Retornar new Fragment i, f/]
    D --> Z([Fin])
```

## 3.4 `ConcatenationNode.buildNFA(nfa)`

```mermaid
flowchart TD
    A([Inicio]) --> B[A ← left.buildNFA nfa<br/>B ← right.buildNFA nfa]
    B --> C[nfa.addTransition A.end, EPSILON, B.start]
    C --> D[/Retornar new Fragment A.start, B.end/]
    D --> Z([Fin])
```

## 3.5 `UnionNode.buildNFA(nfa)`

```mermaid
flowchart TD
    A([Inicio]) --> B[A ← left.buildNFA nfa<br/>B ← right.buildNFA nfa]
    B --> C[i ← nfa.newState<br/>f ← nfa.newState]
    C --> D[nfa.addTransition i, EPSILON, A.start<br/>nfa.addTransition i, EPSILON, B.start]
    D --> E[nfa.addTransition A.end, EPSILON, f<br/>nfa.addTransition B.end, EPSILON, f]
    E --> F[/Retornar new Fragment i, f/]
    F --> Z([Fin])
```

## 3.6 `KleeneNode.buildNFA(nfa)`

```mermaid
flowchart TD
    A([Inicio]) --> B[A ← child.buildNFA nfa]
    B --> C[i ← nfa.newState<br/>f ← nfa.newState]
    C --> D[nfa.addTransition i, EPSILON, A.start<br/>nfa.addTransition i, EPSILON, f]
    D --> E[nfa.addTransition A.end, EPSILON, A.start<br/>nfa.addTransition A.end, EPSILON, f]
    E --> F[/Retornar new Fragment i, f/]
    F --> Z([Fin])
```
