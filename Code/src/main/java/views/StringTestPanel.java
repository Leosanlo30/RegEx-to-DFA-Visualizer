package views;

import controllers.SimulationTrace;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import models.Automaton.State;

// Campo para probar una cadena contra el AFD y mostrar si se acepta, con el recorrido de estados.

public class StringTestPanel extends JPanel {

    private final JTextField inputField = new JTextField();
    private final JButton testButton = new JButton("Probar");
    private final JLabel resultLabel = new JLabel(" ");
    private final JLabel detailLabel = new JLabel(" ");

    public StringTestPanel(Consumer<String> onTest) {
        super(new BorderLayout(0, Theme.SPACE_S));
        setOpaque(false);

        inputField.setFont(Theme.monoFont(Theme.MONO_SIZE));
        inputField.putClientProperty("JTextField.placeholderText", "Cadena a probar (vacía = ε)");
        ActionListener fire = e -> onTest.accept(inputField.getText());
        inputField.addActionListener(fire);
        testButton.addActionListener(fire);

        JPanel inputRow = new JPanel(new BorderLayout(Theme.SPACE_S, 0));
        inputRow.setOpaque(false);
        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(testButton, BorderLayout.EAST);

        resultLabel.setFont(Theme.headingFont());
        detailLabel.setFont(Theme.monoFont(Theme.MONO_SIZE));
        detailLabel.setForeground(Theme.MUTED);
        JPanel feedback = new JPanel();
        feedback.setLayout(new BoxLayout(feedback, BoxLayout.Y_AXIS));
        feedback.setOpaque(false);
        feedback.add(resultLabel);
        feedback.add(detailLabel);

        add(inputRow, BorderLayout.NORTH);
        add(feedback, BorderLayout.CENTER);
    }

    public void setInputEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
        testButton.setEnabled(enabled);
    }

    public void clear() {
        inputField.setText("");
        setFeedback(" ", Theme.INK, " ");
    }

    public void showRunning() {
        setFeedback("Simulando…", Theme.MUTED, " ");
    }

    public void showResult(SimulationTrace trace) {
        String path = formatPath(trace);
        if (trace.accepted()) {
            setFeedback("Aceptada", Theme.ACCEPT, path);
        } else if (trace.isStuck()) {
            char symbol = trace.input().charAt(trace.stuckAt());
            setFeedback("Rechazada: " + trace.lastState() + " no tiene transición con '" + symbol + "'", Theme.REJECT, path);
        } else {
            setFeedback("Rechazada: termina en " + trace.lastState() + ", que no es de aceptación", Theme.REJECT, path);
        }
    }

    private void setFeedback(String result, Color color, String detail) {
        resultLabel.setText(result);
        resultLabel.setForeground(color);
        detailLabel.setText(detail);
        detailLabel.setToolTipText(detail.isBlank() ? null : detail);
    }

    // q0 -a→ q1 -b→ q2 ; si se atasca termina en "-c→ ✕"
    private static String formatPath(SimulationTrace trace) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < trace.path().size(); i++) {
            State state = trace.path().get(i);
            if (i > 0) text.append(" -").append(trace.input().charAt(i - 1)).append("→ ");
            text.append(state);
        }
        if (trace.isStuck()) text.append(" -").append(trace.input().charAt(trace.stuckAt())).append("→ ✕");
        return text.toString();
    }
}
