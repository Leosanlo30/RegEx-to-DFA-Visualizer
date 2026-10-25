# 8. Modelo: AST de la regex (`model/ast`)

Patrón **Composite** (nodos hoja y compuestos) + **Visitor** (operaciones sobre el árbol).

```mermaid
classDiagram
    class RegexNode {
        <<abstract>>
        +accept(RegexVisitor~T~) T
        +toString() String
    }
    class SymbolNode {
        -char symbol
    }
    class BinaryNode {
        <<abstract>>
        #RegexNode left
        #RegexNode right
    }
    class UnaryNode {
        <<abstract>>
        #RegexNode child
    }
    class RegexVisitor~T~ {
        <<interface>>
        +visitSymbol(SymbolNode) T
        +visitConcat(ConcatNode) T
        +visitUnion(UnionNode) T
        +visitStar(StarNode) T
        +visitPlus(PlusNode) T
        +visitOptional(OptionalNode) T
    }
    RegexNode <|-- SymbolNode
    RegexNode <|-- BinaryNode
    RegexNode <|-- UnaryNode
    BinaryNode <|-- ConcatNode
    BinaryNode <|-- UnionNode
    UnaryNode <|-- StarNode
    UnaryNode <|-- PlusNode
    UnaryNode <|-- OptionalNode
    BinaryNode o-- "2" RegexNode
    UnaryNode o-- "1" RegexNode
    RegexNode ..> RegexVisitor : accept
```

`ThompsonBuilder` implementa `RegexVisitor<Fragment>`; su diagrama está en [03-thompson.md](03-thompson.md).

---

## 8.1 Constructores

```mermaid
flowchart TD
    A([Inicio]) --> B{¿Tipo de nodo?}
    B -- SymbolNode --> C[this.symbol ← symbol]
    B -- BinaryNode --> D{¿left o right es null?}
    D -- Sí --> ERR[/Lanzar IllegalArgumentException/]
    D -- No --> E[this.left ← left<br/>this.right ← right]
    B -- UnaryNode --> F{¿child es null?}
    F -- Sí --> ERR
    F -- No --> G[this.child ← child]
    C --> Z([Fin])
    E --> Z
    G --> Z
    ERR --> Z
```

## 8.2 `accept(visitor)` — double dispatch

Cada subclase concreta llama al método del visitor que le corresponde.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿Clase concreta de this?}
    B -- SymbolNode --> C[/Retornar visitor.visitSymbol this/]
    B -- ConcatNode --> D[/Retornar visitor.visitConcat this/]
    B -- UnionNode --> E[/Retornar visitor.visitUnion this/]
    B -- StarNode --> F[/Retornar visitor.visitStar this/]
    B -- PlusNode --> G[/Retornar visitor.visitPlus this/]
    B -- OptionalNode --> H[/Retornar visitor.visitOptional this/]
    C --> Z([Fin])
    D --> Z
    E --> Z
    F --> Z
    G --> Z
    H --> Z
```

> No hay un `if` real: el polimorfismo elige el método. El diagrama solo muestra qué hace cada subclase.

## 8.3 `toString()` — reconstruye la regex con paréntesis

Útil para depurar el parser y mostrar el AST en la vista.

```mermaid
flowchart TD
    A([Inicio]) --> B{¿Tipo de nodo?}
    B -- SymbolNode --> C[/Retornar el símbolo/]
    B -- ConcatNode --> D["Retornar '(' + left + right + ')'"]
    B -- UnionNode --> E["Retornar '(' + left + '|' + right + ')'"]
    B -- StarNode --> F["Retornar child + '*'"]
    B -- PlusNode --> G["Retornar child + '+'"]
    B -- OptionalNode --> H["Retornar child + '?'"]
    C --> Z([Fin])
    D --> Z
    E --> Z
    F --> Z
    G --> Z
    H --> Z
```

Ejemplo: `(a|b)*abb` → `((((a|b)*a)b)b)`.
