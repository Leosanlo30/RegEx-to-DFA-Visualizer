# Explicación de cada método

Complemento de los diagramas de flujo de esta carpeta. Para cada método: **qué hace**, **cómo lo hace** y **por qué**.
Clases según [UML.md](../UML.md). Ejemplo usado en todo el documento: `(a|b)*abb`.

---

## 0. Pipeline — [00-pipeline.md](00-pipeline.md)

### `MainWindow.onConvert()` → `void`
- **Qué hace:** responde al botón "Convertir".
- **Cómo:** lee el texto del campo, llama a `converter.convert(regex)` y guarda el resultado en `currentResult`. Luego dibuja el DFA mínimo con `AutomatonRenderer` y llena la tabla con `TransitionsTableModel`.
- **Por qué:** la vista **no** convierte nada; solo pide y muestra. Si llega una `RegexInvalidException`, muestra el mensaje con la posición del error.

### `RegexConverter.convert(regex: String)` → `ConversionResult`
- **Qué hace:** ejecuta el pipeline completo: `Lexer → Parser → Thompson → Subconjuntos → Minimizador`.
- **Cómo:** cada etapa recibe la salida de la anterior. Al final empaqueta `regex`, `nfa`, `dfa` y `minimalDfa` en un `ConversionResult`.
- **Por qué:** concentra la orquestación en un solo lugar. `MainWindow` depende de la **interfaz** `Converter`, no de esta clase, y así se puede cambiar la implementación sin tocar la vista.

---

## 1. Lexer — [01-lexer.md](01-lexer.md)

### `tokenize()` → `List<Token>`
- **Qué hace:** convierte el texto `regex` en una lista de `Token(type, value, position)`.
- **Cómo:** recorre carácter por carácter. Las letras y los dígitos son `SYMBOL`; `ε` es `EPSILON`; `|`, `*`, `(` y `)` son sus operadores. Los espacios se ignoran y cualquier otro carácter lanza `RegexInvalidException` con su posición. Al final llama a `insertConcatenation`.
- **Por qué:** al parser le es más fácil trabajar con tokens tipados que con caracteres sueltos. La `position` permite decirle al usuario **dónde** está el error.

### `insertConcatenation(tokens)` → `List<Token>` (privado)
- **Qué hace:** vuelve explícita la concatenación, que en la regex es invisible: `ab` → `a · b`.
- **Cómo:** inserta `CONCAT` entre dos tokens cuando el anterior **termina** un operando (`SYMBOL`, `EPSILON`, `*`, `)`) y el actual **empieza** uno (`SYMBOL`, `EPSILON`, `(`).
- **Por qué:** shunting-yard solo maneja operadores que estén escritos. Ejemplo: `(a|b)*abb` → `( a | b ) * · a · b · b`.

---

## 2. Parser — [02-parser.md](02-parser.md)

### `parse()` → `RegexNode`
- **Qué hace:** retorna la raíz del árbol sintáctico.
- **Cómo:** `buildTree(toPostfix())`.
- **Por qué:** separa los dos pasos para poder probar cada uno por separado.

### `precedence(type: TokenType)` → `int` (privado)
- **Qué hace:** devuelve la prioridad de un operador: `KLEENE` = 3, `CONCAT` = 2, `UNION` = 1, cualquier otro = 0.
- **Por qué:** es la precedencia que pide RF02 (`*` > concatenación > `|`). Por eso `ab|c` significa `(ab)|c` y no `a(b|c)`.

### `toPostfix()` → `List<Token>`
- **Qué hace:** pasa los tokens de notación infija a **postfija** con el algoritmo shunting-yard de Dijkstra.
- **Cómo:**
  - Los operandos (`SYMBOL`, `EPSILON`) van directo a la salida.
  - `*` también va directo, porque ya está escrito después de su operando.
  - `(` entra a la pila; con `)` se sacan operadores hasta encontrar su `(`.
  - Antes de meter `|` o `·` a la pila, se sacan los operadores con precedencia **mayor o igual**. El "o igual" hace que sean asociativos por la izquierda.
  - Al terminar se vacía la pila. Si queda un `(`, falta un `)`.
- **Por qué:** en postfijo ya no hay paréntesis ni precedencias que resolver, así que construir el árbol se vuelve trivial.
- **Ejemplo:** `( a | b ) * · a · b · b` → `a b | * a · b · b ·`

### `buildTree(postfix)` → `RegexNode` (privado)
- **Qué hace:** construye el árbol a partir de la expresión postfija.
- **Cómo:** usa una pila de nodos.
  - Un operando apila `SymbolNode` o `EpsilonNode`.
  - `*` desapila 1 nodo y apila un `KleeneNode`.
  - `|` y `·` desapilan 2 nodos (**primero el derecho**, porque la pila invierte el orden) y apilan `UnionNode` o `ConcatenationNode`.
  - Al final debe quedar **exactamente un** nodo, que es la raíz.
