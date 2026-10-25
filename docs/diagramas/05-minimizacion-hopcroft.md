# 5. Minimización de DFA (Hopcroft)

Requisito: DFA **completo** (si falta alguna transición, se agrega un estado sumidero).
Se parte los estados en clases de equivalencia; cada clase final es un estado del DFA mínimo.

## 5.1 Preparación

```mermaid
flowchart TD
    A([Inicio]) --> B[/Recibir DFA/]
    B --> C[Eliminar estados inalcanzables<br/>desde el inicial]
    C --> D{¿DFA completo?}
    D -- No --> E[Agregar estado sumidero<br/>y completar transiciones faltantes]
    D -- Sí --> F
    E --> F[F ← finales<br/>N ← no finales]
    F --> G[P ← particiones no vacías entre F y N<br/>W ← la menor entre F y N]
    G --> H([Ir a refinamiento])
```

## 5.2 Refinamiento de particiones

```mermaid
flowchart TD
    A([Inicio refinamiento]) --> B{¿W vacío?}
    B -- Sí --> Q([Ir a construcción del DFA mínimo])
    B -- No --> C[A ← W.extraer]
    C --> D{¿quedan símbolos c ∈ Σ?}
    D -- No --> B
    D -- Sí --> E[X ← estados con transición<br/>por c hacia algún estado de A]
    E --> F{¿quedan bloques Y ∈ P<br/>sin revisar?}
    F -- No --> D
    F -- Sí --> G[Y1 ← Y ∩ X<br/>Y2 ← Y − X]
    G --> H{¿Y1 y Y2 no vacíos?}
    H -- No --> F
    H -- Sí --> I[Reemplazar Y en P por Y1 y Y2]
    I --> J{¿Y está en W?}
    J -- Sí --> K[Reemplazar Y en W por Y1 y Y2]
    J -- No --> L[Agregar a W el menor entre Y1 y Y2]
    K --> F
    L --> F
```

## 5.3 Construcción del DFA mínimo

```mermaid
flowchart TD
    A([Inicio]) --> B[Por cada bloque de P<br/>crear un estado nuevo]
    B --> C[Inicial ← bloque que contiene<br/>el inicial original]
    C --> D[Finales ← bloques que contienen<br/>estados finales originales]
    D --> E[Por cada bloque B y símbolo c:<br/>tomar un representante s de B<br/>agregar B —c→ bloque de δ s, c]
    E --> F[Opcional: quitar estado sumidero<br/>para la visualización]
    F --> G[/Retornar DFA mínimo/]
    G --> H([Fin])
```
