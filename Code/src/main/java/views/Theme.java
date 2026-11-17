package views;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

// Tokens de diseño: todos los colores, espacios y fuentes de la interfaz salen de aquí.
// Regla: un solo color de acento (estado activo); verde y rojo solo para aceptada / rechazada.

public final class Theme {

    // Colores
    public static final Color BACKGROUND = new Color(0xF6F8FA);
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(0xD0D7DE);
    public static final Color INK = new Color(0x1F2328);
    public static final Color MUTED = new Color(0x656D76);
    public static final Color EDGE = new Color(0x57606A);
    public static final Color ACCENT = new Color(0x0969DA);
    public static final Color ACCENT_SOFT = new Color(0xDDF4FF);
    public static final Color ACCEPT = new Color(0x1A7F37);
    public static final Color REJECT = new Color(0xCF222E);

    // Escala de espaciado (múltiplos de 4)
    public static final int SPACE_XS = 4;
    public static final int SPACE_S = 8;
    public static final int SPACE_M = 16;
    public static final int SPACE_L = 24;

    public static final int CORNER_RADIUS = 10;

    // Tamaños de la fuente monoespaciada (expresiones, cadenas y estados)
    public static final float MONO_SIZE = 13f;
    public static final float MONO_LARGE = 16f;

    private Theme() {
    }

    public static void install() {
        if (!FlatLightLaf.setup()) {
            System.err.println("No se pudo cargar FlatLaf; se usará el Look and Feel por defecto.");
            return;
        }
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("Component.focusedBorderColor", ACCENT);
        UIManager.put("Button.default.background", ACCENT);
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("SplitPane.background", BACKGROUND);
        UIManager.put("SplitPaneDivider.style", "plain");
        UIManager.put("Table.selectionBackground", ACCENT_SOFT);
        UIManager.put("Table.selectionForeground", INK);
        UIManager.put("Table.selectionInactiveBackground", ACCENT_SOFT);
        UIManager.put("Table.selectionInactiveForeground", INK);
        UIManager.put("ScrollPane.border", null);
    }

    public static Font bodyFont() {
        return UIManager.getFont("Label.font");
    }

    public static Font headingFont() {
        return bodyFont().deriveFont(Font.BOLD);
    }

    public static Font smallFont() {
        Font body = bodyFont();
        return body.deriveFont(body.getSize2D() - 1f);
    }

    public static Font monoFont(float size) {
        return new Font(Font.MONOSPACED, Font.PLAIN, Math.round(size));
    }
}
