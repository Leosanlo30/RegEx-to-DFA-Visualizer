# Diagramas de flujo de los algoritmos

Diagramas en [Mermaid](https://mermaid.js.org/) (GitHub los renderiza automáticamente).

| # | Archivo | Algoritmo | Clase |
|---|---------|-----------|-------|
| 0 | [00-pipeline.md](00-pipeline.md) | Flujo general | `ConversionController` |
| 1 | [01-lexer.md](01-lexer.md) | Tokenización + concatenación implícita | `Lexer` |
| 2 | [02-parser.md](02-parser.md) | Descenso recursivo → AST | `Parser` |
| 3 | [03-thompson.md](03-thompson.md) | Construcción de Thompson → NFA-ε | `ThompsonBuilder` |
| 4 | [04-subconjuntos.md](04-subconjuntos.md) | ε-closure, move, subconjuntos → DFA | `SubsetConstruction` |
| 5 | [05-minimizacion-hopcroft.md](05-minimizacion-hopcroft.md) | Minimización de Hopcroft | `DfaMinimizer` |
| 6 | [06-simulacion-dfa.md](06-simulacion-dfa.md) | Aceptación de cadenas | `DFA` |
| 7 | [07-modelo-automatas.md](07-modelo-automatas.md) | Métodos de los autómatas | `State`, `Transition`, `Automaton`, `NFA`, `DFA` |
| 8 | [08-modelo-ast.md](08-modelo-ast.md) | Métodos del AST (Composite + Visitor) | `RegexNode` y subclases |

Ejemplo de referencia para pruebas: `(a|b)*abb`.
