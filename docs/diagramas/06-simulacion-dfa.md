# 6. `accepts(input)` — simular una cadena

`Automaton` implementa `Simulable`; `NFA` y `DFA` sobrescriben `accepts`.
La vista lo usa en `MainWindow.onTestString()` con el DFA mínimo.

## 6.1 `DFA.accepts(input)`

```mermaid
flowchart TD
    A([Inicio]) --> B[actual ← estado inicial<br/>i ← 0]
    B --> C{¿i < longitud de input?}
    C -- No --> H{¿isFinal actual?}
    H -- Sí --> ACC[/Retornar true/]
    H -- No --> REJ[/Retornar false/]
    C -- Sí --> D[sig ← transition actual, input en i]
    D --> E{¿sig es null?}
    E -- Sí --> REJ
    E -- No --> F[actual ← sig<br/>i ← i + 1]
    F --> C
    ACC --> Z([Fin])
    REJ --> Z
```

## 6.2 `NFA.accepts(input)`

```mermaid
flowchart TD
    A([Inicio]) --> B[actuales ← epsilonClosure del estado inicial]
    B --> C{¿quedan caracteres c en input?}
    C -- Sí --> D[actuales ← epsilonClosure move actuales, c]
    D --> E{¿actuales vacío?}
    E -- Sí --> REJ[/Retornar false/]
    E -- No --> C
    C -- No --> F{¿algún estado de actuales<br/>es final?}
    F -- Sí --> ACC[/Retornar true/]
    F -- No --> REJ
    ACC --> Z([Fin])
    REJ --> Z
```
