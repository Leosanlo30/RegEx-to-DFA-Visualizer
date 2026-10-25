# 7. Modelo: autómatas (`model/automata`)

```mermaid
classDiagram
    class State {
        -int id
        -String label
        +getId() int
        +equals(Object) boolean
        +hashCode() int
    }
    class Transition {
        -State from
        -Character symbol
        -State to
        +isEpsilon() boolean
    }
    class Automaton {
        <<abstract>>
        #Set~State~ states
        #Set~Character~ alphabet
        #List~Transition~ transitions
        #State initial
        #Set~State~ finals
        +addState() State
        +addTransition(State, Character, State)
        +setInitial(State)
        +addFinal(State)
        +isFinal(State) boolean
        +getTransitionsFrom(State) List~Transition~
    }
    class NFA {
        +addEpsilon(State, State)
    }
    class DFA {
        -Map transitionTable
        +next(State, char) State
        +accepts(String) boolean
        +isComplete() boolean
        +complete() State
        +removeUnreachable()
    }
    Automaton <|-- NFA
    Automaton <|-- DFA
    Automaton "1" o-- "*" State
    Automaton "1" o-- "*" Transition
    Transition --> State
```

> En el código, ε es `symbol == null` (o la constante `Transition.EPSILON`).

---

## 7.1 `State.equals(Object o)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿o es este mismo objeto?}
    B -- Sí --> T[/Retornar true/]
    B -- No --> C{¿o es null o no es State?}
    C -- Sí --> F[/Retornar false/]
    C -- No --> D{¿o.id = this.id?}
    D -- Sí --> T
    D -- No --> F
    T --> Z([Fin])
    F --> Z
```

`hashCode()` retorna el hash de `id`, para que los estados funcionen en `Set` y `Map`.

## 7.2 `Transition.isEpsilon()`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿symbol es null?}
    B -- Sí --> T[/Retornar true/]
    B -- No --> F[/Retornar false/]
    T --> Z([Fin])
    F --> Z
```

## 7.3 `Automaton.addState()`

```mermaid
flowchart TD
    A([Inicio]) --> B[s ← nuevo State con id = contador]
    B --> C[contador ← contador + 1]
    C --> D[agregar s a states]
    D --> E[/Retornar s/]
    E --> Z([Fin])
```

## 7.4 `Automaton.addTransition(from, symbol, to)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿from y to pertenecen a states?}
    B -- No --> ERR[/Lanzar IllegalArgumentException/]
    B -- Sí --> C[t ← nueva Transition from, symbol, to]
    C --> D[agregar t a transitions]
    D --> E{¿symbol es null?}
    E -- No --> F[agregar symbol a alphabet]
    E -- Sí --> G
    F --> G[/Fin del método/]
    G --> Z([Fin])
    ERR --> Z
```

`NFA.addEpsilon(from, to)` solo llama a `addTransition(from, null, to)`.

## 7.5 `Automaton.setInitial(s)` y `addFinal(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿s pertenece a states?}
    B -- No --> ERR[/Lanzar IllegalArgumentException/]
    B -- Sí --> C{¿Qué método?}
    C -- setInitial --> D[initial ← s]
    C -- addFinal --> E[agregar s a finals]
    D --> Z([Fin])
    E --> Z
    ERR --> Z
```

## 7.6 `Automaton.isFinal(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿finals contiene s?}
    B -- Sí --> T[/Retornar true/]
    B -- No --> F[/Retornar false/]
    T --> Z([Fin])
    F --> Z
```

## 7.7 `Automaton.getTransitionsFrom(s)`

```mermaid
flowchart TD
    A([Inicio]) --> B[resultado ← lista vacía]
    B --> C{¿quedan transiciones t?}
    C -- No --> R[/Retornar resultado/]
    C -- Sí --> D{¿t.from = s?}
    D -- Sí --> E[agregar t a resultado]
    D -- No --> C
    E --> C
    R --> Z([Fin])
```

---

## 7.8 `DFA.addTransition(from, symbol, to)` — sobrescrito

En un DFA no puede haber ε ni dos transiciones con el mismo símbolo desde un estado.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿symbol es null?}
    B -- Sí --> ERR1[/Lanzar IllegalArgumentException:<br/>un DFA no admite ε/]
    B -- No --> C{¿transitionTable ya tiene<br/>from con symbol?}
    C -- Sí --> ERR2[/Lanzar IllegalStateException:<br/>no determinista/]
    C -- No --> D[super.addTransition from, symbol, to]
    D --> E[transitionTable from, symbol ← to]
    E --> Z([Fin])
    ERR1 --> Z
    ERR2 --> Z
```

## 7.9 `DFA.next(s, c)`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿transitionTable tiene s con c?}
    B -- Sí --> C[/Retornar estado destino/]
    B -- No --> D[/Retornar null/]
    C --> Z([Fin])
    D --> Z
```

`DFA.accepts(w)` usa `next` en un ciclo; su diagrama está en [06-simulacion-dfa.md](06-simulacion-dfa.md).

## 7.10 `DFA.isComplete()`

```mermaid
flowchart TD
    A([Inicio]) --> B{¿quedan estados s?}
    B -- No --> T[/Retornar true/]
    B -- Sí --> C{¿quedan símbolos c<br/>del alfabeto para s?}
    C -- No --> B
    C -- Sí --> D{¿next s, c es null?}
    D -- Sí --> F[/Retornar false/]
    D -- No --> C
    T --> Z([Fin])
    F --> Z
```

## 7.11 `DFA.complete()` — agrega el estado sumidero

```mermaid
flowchart TD
    A([Inicio]) --> B{¿isComplete?}
    B -- Sí --> N[/Retornar null:<br/>no hizo falta sumidero/]
    B -- No --> C[sumidero ← addState]
    C --> D[Para cada estado s, incluido el sumidero,<br/>y cada símbolo c del alfabeto:<br/>si next s, c es null, agregar s —c→ sumidero]
    D --> E[/Retornar sumidero/]
    N --> Z([Fin])
    E --> Z
```

## 7.12 `DFA.removeUnreachable()`

```mermaid
flowchart TD
    A([Inicio]) --> B[alcanzables ← conjunto con initial<br/>pila ← initial]
    B --> C{¿pila vacía?}
    C -- No --> D[s ← pila.pop]
    D --> E[Por cada transición s —c→ t:<br/>si t no está en alcanzables,<br/>agregarlo y hacer pila.push t]
    E --> C
    C -- Sí --> F[Quitar de states y finals<br/>los estados no alcanzables]
    F --> G[Quitar transiciones cuyo origen<br/>o destino fue eliminado]
    G --> Z([Fin])
```
