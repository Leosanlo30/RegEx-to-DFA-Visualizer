# 5. `Minimizer.minimize(dfa)` — tabla de pares distinguibles

Método de llenado de tabla (REQUIREMENTS: *distinguishable-pairs table*).
Dos estados `p, q` son **distinguibles** si alguna cadena lleva a uno a un estado final y al otro no.

## 5.1 Llenado de la tabla

```mermaid
flowchart TD
    A([Inicio]) --> B[dfa.complete]
    B --> C[marcada ← tabla de pares p, q<br/>con p ≠ q, toda en false]
    C --> D[Para cada par p, q:<br/>si dfa.isFinal p ≠ dfa.isFinal q,<br/>marcada p, q ← true]
    D --> E[cambio ← true]
    E --> F{¿cambio?}
    F -- No --> G([Ir a 5.2])
    F -- Sí --> H[cambio ← false]
    H --> I{¿quedan pares p, q<br/>sin marcar?}
    I -- No --> F
    I -- Sí --> J{¿quedan símbolos c?}
    J -- No --> I
    J -- Sí --> K[p2 ← dfa.transition p, c<br/>q2 ← dfa.transition q, c]
    K --> L{¿p2 ≠ q2 Y<br/>marcada p2, q2?}
    L -- No --> J
    L -- Sí --> M[marcada p, q ← true<br/>cambio ← true]
    M --> I
```

## 5.2 Construcción del DFA mínimo

Los pares **no marcados** son equivalentes y se fusionan en un solo estado.

```mermaid
flowchart TD
    A([Inicio]) --> B[Agrupar en clases los estados<br/>cuyos pares no están marcados]
    B --> C[minimo ← new DFA<br/>por cada clase: new State, minimo.addState]
    C --> D[inicial de minimo ← clase que contiene<br/>el inicial de dfa]
    D --> E[Por cada clase con algún estado final:<br/>minimo.markFinal clase]
    E --> F[Por cada clase K y símbolo c:<br/>r ← un estado cualquiera de K<br/>minimo.addTransition K, c, clase de dfa.transition r, c]
    F --> G[/Retornar minimo/]
    G --> Z([Fin])
```

> El estado sumidero que agregó `complete()` queda en el DFA mínimo; la vista puede ocultarlo al dibujar.
