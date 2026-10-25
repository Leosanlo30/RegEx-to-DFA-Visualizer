# 4. Construcción de subconjuntos (NFA → DFA)

Cada estado del DFA representa un **conjunto** de estados del NFA.
`SubsetConstructor` usa los métodos `epsilonClosure` y `move` de `NFA`.

## 4.1 `NFA.epsilonClosure(s)`

Todos los estados alcanzables desde `s` usando solo transiciones `EPSILON`.

```mermaid
flowchart TD
    A([Inicio]) --> B[cierre ← copia de s<br/>pila ← todos los estados de s]
    B --> C{¿pila vacía?}
    C -- Sí --> R[/Retornar cierre/]
    R --> Z([Fin])
    C -- No --> D[q ← pila.pop]
    D --> E[destinos ← delta de q con EPSILON<br/>o vacío si no hay]
    E --> F{¿quedan t en destinos?}
    F -- No --> C
    F -- Sí --> G{¿t ∈ cierre?}
    G -- Sí --> F
    G -- No --> H[agregar t a cierre<br/>pila.push t]
    H --> F
```

## 4.2 `NFA.move(s, c)`

```mermaid
flowchart TD
    A([Inicio]) --> B[resultado ← conjunto vacío]
    B --> C{¿quedan estados q en s?}
    C -- No --> R[/Retornar resultado/]
    R --> Z([Fin])
    C -- Sí --> D{¿delta tiene transiciones<br/>de q con c?}
    D -- No --> C
    D -- Sí --> E[agregar todos esos destinos<br/>a resultado]
    E --> C
```

## 4.3 `SubsetConstructor.determinize(nfa)`

```mermaid
flowchart TD
    A([Inicio]) --> B[dfa ← new DFA<br/>mapa ← vacío: Set de State NFA → State DFA<br/>pendientes ← cola vacía]
    B --> C[D0 ← nfa.epsilonClosure de inicial del NFA]
    C --> D[q0 ← new State<br/>dfa.addState q0, inicial de dfa ← q0<br/>mapa D0 ← q0, pendientes.encolar D0]
    D --> E{¿pendientes vacía?}
    E -- Sí --> R[/Retornar dfa/]
    R --> Z([Fin])
    E -- No --> F[T ← pendientes.desencolar]
    F --> G{¿T contiene algún final del NFA?}
    G -- Sí --> H[dfa.markFinal mapa T]
    G -- No --> I
    H --> I{¿quedan símbolos c<br/>en el alfabeto del NFA?}
    I -- No --> E
    I -- Sí --> J[U ← nfa.epsilonClosure nfa.move T, c]
    J --> K{¿U vacío?}
    K -- Sí --> I
    K -- No --> L{¿mapa contiene U?}
    L -- No --> M[qU ← new State<br/>dfa.addState qU<br/>mapa U ← qU, pendientes.encolar U]
    M --> N
    L -- Sí --> N[dfa.addTransition mapa T, c, mapa U]
    N --> I
```

> Si `U` es vacío no se crea transición. El DFA queda **parcial**; `Minimizer` lo completa con `dfa.complete()`.
