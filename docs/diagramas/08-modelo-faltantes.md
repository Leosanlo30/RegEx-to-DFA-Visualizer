# 8. Métodos del modelo que faltaban

Constructores y métodos auxiliares de `Automaton`, `Fragment`, `State` y los nodos del árbol (`RegexNode`).
Son los que necesita Thompson ([03-thompson.md](03-thompson.md)) para funcionar y que todavía no tenían diagrama.

---

## 8.1 `Automaton()` — constructor vacío

Lo usa `ThompsonConverter.build` para crear un `NFA` desde cero.

```mermaid
flowchart TD
    A([Inicio]) --> B[states ← new HashSet<br/>finals ← new HashSet<br/>alphabet ← new HashSet]
    B --> C[initial ← null<br/>nextId ← 0]
    C --> Z([Fin])
```

## 8.2 `Automaton(initial, finals, alphabet, states)` — constructor con datos

Lo usan `SubsetConstructor` y `Minimizer` cuando ya tienen los conjuntos armados.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿algún conjunto es null?}
    B -- Sí --> ERR[/Lanzar IllegalArgumentException/]
    B -- No --> C{¿initial ≠ null y<br/>states no contiene initial?}
    C -- Sí --> ERR
    C -- No --> D{¿states contiene<br/>todos los finals?}
    D -- No --> ERR
    D -- Sí --> E[asignar los cuatro atributos]
    E --> F[nextId ← mayor id de states + 1<br/>o 0 si states está vacío]
    F --> Z([Fin])
    ERR --> Z
```

## 8.3 `Automaton.newState()`

```mermaid
flowchart TD
    A([Inicio]) --> B[s ← new State nextId]
    B --> C[nextId ← nextId + 1]
    C --> D[addState s]
    D --> E[/Retornar s/]
    E --> Z([Fin])
```

## 8.4 `Automaton.hasState(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿states contiene s?}
    B -- Sí --> T[/Retornar true/]
    B -- No --> F[/Retornar false/]
    T --> Z([Fin])
    F --> Z
```

---

## 8.5 `Fragment(start, end)` y getters

```mermaid
flowchart TD
    A([Inicio]) --> B{¿start o end es null?}
    B -- Sí --> ERR[/Lanzar IllegalArgumentException/]
    B -- No --> C[this.start ← start<br/>this.end ← end]
    C --> Z([Fin])
    ERR --> Z
```

`getStart()` y `getEnd()` solo retornan el atributo. No hay setters: un fragmento no cambia después de creado.

## 8.6 `State.toString()`

```mermaid
flowchart TD
    A([Inicio]) --> B[/"Retornar 'q' + id"/]
    B --> Z([Fin])
```

Lo usan `TransitionsTableModel` y `AutomatonRenderer` para mostrar `q0`, `q1`, …

---

## 8.7 Constructores de los nodos del árbol

`EpsilonNode()` no tiene atributos, así que su constructor está vacío.

### `SymbolNode(symbol)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿symbol es letra o dígito?}
    B -- No --> ERR[/Lanzar IllegalArgumentException/]
    B -- Sí --> C[this.symbol ← symbol]
    C --> Z([Fin])
    ERR --> Z
```

### `UnionNode(left, right)` y `ConcatenationNode(left, right)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿left o right es null?}
    B -- Sí --> ERR[/Lanzar IllegalArgumentException/]
    B -- No --> C[this.left ← left<br/>this.right ← right]
    C --> Z([Fin])
    ERR --> Z
```

### `KleeneNode(child)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿child es null?}
    B -- Sí --> ERR[/Lanzar IllegalArgumentException/]
    B -- No --> C[this.child ← child]
    C --> Z([Fin])
    ERR --> Z
```

### Ejemplo: árbol de `(a|b)*abb`

Así queda el árbol que `Parser.buildTree` arma con estos constructores (concatenación asociativa por la izquierda):

```mermaid
flowchart TD
    C3["ConcatenationNode"] --> C2["ConcatenationNode"]
    C3 --> B2["SymbolNode 'b'"]
    C2 --> C1["ConcatenationNode"]
    C2 --> B1["SymbolNode 'b'"]
    C1 --> K["KleeneNode"]
    C1 --> A2["SymbolNode 'a'"]
    K --> U["UnionNode"]
    U --> A1["SymbolNode 'a'"]
    U --> B0["SymbolNode 'b'"]
```
