# 2. Parser (descenso recursivo → AST)

Gramática (de menor a mayor precedencia):

```
expr    → term ( '|' term )*
term    → factor ( CONCAT factor )*
factor  → atom ( '*' | '+' | '?' )*
atom    → SYMBOL | '(' expr ')'
```

Cada regla es un método que retorna un `RegexNode` (patrón Composite).

## 2.1 Método `parse()`

```mermaid
flowchart TD
    A([Inicio]) --> B[pos ← 0]
    B --> C[raiz ← parseExpr]
    C --> D{¿pos = cantidad de tokens?}
    D -- No --> E[/Lanzar RegexSyntaxException:<br/>token inesperado/]
    D -- Sí --> F[/Retornar raiz/]
    E --> G([Fin])
    F --> G
```

## 2.2 `parseExpr()` — unión

```mermaid
flowchart TD
    A([Inicio]) --> B[izq ← parseTerm]
    B --> C{"¿token actual es '|'?"}
    C -- Sí --> D["consumir '|'"]
    D --> E[der ← parseTerm]
    E --> F[izq ← UnionNode izq, der]
    F --> C
    C -- No --> G[/Retornar izq/]
    G --> H([Fin])
```

## 2.3 `parseTerm()` — concatenación

```mermaid
flowchart TD
    A([Inicio]) --> B[izq ← parseFactor]
    B --> C{¿token actual es CONCAT?}
    C -- Sí --> D[consumir CONCAT]
    D --> E[der ← parseFactor]
    E --> F[izq ← ConcatNode izq, der]
    F --> C
    C -- No --> G[/Retornar izq/]
    G --> H([Fin])
```

## 2.4 `parseFactor()` — operadores unarios

```mermaid
flowchart TD
    A([Inicio]) --> B[nodo ← parseAtom]
    B --> C{¿token actual?}
    C -- "*" --> D[consumir<br/>nodo ← StarNode nodo]
    C -- "+" --> E[consumir<br/>nodo ← PlusNode nodo]
    C -- "?" --> F[consumir<br/>nodo ← OptionalNode nodo]
    D --> C
    E --> C
    F --> C
    C -- otro --> G[/Retornar nodo/]
    G --> H([Fin])
```

## 2.5 `parseAtom()`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿token actual?}
    B -- SYMBOL --> C[consumir<br/>nodo ← SymbolNode símbolo]
    C --> R[/Retornar nodo/]
    B -- "(" --> D["consumir '('"]
    D --> E[nodo ← parseExpr]
    E --> F{"¿token actual es ')'?"}
    F -- Sí --> G["consumir ')'"]
    G --> R
    F -- No --> ERR[/"Lanzar RegexSyntaxException:<br/>falta ')'"/]
    B -- "otro o fin" --> ERR2[/"Lanzar RegexSyntaxException:<br/>se esperaba símbolo o '('"/]
    R --> Z([Fin])
    ERR --> Z
    ERR2 --> Z
```
