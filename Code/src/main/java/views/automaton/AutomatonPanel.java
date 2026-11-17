package views.automaton;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import models.Automaton.DFA;
import models.Automaton.State;
import views.Theme;

// Dibuja el diagrama de estados de un AFD con Java2D.
// Rueda del mouse: zoom; arrastrar: mover; doble clic: ajustar a la vista.

public class AutomatonPanel extends JPanel {

    static final double RADIUS = 24;
    private static final double FINAL_RING_GAP = 4;
    private static final double INITIAL_ARROW_LENGTH = 36;
    private static final double ARROW_SIZE = 9;
    private static final double ARROW_ANGLE = Math.toRadians(25);
    private static final double CURVE_BEND = 0.22;
    private static final double LOOP_HEIGHT = 2.9;
    private static final double LOOP_WIDTH = 1.4;
    private static final double LABEL_PADDING = 4;
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 3.0;
    private static final double MAX_FIT_ZOOM = 1.4;
    private static final double ZOOM_STEP = 1.1;
    private static final int FIT_MARGIN = 40;
    private static final int OVERLAY_MARGIN = 12;

    private static final Stroke NODE_STROKE = new BasicStroke(1.5f);
    private static final Stroke ACTIVE_STROKE = new BasicStroke(2.5f);
    private static final Stroke EDGE_STROKE = new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private record Edge(State from, State to, String label) {
    }

    private DFA dfa;
    private Map<State, Point2D> positions = Map.of();
    private List<Edge> edges = List.of();
    private State activeState;
    private double zoom = 1.0;
    private double offsetX;
    private double offsetY;
    private boolean needsFit;
    private Point dragOrigin;

    public AutomatonPanel() {
        setBackground(Theme.SURFACE);
        installInteractions();
    }

    public void setDfa(DFA dfa) {
        this.dfa = dfa;
        this.positions = dfa == null ? Map.of() : AutomatonLayout.compute(dfa);
        this.edges = dfa == null ? List.of() : groupEdges(dfa);
        this.activeState = null;
        fitToView();
    }

    public void setActiveState(State state) {
        this.activeState = state;
        repaint();
    }

    public void fitToView() {
        needsFit = true;
        repaint();
    }

    // Une las transiciones que comparten origen y destino en una sola flecha: "a, b"
    private static List<Edge> groupEdges(DFA dfa) {
        Map<State, Map<State, List<Character>>> grouped = new LinkedHashMap<>();
        dfa.getDelta().forEach((from, row) -> row.forEach((symbol, to) ->
                grouped.computeIfAbsent(from, k -> new LinkedHashMap<>())
                        .computeIfAbsent(to, k -> new ArrayList<>())
                        .add(symbol)));

        List<Edge> result = new ArrayList<>();
        grouped.forEach((from, targets) -> targets.forEach((to, symbols) -> result.add(new Edge(from, to,
                symbols.stream().sorted().map(String::valueOf).collect(Collectors.joining(", "))))));
        return List.copyOf(result);
    }

