# 7. Métodos del modelo de autómatas

Métodos de `State`, `Automaton`, `NFA` y `DFA` según [UML.md](../UML.md).
Los demás métodos de estas clases están en otros diagramas:

- `NFA.epsilonClosure` y `NFA.move`: [04-subconjuntos.md](04-subconjuntos.md)
- `accepts` de `NFA` y `DFA`: [06-simulacion-dfa.md](06-simulacion-dfa.md)

## 7.1 `State.equals(o)` y `hashCode()`

```mermaid
flowchart TD
    A([Inicio equals]) --> B{¿o es este mismo objeto?}
    B -- Sí --> T[/Retornar true/]
    B -- No --> C{¿o es null o no es State?}
    C -- Sí --> F[/Retornar false/]
    C -- No --> D{¿o.id = this.id?}
    D -- Sí --> T
    D -- No --> F
    T --> Z([Fin])
    F --> Z
```

`hashCode()` retorna `Integer.hashCode(id)`. Es obligatorio sobrescribir los dos para que `State` funcione en `Set` y `Map`.

## 7.2 `Automaton.addState(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿s es null?}
    B -- Sí --> ERR[/Lanzar IllegalArgumentException/]
    B -- No --> C[agregar s a states]
    C --> Z([Fin])
    ERR --> Z
```

## 7.3 `Automaton.markFinal(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿states contiene s?}
    B -- No --> ERR[/Lanzar IllegalArgumentException/]
    B -- Sí --> C[agregar s a finals]
    C --> Z([Fin])
    ERR --> Z
```

## 7.4 `Automaton.isFinal(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿finals contiene s?}
    B -- Sí --> T[/Retornar true/]
    B -- No --> F[/Retornar false/]
    T --> Z([Fin])
    F --> Z
```

---

## 7.5 `NFA.newState()`

```mermaid
flowchart TD
    A([Inicio]) --> B[s ← new State contador]
    B --> C[contador ← contador + 1]
    C --> D[addState s]
    D --> E[/Retornar s/]
    E --> Z([Fin])
```

## 7.6 `NFA.addTransition(src, c, dst)`

`delta: Map<State, Map<Character, Set<State>>>` — un mismo símbolo puede ir a varios estados.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿states contiene src y dst?}
    B -- No --> ERR[/Lanzar IllegalArgumentException/]
    B -- Sí --> C{¿delta tiene src?}
    C -- No --> D[delta src ← nuevo Map]
    C -- Sí --> E
    D --> E{¿delta src tiene c?}
    E -- No --> F[delta src, c ← nuevo Set]
    E -- Sí --> G
    F --> G[agregar dst a delta src, c]
    G --> H{¿c ≠ EPSILON?}
    H -- Sí --> I[agregar c a alphabet]
    H -- No --> Z([Fin])
    I --> Z
    ERR --> Z
```

---

## 7.7 `DFA.addTransition(src, c, dst)`

`delta: Map<State, Map<Character, State>>` — cada símbolo va a **un solo** estado.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿states contiene src y dst?}
    B -- No --> ERR[/Lanzar IllegalArgumentException/]
    B -- Sí --> C{¿delta tiene src?}
    C -- No --> D[delta src ← nuevo Map]
    C -- Sí --> E
    D --> E{"¿delta src ya tiene c<br/>con un destino ≠ dst?"}
    E -- Sí --> ERR2[/Lanzar IllegalStateException:<br/>dejaría de ser determinista/]
    E -- No --> F[delta src, c ← dst<br/>agregar c a alphabet]
    F --> Z([Fin])
    ERR --> Z
    ERR2 --> Z
```

## 7.8 `DFA.transition(s, c)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿delta tiene s Y<br/>delta s tiene c?}
    B -- Sí --> C[/Retornar delta s, c/]
    B -- No --> D[/Retornar null/]
    C --> Z([Fin])
    D --> Z
```

## 7.9 `DFA.complete()`

Deja `transition` como función total agregando un estado **sumidero**.

```mermaid
flowchart TD
    A([Inicio]) --> B[faltan ← false]
    B --> C[Revisar cada estado s y símbolo c:<br/>si transition s, c es null, faltan ← true]
    C --> D{¿faltan?}
    D -- No --> Z([Fin])
    D -- Sí --> E[sumidero ← new State con id = mayor id + 1<br/>addState sumidero]
    E --> F{¿quedan pares s, c<br/>incluido el sumidero?}
    F -- No --> Z
    F -- Sí --> G{¿transition s, c es null?}
    G -- Sí --> H[addTransition s, c, sumidero]
    G -- No --> F
    H --> F
```
