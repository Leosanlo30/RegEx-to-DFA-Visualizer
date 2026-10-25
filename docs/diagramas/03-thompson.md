# 3. Construcción de Thompson (AST → NFA-ε)

`ThompsonBuilder` recorre el AST con el patrón **Visitor** (post-orden).
Cada visita retorna un **fragmento** `(inicio, fin)` con un único estado final.

## 3.1 Algoritmo general

```mermaid
flowchart TD
    A([Inicio]) --> B[/Recibir raíz del AST/]
    B --> C[frag ← visitar raíz]
    C --> D[NFA.inicial ← frag.inicio<br/>NFA.finales ← frag.fin]
    D --> E[/Retornar NFA/]
    E --> F([Fin])
```

## 3.2 `visitar(nodo)` — despacho por tipo de nodo

```mermaid
flowchart TD
    A([visitar nodo]) --> B{¿Tipo de nodo?}

    B -- SymbolNode a --> S1[i, f ← nuevos estados]
    S1 --> S2[i —a→ f]
    S2 --> RET

    B -- ConcatNode --> C1[A ← visitar izq<br/>B ← visitar der]
    C1 --> C2[A.fin —ε→ B.inicio]
    C2 --> C3[frag ← A.inicio, B.fin]
    C3 --> RET2

    B -- UnionNode --> U1[A ← visitar izq<br/>B ← visitar der]
    U1 --> U2[i, f ← nuevos estados]
    U2 --> U3[i —ε→ A.inicio<br/>i —ε→ B.inicio<br/>A.fin —ε→ f<br/>B.fin —ε→ f]
    U3 --> RET

    B -- StarNode --> K1[A ← visitar hijo]
    K1 --> K2[i, f ← nuevos estados]
    K2 --> K3[i —ε→ A.inicio<br/>i —ε→ f<br/>A.fin —ε→ A.inicio<br/>A.fin —ε→ f]
    K3 --> RET

    B -- PlusNode --> P1[A ← visitar hijo]
    P1 --> P2[i, f ← nuevos estados]
    P2 --> P3[i —ε→ A.inicio<br/>A.fin —ε→ A.inicio<br/>A.fin —ε→ f]
    P3 --> RET

    B -- OptionalNode --> O1[A ← visitar hijo]
    O1 --> O2[i, f ← nuevos estados]
    O2 --> O3[i —ε→ A.inicio<br/>i —ε→ f<br/>A.fin —ε→ f]
    O3 --> RET

    RET[frag ← i, f] --> RET2[/Retornar frag/]
    RET2 --> Z([Fin])
```
