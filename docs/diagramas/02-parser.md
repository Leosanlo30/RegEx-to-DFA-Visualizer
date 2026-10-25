# 2. `Parser` (shunting-yard → árbol)

El parser recibe los tokens del `Lexer`, los pasa a **notación postfija** y con ella construye el árbol de `RegexNode`.

## 2.1 `parse()`

```mermaid
flowchart TD
    A([Inicio]) --> B[postfix ← toPostfix]
    B --> C[root ← buildTree postfix]
    C --> D[/Retornar root/]
    D --> Z([Fin])
```

## 2.2 `precedence(type)`

Precedencia (RF02): `*` > concatenación > `|`.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿type?}
    B -- KLEENE --> C[/Retornar 3/]
    B -- CONCAT --> D[/Retornar 2/]
    B -- UNION --> E[/Retornar 1/]
    B -- otro --> F[/Retornar 0/]
    C --> Z([Fin])
    D --> Z
    E --> Z
    F --> Z
```

## 2.3 `toPostfix()` — algoritmo shunting-yard

`*` es unario **postfijo**: ya está después de su operando, así que va directo a la salida.

```mermaid
flowchart TD
    A([Inicio]) --> B[salida ← lista vacía<br/>pila ← pila vacía]
    B --> C{¿quedan tokens t?}
    C -- Sí --> D{¿t.type?}

    D -- SYMBOL, EPSILON o KLEENE --> E[agregar t a salida]
    E --> C

    D -- LEFT_PAREN --> F[pila.push t]
    F --> C

    D -- RIGHT_PAREN --> G{¿pila vacía?}
    G -- Sí --> ERR1[/Lanzar RegexInvalidException:<br/>paréntesis de cierre sin abrir/]
    G -- No --> H{¿tope es LEFT_PAREN?}
    H -- No --> I[salida ← pila.pop]
    I --> G
    H -- Sí --> J[pila.pop, descartar]
    J --> C

    D -- UNION o CONCAT --> K{"¿pila no vacía Y tope ≠ LEFT_PAREN Y<br/>precedence tope ≥ precedence t?"}
    K -- Sí --> L[salida ← pila.pop]
    L --> K
    K -- No --> M[pila.push t]
    M --> C

    C -- No --> N{¿pila vacía?}
    N -- No --> O{¿tope es LEFT_PAREN?}
    O -- Sí --> ERR2[/Lanzar RegexInvalidException:<br/>paréntesis sin cerrar/]
    O -- No --> P[salida ← pila.pop]
    P --> N
    N -- Sí --> R[/Retornar salida/]

    ERR1 --> Z([Fin])
    ERR2 --> Z
    R --> Z
```

Ejemplo: `( a | b ) * · a · b · b` → `a b | * a · b · b ·`

## 2.4 `buildTree(postfix)`

```mermaid
flowchart TD
    A([Inicio]) --> B[pila ← pila vacía de RegexNode]
    B --> C{¿quedan tokens t en postfix?}
    C -- Sí --> D{¿t.type?}

    D -- SYMBOL --> E[pila.push new SymbolNode t.value]
    D -- EPSILON --> F[pila.push new EpsilonNode]
    E --> C
    F --> C

    D -- KLEENE --> G{¿pila tiene ≥ 1 nodo?}
    G -- No --> ERR[/Lanzar RegexInvalidException<br/>position = t.position:<br/>falta operando/]
    G -- Sí --> H[hijo ← pila.pop<br/>pila.push new KleeneNode hijo]
    H --> C

    D -- UNION o CONCAT --> I{¿pila tiene ≥ 2 nodos?}
    I -- No --> ERR
    I -- Sí --> J[der ← pila.pop<br/>izq ← pila.pop]
    J --> K{¿UNION?}
    K -- Sí --> L[pila.push new UnionNode izq, der]
    K -- No --> M[pila.push new ConcatenationNode izq, der]
    L --> C
    M --> C

    C -- No --> N{¿pila tiene exactamente 1 nodo?}
    N -- No --> ERR2[/Lanzar RegexInvalidException:<br/>expresión vacía o incompleta/]
    N -- Sí --> R[/Retornar pila.pop/]

    ERR --> Z([Fin])
    ERR2 --> Z
    R --> Z
```

> Importante: primero se saca `der` y después `izq`, porque la pila invierte el orden.
