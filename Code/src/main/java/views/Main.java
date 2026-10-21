package views;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // TODO: reemplazar por VentanaPrincipal
            JFrame ventana = new JFrame("Regex DFA Visualizer");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(900, 600);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
