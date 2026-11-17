package views;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

// Barra de menú: Archivo, Sintaxis y Ayuda. Las acciones vienen de MainWindow para compartirlas con los botones.

public class MainMenuBar extends JMenuBar {

    public record Actions(Action openRegex, Action openConversion, Action save, Action exit, Action about) {
    }

    public MainMenuBar(Actions actions, SyntaxMenu syntaxMenu) {
        JMenu file = new JMenu("Archivo");
        file.add(actions.openRegex());
        file.add(actions.openConversion());
        file.addSeparator();
        file.add(actions.save());
        file.addSeparator();
        file.add(actions.exit());

        JMenu help = new JMenu("Ayuda");
        help.add(actions.about());

        add(file);
        add(syntaxMenu);
        add(help);
    }
}
