# 0. Pipeline general

Flujo desde que el usuario presiona "Convertir" hasta que se muestra el DFA mínimo.
Clases según [UML.md](../UML.md).

## 0.1 `MainWindow.onConvert()`

```mermaid
flowchart TD
    A([Inicio]) --> B[/regex ← texto del campo de entrada/]
    B --> C[currentResult ← converter.convert regex]
    C --> D{¿Se lanzó RegexInvalidException?}
    D -- Sí --> E[/Mostrar mensaje de error<br/>con la posición del fallo/]
    D -- No --> F[AutomatonRenderer.draw currentResult.minimalDfa]
    F --> G[Actualizar TransitionsTableModel<br/>con currentResult.minimalDfa]
    G --> H[/Mostrar diagrama y tabla/]
    E --> Z([Fin])
    H --> Z
```

## 0.2 `RegexConverter.convert(regex)`

```mermaid
flowchart TD
    A([Inicio]) --> B[/Recibir regex/]
    B --> C[tokens ← new Lexer regex .tokenize]
    C --> D[root ← new Parser tokens .parse]
    D --> E[nfa ← thompson.build root]
    E --> F[dfa ← subsets.determinize nfa]
    F --> G[minimalDfa ← minimizer.minimize dfa]
    G --> H[result ← new ConversionResult<br/>regex, nfa, dfa, minimalDfa]
    H --> I[/Retornar result/]
    I --> Z([Fin])
```

> `Lexer` y `Parser` lanzan `RegexInvalidException`; `convert` no la atrapa, la propaga a `MainWindow`.