- **Por qué:** si faltan operandos (`a|`, `*a`) o sobran (expresión vacía), la pila lo detecta y se lanza `RegexInvalidException`.

---

## 3. Thompson — [03-thompson.md](03-thompson.md)

Idea general: cada nodo del árbol produce un **fragmento** de NFA con un solo estado de entrada (`start`) y uno de salida (`end`). Los fragmentos se conectan con transiciones ε. Así cada nodo se construye solo con los fragmentos de sus hijos.

### `ThompsonConverter.build(root)` → `NFA`
- **Cómo:** crea un `NFA` vacío y llama a `root.buildNFA(nfa)`. El `start` del fragmento resultante pasa a ser el estado inicial y su `end` el único final.
- **Por qué:** gracias al **polimorfismo** no necesita saber de qué tipo es cada nodo; cada subclase sabe construirse sola.

### `RegexNode.buildNFA(nfa: NFA)` → `Fragment` (abstracto)
Recibe el `NFA` compartido, donde agrega sus estados y transiciones, y retorna su fragmento.

| Nodo | Construcción | Estados nuevos |
|---|---|---|
| `SymbolNode` | `i --a--> f` | 2 |
| `EpsilonNode` | `i --ε--> f` | 2 |
| `ConcatenationNode` | `A.end --ε--> B.start`; el fragmento es `(A.start, B.end)` | 0 |
| `UnionNode` | `i --ε--> A.start`, `i --ε--> B.start`, `A.end --ε--> f`, `B.end --ε--> f` | 2 |
| `KleeneNode` | `i --ε--> A.start`, `i --ε--> f` (**cero** repeticiones), `A.end --ε--> A.start` (**repetir**), `A.end --ε--> f` | 2 |

- **Por qué siempre funciona:** cada fragmento tiene una sola entrada y una sola salida, así que se puede conectar con cualquier otro sin romperlo. El NFA resultante tiene como máximo `2 × (número de símbolos y operadores)` estados.

---

## 4. Subconjuntos — [04-subconjuntos.md](04-subconjuntos.md)

### `NFA.epsilonClosure(s: Set<State>)` → `Set<State>`
- **Qué hace:** devuelve todos los estados a los que se llega desde `s` **sin consumir caracteres**, es decir, usando solo ε. Incluye los propios estados de `s`.
- **Cómo:** recorrido en profundidad con una pila. Cada estado nuevo se agrega al cierre y se apila; los ya visitados se ignoran, y así los ciclos ε no se repiten infinitamente.
- **Por qué:** en un NFA, "estar en q" en realidad significa estar también en todo su cierre ε.

### `NFA.move(s: Set<State>, c: char)` → `Set<State>`
- **Qué hace:** devuelve los estados a los que se llega desde `s` consumiendo **exactamente** el carácter `c` (un solo paso, sin ε).
- **Por qué:** se combina con el cierre: `epsilonClosure(move(T, c))` es "leer `c` y luego seguir todos los ε".

### `SubsetConstructor.determinize(nfa)` → `DFA`
- **Qué hace:** construye un DFA equivalente al NFA.
- **Cómo:**
  1. Cada estado del DFA representa un **conjunto** de estados del NFA. El estado inicial es `epsilonClosure({inicial})`.
  2. Una cola guarda los conjuntos pendientes. Por cada conjunto `T` y cada símbolo `c` se calcula `U = epsilonClosure(move(T, c))`.
  3. Si `U` es nuevo, se crea su estado y se encola. En cualquier caso se agrega la transición `T --c--> U`.
  4. Un conjunto es final si contiene **algún** estado final del NFA.
- **Por qué:** el DFA "simula en paralelo" todos los caminos posibles del NFA. Si `U` es vacío no se crea transición; el DFA queda parcial y lo completa el minimizador.

---

## 5. Minimización — [05-minimizacion.md](05-minimizacion.md)

### `Minimizer.minimize(dfa)` → `DFA`
- **Qué hace:** retorna el DFA con el **mínimo** número de estados que reconoce el mismo lenguaje.
- **Cómo:** con la tabla de pares distinguibles.
  1. `dfa.complete()`, para que `transition` nunca devuelva `null`.
  2. Se marca cada par `(p, q)` en el que uno es final y el otro no: la cadena vacía ya los distingue.
  3. Se repite hasta que no haya cambios: si con algún símbolo `c` el par `(transition(p,c), transition(q,c))` ya está marcado, se marca `(p, q)`. Si sus sucesores son distinguibles, ellos también lo son.
  4. Los pares que **quedan sin marcar** son equivalentes. Se agrupan en clases y cada clase es un estado del DFA mínimo, con transiciones tomadas de cualquiera de sus estados.
