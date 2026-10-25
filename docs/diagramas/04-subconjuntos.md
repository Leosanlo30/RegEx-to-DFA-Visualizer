# 4. Construcción de subconjuntos (NFA-ε → DFA)

Cada estado del DFA representa un **conjunto** de estados del NFA.
Usa dos operaciones auxiliares: `ε-closure(T)` y `move(T, a)`.

## 4.1 `ε-closure(T)`

Todos los estados alcanzables desde `T` usando solo transiciones ε.

```mermaid
flowchart TD
    A([Inicio]) --> B[/Recibir conjunto T/]
    B --> C[cierre ← copia de T<br/>pila ← todos los estados de T]
    C --> D{¿pila vacía?}
    D -- Sí --> R[/Retornar cierre/]
    R --> Z([Fin])
    D -- No --> E[s ← pila.pop]
    E --> F{¿quedan transiciones<br/>s —ε→ t sin revisar?}
    F -- No --> D
    F -- Sí --> G{¿t ∈ cierre?}
    G -- Sí --> F
    G -- No --> H[agregar t a cierre<br/>pila.push t]
    H --> F
```

## 4.2 `move(T, a)`

```mermaid
flowchart TD
    A([Inicio]) --> B[/Recibir conjunto T y símbolo a/]
    B --> C[resultado ← vacío]
    C --> D[Para cada s ∈ T y cada<br/>transición s —a→ t:<br/>agregar t a resultado]
    D --> E[/Retornar resultado/]
    E --> F([Fin])
```

## 4.3 Algoritmo principal

```mermaid
flowchart TD
    A([Inicio]) --> B[/Recibir NFA y alfabeto Σ/]
    B --> C[D0 ← ε-closure inicial del NFA]
    C --> D[Crear estado DFA para D0<br/>marcarlo como inicial<br/>pendientes ← cola con D0<br/>mapa ← D0 → estado]
    D --> E{¿pendientes vacía?}
    E -- Sí --> M[Marcar como finales los estados DFA<br/>cuyo conjunto contiene un final del NFA]
    M --> R[/Retornar DFA/]
    R --> Z([Fin])
    E -- No --> F[T ← pendientes.desencolar]
    F --> G{¿quedan símbolos a ∈ Σ<br/>sin procesar para T?}
    G -- No --> E
    G -- Sí --> H[U ← ε-closure move T, a]
    H --> I{¿U vacío?}
    I -- Sí --> G
    I -- No --> J{¿U ya está en mapa?}
    J -- No --> K[Crear estado DFA para U<br/>mapa ← U → estado<br/>pendientes.encolar U]
    K --> L
    J -- Sí --> L[Agregar transición DFA:<br/>mapa T —a→ mapa U]
    L --> G
```

> Si `U` es vacío no se crea transición (DFA parcial). Si se quiere un DFA completo,
> se agrega un estado sumidero antes de minimizar.
