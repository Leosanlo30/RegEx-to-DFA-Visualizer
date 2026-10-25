# Diagramas de flujo de los algoritmos

Diagramas en [Mermaid](https://mermaid.js.org/) (GitHub los renderiza automáticamente).
Las clases y métodos siguen [docs/UML.md](../UML.md); la sintaxis y el método de minimización, [docs/REQUIREMENTS.md](../REQUIREMENTS.md).

| # | Archivo | Métodos |
|---|---------|---------|
| 0 | [00-pipeline.md](00-pipeline.md) | `MainWindow.onConvert`, `RegexConverter.convert` |
| 1 | [01-lexer.md](01-lexer.md) | `Lexer.tokenize`, `Lexer.insertConcatenation` |
| 2 | [02-parser.md](02-parser.md) | `Parser.parse`, `precedence`, `toPostfix`, `buildTree` |
| 3 | [03-thompson.md](03-thompson.md) | `ThompsonConverter.build`, `buildNFA` de cada `RegexNode` |
| 4 | [04-subconjuntos.md](04-subconjuntos.md) | `NFA.epsilonClosure`, `NFA.move`, `SubsetConstructor.determinize` |
| 5 | [05-minimizacion.md](05-minimizacion.md) | `Minimizer.minimize` (tabla de pares distinguibles) |
| 6 | [06-simulacion-dfa.md](06-simulacion-dfa.md) | `DFA.accepts`, `NFA.accepts` |
| 7 | [07-modelo-automatas.md](07-modelo-automatas.md) | `State`, `Automaton`, `NFA.newState/addTransition`, `DFA.addTransition/transition/complete` |

Ejemplo de referencia para pruebas: `(a|b)*abb`.

Explicación de cada método: [EXPLICACIONES.md](EXPLICACIONES.md).