    // ---------- Pintado ----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            if (positions.isEmpty()) {
                paintEmptyState(g2);
                return;
            }
            if (needsFit) applyFit();

            Graphics2D world = (Graphics2D) g2.create();
            world.translate(offsetX, offsetY);
            world.scale(zoom, zoom);
            world.setFont(Theme.bodyFont());
            paintEdges(world);
            paintInitialArrow(world);
            paintStates(world);
            world.dispose();

            paintOverlay(g2);
        } finally {
            g2.dispose();
        }
    }

    private void paintEmptyState(Graphics2D g) {
        String title = "Aún no hay autómata";
        String hint = "Escribe una expresión regular y presiona Convertir.";
        int centerY = getHeight() / 2;
        g.setFont(Theme.headingFont());
        g.setColor(Theme.INK);
        drawCentered(g, title, getWidth() / 2.0, centerY - Theme.SPACE_S);
        g.setFont(Theme.bodyFont());
        g.setColor(Theme.MUTED);
        drawCentered(g, hint, getWidth() / 2.0, centerY + Theme.SPACE_M);
    }

    private void paintEdges(Graphics2D g) {
        g.setStroke(EDGE_STROKE);
        for (Edge edge : edges) {
            if (edge.from().equals(edge.to())) paintSelfLoop(g, positions.get(edge.from()), edge.label());
            else paintEdge(g, edge);
        }
    }

    private void paintEdge(Graphics2D g, Edge edge) {
        Point2D a = positions.get(edge.from());
        Point2D b = positions.get(edge.to());
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double length = Math.hypot(dx, dy);
        // Normal "hacia arriba" para aristas hacia la derecha: las de regreso quedan abajo
        double nx = dy / length;
        double ny = -dx / length;
        double bend = needsCurve(edge, a, b) ? CURVE_BEND * length : 0;
        Point2D control = new Point2D.Double((a.getX() + b.getX()) / 2 + nx * bend, (a.getY() + b.getY()) / 2 + ny * bend);
        Point2D start = pointOnCircle(a, control);
        Point2D end = pointOnCircle(b, control);

        g.setColor(Theme.EDGE);
        g.draw(new QuadCurve2D.Double(start.getX(), start.getY(), control.getX(), control.getY(), end.getX(), end.getY()));
        paintArrowHead(g, control, end);
        Point2D middle = quadPoint(start, control, end);
        paintEdgeLabel(g, edge.label(), middle.getX(), middle.getY());
    }

    // Curva la arista si hay una de regreso o si la línea recta atravesaría otro estado
    private boolean needsCurve(Edge edge, Point2D a, Point2D b) {
        boolean hasReverse = edges.stream().anyMatch(e -> e.from().equals(edge.to()) && e.to().equals(edge.from()));
        if (hasReverse) return true;
        Line2D line = new Line2D.Double(a, b);
        return positions.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(edge.from()) && !entry.getKey().equals(edge.to()))
                .anyMatch(entry -> line.ptSegDist(entry.getValue()) < RADIUS * 1.5);
    }

    private void paintSelfLoop(Graphics2D g, Point2D center, String label) {
        double cx = center.getX();
        double cy = center.getY();
        Point2D start = polar(center, Math.toRadians(-120));
        Point2D end = polar(center, Math.toRadians(-60));
        Point2D c1 = new Point2D.Double(cx - RADIUS * LOOP_WIDTH, cy - RADIUS * LOOP_HEIGHT);
        Point2D c2 = new Point2D.Double(cx + RADIUS * LOOP_WIDTH, cy - RADIUS * LOOP_HEIGHT);

        g.setColor(Theme.EDGE);
        g.draw(new CubicCurve2D.Double(start.getX(), start.getY(), c1.getX(), c1.getY(),
                c2.getX(), c2.getY(), end.getX(), end.getY()));
        paintArrowHead(g, c2, end);
        // Punto medio de la cúbica (t = 0.5)
        double topY = 0.125 * start.getY() + 0.375 * c1.getY() + 0.375 * c2.getY() + 0.125 * end.getY();
        paintEdgeLabel(g, label, cx, topY);
    }

    private void paintInitialArrow(Graphics2D g) {
        Point2D initial = positions.get(dfa.getInitial_state());
        if (initial == null) return;
        Point2D tip = new Point2D.Double(initial.getX() - RADIUS, initial.getY());
        Point2D tail = new Point2D.Double(tip.getX() - INITIAL_ARROW_LENGTH, tip.getY());
        g.setColor(Theme.INK);
        g.setStroke(EDGE_STROKE);
        g.draw(new Line2D.Double(tail, tip));
        paintArrowHead(g, tail, tip);
    }

    private void paintStates(Graphics2D g) {
        positions.forEach((state, center) -> paintState(g, state, center));
    }

    private void paintState(Graphics2D g, State state, Point2D center) {
        boolean active = state.equals(activeState);
        Ellipse2D circle = circle(center, RADIUS);
        g.setColor(active ? Theme.ACCENT_SOFT : Theme.SURFACE);
        g.fill(circle);
        g.setColor(active ? Theme.ACCENT : Theme.INK);
        g.setStroke(active ? ACTIVE_STROKE : NODE_STROKE);
        g.draw(circle);
        if (dfa.isFinal(state)) g.draw(circle(center, RADIUS - FINAL_RING_GAP));
        g.setColor(Theme.INK);
        drawCentered(g, state.toString(), center.getX(), center.getY());
    }

    private static void paintArrowHead(Graphics2D g, Point2D from, Point2D tip) {
        double angle = Math.atan2(tip.getY() - from.getY(), tip.getX() - from.getX());
        Path2D head = new Path2D.Double();
        head.moveTo(tip.getX(), tip.getY());
        head.lineTo(tip.getX() - ARROW_SIZE * Math.cos(angle - ARROW_ANGLE), tip.getY() - ARROW_SIZE * Math.sin(angle - ARROW_ANGLE));
        head.lineTo(tip.getX() - ARROW_SIZE * Math.cos(angle + ARROW_ANGLE), tip.getY() - ARROW_SIZE * Math.sin(angle + ARROW_ANGLE));
        head.closePath();
        g.fill(head);
    }

    // Etiqueta con fondo para que la línea no la tape
    private static void paintEdgeLabel(Graphics2D g, String text, double x, double y) {
        FontMetrics fm = g.getFontMetrics();
        double width = fm.stringWidth(text) + LABEL_PADDING * 2;
        double height = fm.getAscent() + fm.getDescent();
        g.setColor(Theme.SURFACE);
        g.fill(new RoundRectangle2D.Double(x - width / 2, y - height / 2, width, height, height, height));
        g.setColor(Theme.INK);
        drawCentered(g, text, x, y);
    }

    // Leyenda y nivel de zoom, en coordenadas de pantalla (no se escalan)
    private void paintOverlay(Graphics2D g) {
        g.setFont(Theme.smallFont());
        FontMetrics fm = g.getFontMetrics();
        int baseline = getHeight() - OVERLAY_MARGIN;
        int midY = baseline - fm.getAscent() / 2 + 1;

        g.setColor(Theme.MUTED);
        g.setStroke(new BasicStroke(1.2f));
        int x = OVERLAY_MARGIN;
        Point2D tail = new Point2D.Double(x, midY);
        Point2D tip = new Point2D.Double(x + 18, midY);
        g.draw(new Line2D.Double(tail, tip));
        paintArrowHead(g, tail, tip);
        x += 24;
        g.drawString("inicial", x, baseline);
        x += fm.stringWidth("inicial") + Theme.SPACE_M;

        Point2D ringCenter = new Point2D.Double(x + 7, midY);
        g.draw(circle(ringCenter, 7));
        g.draw(circle(ringCenter, 4.5));
        x += 20;
        g.drawString("aceptación", x, baseline);

        String zoomText = Math.round(zoom * 100) + "%";
        g.drawString(zoomText, getWidth() - OVERLAY_MARGIN - fm.stringWidth(zoomText), baseline);
    }

    // ---------- Geometría ----------

    private Rectangle2D worldBounds() {
        Rectangle2D bounds = null;
        for (Point2D p : positions.values()) {
            // Incluye el espacio del bucle sobre el estado
            Rectangle2D node = new Rectangle2D.Double(p.getX() - RADIUS * LOOP_WIDTH, p.getY() - RADIUS * LOOP_HEIGHT,
                    RADIUS * LOOP_WIDTH * 2, RADIUS * (LOOP_HEIGHT + 1));
            bounds = bounds == null ? node : bounds.createUnion(node);
        }
        Point2D initial = positions.get(dfa.getInitial_state());
        if (initial != null) bounds.add(initial.getX() - RADIUS - INITIAL_ARROW_LENGTH, initial.getY());
        return bounds;
    }

    private void applyFit() {
        double availableWidth = getWidth() - 2.0 * FIT_MARGIN;
        double availableHeight = getHeight() - 2.0 * FIT_MARGIN;
        if (availableWidth <= 0 || availableHeight <= 0) return;
        Rectangle2D bounds = worldBounds();
        double fit = Math.min(availableWidth / bounds.getWidth(), availableHeight / bounds.getHeight());
        zoom = clampZoom(Math.min(MAX_FIT_ZOOM, fit));
        offsetX = getWidth() / 2.0 - bounds.getCenterX() * zoom;
        offsetY = getHeight() / 2.0 - bounds.getCenterY() * zoom;
        needsFit = false;
    }

    private static Point2D pointOnCircle(Point2D center, Point2D toward) {
        double angle = Math.atan2(toward.getY() - center.getY(), toward.getX() - center.getX());
        return polar(center, angle);
    }

    private static Point2D polar(Point2D center, double angle) {
        return new Point2D.Double(center.getX() + RADIUS * Math.cos(angle), center.getY() + RADIUS * Math.sin(angle));
    }

    private static Point2D quadPoint(Point2D start, Point2D control, Point2D end) {
        return new Point2D.Double(0.25 * start.getX() + 0.5 * control.getX() + 0.25 * end.getX(),
                0.25 * start.getY() + 0.5 * control.getY() + 0.25 * end.getY());
    }

    private static Ellipse2D circle(Point2D center, double radius) {
        return new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2);
    }

    private static void drawCentered(Graphics2D g, String text, double x, double y) {
        FontMetrics fm = g.getFontMetrics();
        float textX = (float) (x - fm.stringWidth(text) / 2.0);
        float textY = (float) (y + (fm.getAscent() - fm.getDescent()) / 2.0);
        g.drawString(text, textX, textY);
    }

    private static double clampZoom(double value) {
        return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, value));
    }

    // ---------- Interacción ----------

    private void installInteractions() {
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (positions.isEmpty()) return;
                dragOrigin = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOrigin == null) return;
                offsetX += e.getX() - dragOrigin.x;
                offsetY += e.getY() - dragOrigin.y;
                dragOrigin = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragOrigin = null;
                setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) fitToView();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (positions.isEmpty()) return;
                zoomAt(e.getPoint(), Math.pow(ZOOM_STEP, -e.getPreciseWheelRotation()));
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fitToView();
            }
        });
    }

    // Mantiene fijo el punto bajo el cursor mientras cambia el zoom
    private void zoomAt(Point anchor, double factor) {
        double newZoom = clampZoom(zoom * factor);
        double ratio = newZoom / zoom;
        offsetX = anchor.x - (anchor.x - offsetX) * ratio;
        offsetY = anchor.y - (anchor.y - offsetY) * ratio;
        zoom = newZoom;
        repaint();
    }
}