- **Por qué:** dos estados equivalentes aceptan exactamente las mismas cadenas de ahí en adelante, así que fusionarlos no cambia el lenguaje.

---

## 6. Simulación — [06-simulacion-dfa.md](06-simulacion-dfa.md)

### `DFA.accepts(input)` → `boolean`
- **Cómo:** empieza en el estado inicial y por cada carácter avanza con `transition`. Si en algún momento es `null`, rechaza. Al terminar, acepta si el estado actual es final.
- **Por qué:** es lo que usa `MainWindow.onTestString()` para que el alumno pruebe cadenas. Tarda tiempo lineal en la longitud de la cadena.

### `NFA.accepts(input)` → `boolean`
- **Cómo:** trabaja con un **conjunto** de estados actuales. Empieza en `epsilonClosure({inicial})` y por cada carácter calcula `epsilonClosure(move(actuales, c))`. Acepta si al final algún estado es final.
- **Por qué:** sirve para **probar** que NFA, DFA y DFA mínimo aceptan las mismas cadenas.

---

## 7. Modelo de autómatas — [07-modelo-automatas.md](07-modelo-automatas.md)

### `State.equals(o: Object)` → `boolean`
- **Qué hace:** dos `State` son iguales si tienen el mismo `id`.
- **Cómo:**
  1. `this == o`: si es **el mismo objeto en memoria**, retorna `true` sin revisar más (atajo).
  2. Si `o` es `null` o no es `State`, retorna `false`.
  3. Hace el cast `(State) o` y compara los `id`.
- **Por qué recibe `Object` y no `State`:** así está declarado en la clase `Object`; para **sobrescribirlo** hay que respetar esa firma.
- **`==` vs `equals`:** con objetos, `==` compara si son la misma referencia y `equals` compara el contenido.

  ```java
  State a = new State(1), b = a, c = new State(1);
  a == b;      // true  (mismo objeto)
  a == c;      // false (objetos distintos)
  a.equals(c); // true  (mismo id)
  ```

### `State.hashCode()` → `int`
- **Qué hace:** retorna `Integer.hashCode(id)`.
- **Por qué:** `HashSet` y `HashMap` primero buscan por `hashCode` y luego confirman con `equals`. **Regla:** si dos objetos son `equals`, deben tener el mismo `hashCode`. Si solo sobrescribes uno de los dos, los `Set<State>` guardan duplicados o no encuentran estados.

### `Automaton.addState(s)` → `void`
- Agrega `s` al conjunto `states`; si `s` es `null`, lanza una excepción. Como `states` es un `Set`, agregar un estado repetido no tiene efecto.

### `Automaton.markFinal(s)` → `void`
- Agrega `s` a `finals`. Antes verifica que `s` pertenezca al autómata, para no marcar como final un estado que no existe.

### `Automaton.isFinal(s)` → `boolean`
- Retorna `finals.contains(s)`. Depende de que `equals` y `hashCode` estén bien implementados.

### `NFA.newState()` → `State`
- **Qué hace:** crea un estado con `id` igual al contador, incrementa el contador, lo agrega con `addState` y lo retorna.
- **Por qué:** garantiza `id`s únicos. Los nodos de Thompson nunca hacen `new State(...)` directamente.

### `NFA.addTransition(src, c, dst)` → `void`
- **Qué hace:** guarda `src --c--> dst` en `delta: Map<State, Map<Character, Set<State>>>`.
- **Cómo:** crea el `Map` interno y el `Set` si todavía no existen, y agrega `dst`. Si `c` no es `EPSILON`, lo agrega al `alphabet`.
- **Por qué un `Set` de destinos:** en un NFA un mismo símbolo puede llevar a **varios** estados. ε no forma parte del alfabeto.

### `DFA.addTransition(src, c, dst)` → `void`
- **Qué hace:** guarda `src --c--> dst` en `delta: Map<State, Map<Character, State>>`, con **un solo** destino.
- **Por qué valida:** si ya existe `src --c-->` hacia otro estado, el autómata dejaría de ser determinista, así que lanza `IllegalStateException`.

### `DFA.transition(s, c)` → `State`
- Retorna el destino de `s` con `c`, o `null` si no existe. Es la función δ del DFA.

### `DFA.complete()` → `void`
- **Qué hace:** vuelve `transition` una función **total**: toda combinación (estado, símbolo) tiene un destino.
- **Cómo:** si falta alguna transición, crea un estado **sumidero** (no final) y dirige ahí todas las que faltan, incluidas las del propio sumidero, que apuntan a sí mismo.
- **Por qué:** la tabla de pares distinguibles necesita consultar `transition(p, c)` para todo par y símbolo. El sumidero representa "esta cadena ya no puede ser aceptada".
