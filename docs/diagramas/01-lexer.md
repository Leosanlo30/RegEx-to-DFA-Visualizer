# 1. `Lexer`

Sintaxis (RF02): letras y dígitos, `|`, `*`, `(`, `)`, `ε` y concatenación implícita.

| Carácter | `TokenType` |
|---|---|
| letra o dígito | `SYMBOL` |
| `ε` | `EPSILON` |
| `\|` | `UNION` |
| `*` | `KLEENE` |
| `(` | `LEFT_PAREN` |
| `)` | `RIGHT_PAREN` |
| (insertado) | `CONCAT` |

## 1.1 `tokenize()`

```mermaid
flowchart TD
    A([Inicio]) --> B[tokens ← lista vacía<br/>i ← 0]
    B --> C{¿i < longitud de regex?}
    C -- No --> R[/Retornar insertConcatenation tokens/]
    R --> Z([Fin])
    C -- Sí --> D[c ← regex en i]
    D --> E{¿c es espacio?}
    E -- Sí --> N[i ← i + 1]
    E -- No --> F{¿Qué es c?}
    F -- letra o dígito --> G1[type ← SYMBOL]
    F -- ε --> G2[type ← EPSILON]
    F -- "|" --> G3[type ← UNION]
    F -- "*" --> G4[type ← KLEENE]
    F -- "(" --> G5[type ← LEFT_PAREN]
    F -- ")" --> G6[type ← RIGHT_PAREN]
    F -- otro --> ERR[/Lanzar RegexInvalidException<br/>position = i/]
    ERR --> Z
    G1 --> H
    G2 --> H
    G3 --> H
    G4 --> H
    G5 --> H
    G6 --> H
    H[agregar new Token type, c, i a tokens] --> N
    N --> C
```

## 1.2 `insertConcatenation(tokens)`

Agrega `CONCAT` entre dos tokens cuando el anterior **termina** un operando y el siguiente **empieza** uno.

```mermaid
flowchart TD
    A([Inicio]) --> B[resultado ← lista vacía<br/>prev ← null]
    B --> C{¿quedan tokens t?}
    C -- No --> R[/Retornar resultado/]
    R --> Z([Fin])
    C -- Sí --> D{"¿prev ≠ null Y<br/>prev ∈ SYMBOL, EPSILON, KLEENE, RIGHT_PAREN Y<br/>t ∈ SYMBOL, EPSILON, LEFT_PAREN?"}
    D -- Sí --> E[agregar Token CONCAT<br/>con position de t a resultado]
    D -- No --> F
    E --> F[agregar t a resultado<br/>prev ← t]
    F --> C
```

Ejemplo: `(a|b)*abb` → `( a | b ) * · a · b · b`
