package views;

import controllers.SampleConverter;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // En macOS el menú va en la barra superior del sistema
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Regex DFA Visualizer");

        SwingUtilities.invokeLater(() -> {
            Theme.install();
            // TODO: reemplazar SampleConverter por el convertidor real cuando esté listo
            MainWindow window = new MainWindow(new SampleConverter());
            window.setVisible(true);
        });
    }
}
