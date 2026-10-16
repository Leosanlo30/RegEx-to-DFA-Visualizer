# Design

Software-design document for **Regex DFA Visualizer**. Covers the package layout, the classes with their attributes and methods, and how inheritance, polymorphism, and interfaces are applied.

Visibility notation used throughout: `+` public, `-` private, `#` protected. Getters, setters, and constructors are omitted.

## 1. Class breakdown by package

### `modelo` — Automata and syntax tree

#### `Autómata` (abstract base)

| Visibility | Member |
|------------|--------|
| `#` | `estados` |
| `#` | `alfabeto` |
| `#` | `inicial` |
| `#` | `finales` |
| `+` | `agregarEstado(e : Estado)` |
| `+` | `marcarFinal(e : Estado)` |
| `+` | `esFinal(e : Estado)` |
| `+` | `acepta(cadena : String)` |

#### `Simulable` «interface»

| Visibility | Member |
|------------|--------|
| `+` | `acepta(cadena : String)` |

#### `AFN`

| Visibility | Member |
|------------|--------|
| `+` | `EPSILON` |
| `-` | `delta` |
| `+` | `nuevoEstado()` |
| `+` | `agregarTransicion(o : Estado, c : char, d : Estado)` |
| `+` | `cerraduraEpsilon(s : Set<Estado>)` |
| `+` | `mover(s : Set<Estado>, c : char)` |
| `+` | `acepta(cadena : String)` |

#### `AFD`

| Visibility | Member |
|------------|--------|
| `-` | `delta` |
| `+` | `agregarTransicion(o : Estado, c : char, d : Estado)` |
| `+` | `transicion(e : Estado, c : char)` |
| `+` | `completar()` |
| `+` | `acepta(cadena : String)` |

#### `Estado`

| Visibility | Member |
|------------|--------|
| `-` | `id` |
| `+` | `equals(o : Object)` |
| `+` | `hashCode()` |

#### `Fragmento`

| Visibility | Member |
|------------|--------|
| `-` | `inicio` |
| `-` | `fin` |

#### Syntax-tree node classes

| Class | Attributes | Methods |
|-------|------------|---------|
| `NodoRegex` (abstract base) | — | `+construirAFN(afn : AFN)` |
| `NodoSimbolo` | `-simbolo` | `+construirAFN(afn : AFN)` |
| `NodoEpsilon` | — | `+construirAFN(afn : AFN)` |
| `NodoUnion` | `-izq`, `-der` | `+construirAFN(afn : AFN)` |
| `NodoConcatenacion` | `-izq`, `-der` | `+construirAFN(afn : AFN)` |
| `NodoKleene` | `-hijo` | `+construirAFN(afn : AFN)` |

### `logica` — Conversion algorithms

#### `Lexer`

| Visibility | Member |
|------------|--------|
| `-` | `regex` |
| `+` | `tokenizar()` |
| `-` | `insertarConcatenacion(tokens : List<Token>)` |

#### `Parser`

| Visibility | Member |
|------------|--------|
| `-` | `tokens` |
| `+` | `parsear()` |
| `+` | `aPostfija()` |
| `-` | `construirArbol(postfija : List<Token>)` |
| `-` | `precedencia(tipo : TipoToken)` |

#### `Token`

| Visibility | Member |
|------------|--------|
| `-` | `tipo` |
| `-` | `valor` |
| `-` | `posicion` |

#### `TipoToken` «enumeration»

Values: `SÍMBOLO`, `EPSILON`, `UNIÓN`, `CONCAT`, `KLEENE`, `PAR_IZQ`, `PAR_DER`.

#### `Convertidor` «interface»

| Visibility | Member |
|------------|--------|
| `+` | `convertir(regex : String)` |

#### `ConvertidorRegex`

| Visibility | Member |
|------------|--------|
| `-` | `thompson` |
| `-` | `subconjuntos` |
| `-` | `minimizador` |
| `+` | `convertir(regex : String)` |

#### `ConvertidorThompson`

| Visibility | Member |
|------------|--------|
| `+` | `construir(raiz : NodoRegex)` |

#### `ConstructorSubconjuntos`

| Visibility | Member |
|------------|--------|
| `+` | `determinizar(afn : AFN)` |

#### `Minimizador`

| Visibility | Member |
|------------|--------|
| `+` | `minimizar(afd : AFD)` |

#### `ResultadoConversion`

| Visibility | Member |
|------------|--------|
| `-` | `regex` |
| `-` | `afn` |
| `-` | `afd` |
| `-` | `afdMinimo` |

### `persistencia` — Saving and loading conversions

#### `Repositorio<T>` «interface»

| Visibility | Member |
|------------|--------|
| `+` | `guardar(obj : T, archivo : File)` |
| `+` | `cargar(archivo : File)` |

#### `RepositorioJson`

| Visibility | Member |
|------------|--------|
| `-` | `gson` |
| `+` | `guardar(r : ResultadoConversion, archivo : File)` |
| `+` | `cargar(archivo : File)` |

### `excepciones` — Custom program errors

| Class | Attributes |
|-------|------------|
| `RegexInvalidaException` | `-posicion` |
| `ArchivoCorruptoException` | `-ruta` |

### `interfaz` — Graphical user interface

#### `VentanaPrincipal`

| Visibility | Member |
|------------|--------|
| `-` | `convertidor` |
| `-` | `repositorio` |
| `-` | `resultadoActual` |
| `-` | `alConvertir()` |
| `-` | `alProbarCadena()` |
| `-` | `alGuardar()` |
| `-` | `alCargar()` |

#### `MenuSintaxis`

| Visibility | Member |
|------------|--------|
| `-` | `campoDestino` |
| `+` | `insertar(texto : String)` |

#### `ModeloTablaTransiciones`

| Visibility | Member |
|------------|--------|
| `-` | `afd` |
| `+` | `getValueAt(fila : int, col : int)` |

#### `RenderizadorAutomata`

| Visibility | Member |
|------------|--------|
| `+` | `dibujar(afd : AFD)` |

## 2. UML diagrams

See `DiagramaUMLPOO.pdf` for the full class-diagram artifact.

## 3. Inheritance, polymorphism, and interfaces

### 3.1 Inheritance

- **`Autómata`** is the base class for finite automata. Two types inherit from it: the non-deterministic finite automaton (**AFN**) and the deterministic finite automaton (**AFD**). Both share the states, the alphabet, the initial state, and the accepting states; each one defines its own transitions.
- **`NodoRegex`** represents a part of the regular expression. Five node types inherit from it: symbol, empty string, union, concatenation, and Kleene star.
- **Exceptions:** `RegexInvalidaException` and `ArchivoCorruptoException` extend Java's `Exception` class.
- **GUI:** the main window, the syntax menu, and the table model extend Swing components (`JFrame`, `JMenu`, and `AbstractTableModel`).

### 3.2 Polymorphism

- **NFA construction:** every regex node exposes `construirAFN()`, but each node type builds its share of the automaton differently. The program walks the syntax tree calling the same method without needing to know which concrete node it is dealing with.
- **String evaluation:** both the AFN and the AFD expose `acepta()`, but each one evaluates the string in its own way — the AFN can be in several states at once, the AFD only in one.

### 3.3 Interfaces

- **`Simulable`** — defines the ability to evaluate a string. Implemented by the automata.
- **`Convertidor`** — defines the operation of converting a regular expression into an automaton.
- **`Repositorio`** — defines how a conversion is saved and loaded; implemented over JSON.
