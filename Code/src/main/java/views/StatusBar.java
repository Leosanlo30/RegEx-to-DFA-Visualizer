package views;

import controllers.ConversionResult;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// Barra inferior: mensaje de la última acción (izquierda) y conteos de la conversión (derecha).

public class StatusBar extends JPanel {

    private final JLabel messageLabel = new JLabel("Listo.");
    private final JLabel pipelineLabel = new JLabel();

    public StatusBar() {
        super(new BorderLayout(Theme.SPACE_M, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                new EmptyBorder(Theme.SPACE_XS + 2, Theme.SPACE_M, Theme.SPACE_XS + 2, Theme.SPACE_M)));
        for (JLabel label : new JLabel[] {messageLabel, pipelineLabel}) {
            label.setFont(Theme.smallFont());
            label.setForeground(Theme.MUTED);
        }
        add(messageLabel, BorderLayout.WEST);
        add(pipelineLabel, BorderLayout.EAST);
    }

    public void setMessage(String message) {
        messageLabel.setForeground(Theme.MUTED);
        messageLabel.setText(message);
    }

    public void setError(String message) {
        messageLabel.setForeground(Theme.REJECT);
        messageLabel.setText(message);
    }

    public void showPipeline(ConversionResult result) {
        int minimal = result.minimalDfa().getStates().size();
        pipelineLabel.setText(result.hasPipeline()
                ? String.format("AFN de Thompson: %d estados  →  AFD: %d  →  AFD mínimo: %d",
                        result.nfa().getStates().size(), result.dfa().getStates().size(), minimal)
                : String.format("AFD mínimo: %d estados", minimal));
    }
}
