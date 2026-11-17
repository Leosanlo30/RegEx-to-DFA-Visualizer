package views;

import java.util.List;
import java.util.function.Consumer;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

// Menú "Sintaxis": inserta operadores en el campo de la ER y carga expresiones de ejemplo.

public class SyntaxMenu extends JMenu {

    private record Example(String regex, String note) {
    }

    private static final List<Example> EXAMPLES = List.of(
            new Example("(a|b)*abb", ""),
            new Example("a*b", ""),
            new Example("(ab)*", ""),
            new Example("(0|1)*1", ""),
            new Example("(a|b*abb", "  — con error"));

    private final JTextField target;

    public SyntaxMenu(JTextField target, Consumer<String> onExample) {
        super("Sintaxis");
        this.target = target;

        JMenu operators = new JMenu("Operadores");
        operators.add(item("|   Unión", () -> insert("|")));
        operators.add(item("*   Estrella de Kleene", () -> insert("*")));
        operators.add(item("( ) Agrupación", this::wrapInGroup));
        operators.add(item("ε   Cadena vacía", () -> insert("ε")));

        JMenu examples = new JMenu("Ejemplos");
        for (Example example : EXAMPLES) {
            examples.add(item(example.regex() + example.note(), () -> onExample.accept(example.regex())));
        }

        add(operators);
        add(examples);
    }

    // Inserta en la posición del cursor (o reemplaza la selección)
    public void insert(String text) {
        target.replaceSelection(text);
        target.requestFocusInWindow();
    }

    // Envuelve la selección en paréntesis; sin selección deja el cursor dentro de "()"
    private void wrapInGroup() {
        String selected = target.getSelectedText();
        target.replaceSelection("(" + (selected == null ? "" : selected) + ")");
        if (selected == null) target.setCaretPosition(target.getCaretPosition() - 1);
        target.requestFocusInWindow();
    }

    private static JMenuItem item(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }
}
