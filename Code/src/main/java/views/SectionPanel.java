package views;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// Tarjeta blanca con título: agrupa cada zona de la ventana (diagrama, tabla, prueba de cadenas).

public class SectionPanel extends JPanel {

    private final JLabel titleLabel = new JLabel();
    private final JPanel header = new JPanel(new BorderLayout(Theme.SPACE_S, 0));

    public SectionPanel(String title, JComponent content) {
        super(new BorderLayout(0, Theme.SPACE_S));
        setOpaque(false);
        setBorder(new EmptyBorder(Theme.SPACE_M, Theme.SPACE_M, Theme.SPACE_M, Theme.SPACE_M));

        titleLabel.setText(title);
        titleLabel.setFont(Theme.headingFont());
        titleLabel.setForeground(Theme.INK);
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    // Texto o control secundario alineado a la derecha del título
    public void setTrailing(JComponent component) {
        header.add(component, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D card = new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1.0, getHeight() - 1.0,
                    Theme.CORNER_RADIUS * 2, Theme.CORNER_RADIUS * 2);
            g2.setColor(Theme.SURFACE);
            g2.fill(card);
            g2.setColor(Theme.BORDER);
            g2.draw(card);
        } finally {
            g2.dispose();
        }
    }

    public static JLabel hint(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.smallFont());
        label.setForeground(Theme.MUTED);
        return label;
    }
}
