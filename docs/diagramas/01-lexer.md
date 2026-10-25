# 1. Lexer (análisis léxico + concatenación implícita)

Convierte la cadena en una lista de tokens e inserta el token `CONCAT` donde la concatenación es implícita
(por ejemplo `ab` → `a · b`, `a(` → `a · (`, `*a` → `* · a`).

Operadores soportados: `|`, `*`, `+`, `?`, `(`, `)`. Cualquier otro carácter es un `SYMBOL` (`\` escapa el siguiente).

```mermaid
flowchart TD
    A([Inicio]) --> B[/Leer regex/]
    B --> C[tokens ← lista vacía<br/>i ← 0]
    C --> D{¿i < longitud?}
    D -- No --> Z[/Retornar tokens/]
    Z --> FIN([Fin])
    D -- Sí --> E[c ← regex en i]
    E --> F{¿c es espacio?}
    F -- Sí --> N[i ← i + 1]
    F -- No --> G{¿c es barra invertida?}
    G -- Sí --> G1{¿hay carácter siguiente?}
    G1 -- No --> ERR[/Lanzar RegexSyntaxException/]
    ERR --> FIN
    G1 -- Sí --> G2[i ← i + 1<br/>nuevo ← SYMBOL del carácter escapado]
    G -- No --> H{¿c es operador?}
    H -- Sí --> H1[nuevo ← token del operador]
    H -- No --> H2[nuevo ← SYMBOL c]
    G2 --> I
    H1 --> I
    H2 --> I
    I{"¿tokens no vacío Y<br/>último ∈ SYMBOL, ), *, +, ?<br/>Y nuevo ∈ SYMBOL, ( ?"}
    I -- Sí --> J[Agregar CONCAT a tokens]
    I -- No --> K
    J --> K[Agregar nuevo a tokens]
    K --> N
    N --> D
```
