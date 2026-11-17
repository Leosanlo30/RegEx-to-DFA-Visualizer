package views;

import controllers.ConversionResult;
import controllers.Converter;
import controllers.Exceptions.AutomatonException;
import controllers.Exceptions.FileFormatException;
import controllers.Exceptions.RegexSyntaxException;
import controllers.SimulationTrace;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import models.Automaton.DFA;
import models.Automaton.State;
import models.persistencia.AutomatonJsonManager;
import models.persistencia.RegexFileReader;
import views.automaton.AutomatonPanel;
import views.table.TransitionTable;
import views.table.TransitionTableModel;

// Ventana principal. Arma el layout y conecta las acciones del usuario con el convertidor y la persistencia.

public class MainWindow extends JFrame {

    private static final String APP_NAME = "Regex DFA Visualizer";
    private static final String DIAGRAM_TITLE = "AFD mínimo";
    private static final Dimension DEFAULT_SIZE = new Dimension(1100, 680);
    private static final Dimension MINIMUM_SIZE = new Dimension(820, 520);
    private static final double DIAGRAM_WEIGHT = 0.6;
    private static final int REGEX_COLUMNS = 28;

    private final Converter converter;
    private final JTextField regexField = new JTextField(REGEX_COLUMNS);
    private final AutomatonPanel automatonPanel = new AutomatonPanel();
    private final SectionPanel diagramSection = new SectionPanel(DIAGRAM_TITLE, automatonPanel);
    private final TransitionTableModel tableModel = new TransitionTableModel();
    private final TransitionTable table = new TransitionTable(tableModel);
    private final StringTestPanel testPanel = new StringTestPanel(this::onTestString);
    private final StatusBar statusBar = new StatusBar();
    private final SimulationPlayer player = new SimulationPlayer(this::showStep, this::onSimulationFinished);
    private final FileDialogs fileDialogs = new FileDialogs();

    private final Action openAnyAction = action("Abrir…", null, this::onOpenAny);
    private final Action openRegexAction = action("Abrir expresión (.txt)…", shortcut(KeyEvent.VK_O, 0), this::onOpenRegex);
    private final Action openConversionAction = action("Abrir conversión (.json)…",
            shortcut(KeyEvent.VK_O, KeyEvent.SHIFT_DOWN_MASK), this::onOpenConversion);
    private final Action saveAction = action("Guardar conversión (.json)…", shortcut(KeyEvent.VK_S, 0), this::onSave);

    private ConversionResult current;

    public MainWindow(Converter converter) {
        super(APP_NAME);
        this.converter = Objects.requireNonNull(converter, "converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());

        JPanel workspace = new JPanel(new BorderLayout(0, Theme.SPACE_M));
        workspace.setBorder(new EmptyBorder(Theme.SPACE_M, Theme.SPACE_M, Theme.SPACE_M, Theme.SPACE_M));
        workspace.add(buildToolbar(), BorderLayout.NORTH);
        workspace.add(buildPanels(), BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout());
        content.add(workspace, BorderLayout.CENTER);
        content.add(statusBar, BorderLayout.SOUTH);
        setContentPane(content);

        setSize(DEFAULT_SIZE);
        setMinimumSize(MINIMUM_SIZE);
        setLocationRelativeTo(null);
        setResultAvailable(false);
    }

    // ---------- Construcción del layout ----------

    private MainMenuBar buildMenuBar() {
        Action exit = action("Salir", shortcut(KeyEvent.VK_Q, 0),
                () -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        Action about = action("Acerca de…", null, this::onAbout);
        SyntaxMenu syntaxMenu = new SyntaxMenu(regexField, this::onExample);
        return new MainMenuBar(new MainMenuBar.Actions(openRegexAction, openConversionAction, saveAction, exit, about), syntaxMenu);
    }

    private JComponent buildToolbar() {
        JLabel label = new JLabel("Expresión regular");
        label.setFont(Theme.headingFont());
        label.setLabelFor(regexField);

        regexField.setFont(Theme.monoFont(Theme.MONO_LARGE));
        regexField.putClientProperty("JTextField.placeholderText", "Ej. (a|b)*abb");

        JButton convertButton = new JButton("Convertir");
        convertButton.addActionListener(e -> onConvert());
        getRootPane().setDefaultButton(convertButton);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACE_S, 0));
        buttons.setOpaque(false);
        buttons.add(convertButton);
        buttons.add(button(openAnyAction, "Abrir…"));
        buttons.add(button(saveAction, "Guardar"));

        JPanel toolbar = new JPanel(new BorderLayout(Theme.SPACE_M, 0));
        toolbar.setOpaque(false);
        toolbar.add(label, BorderLayout.WEST);
        toolbar.add(regexField, BorderLayout.CENTER);
        toolbar.add(buttons, BorderLayout.EAST);
        return toolbar;
    }

    private JComponent buildPanels() {
        diagramSection.setTrailing(SectionPanel.hint("Rueda: zoom · Arrastrar: mover · Doble clic: ajustar"));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(Theme.BORDER));
        JPanel tableContent = new JPanel(new BorderLayout(0, Theme.SPACE_S));
        tableContent.setOpaque(false);
        tableContent.add(tableScroll, BorderLayout.CENTER);
        tableContent.add(SectionPanel.hint("→ inicial   * aceptación   — sin transición (rechaza)"), BorderLayout.SOUTH);

        JPanel sidePanel = new JPanel(new BorderLayout(0, Theme.SPACE_M));
        sidePanel.setOpaque(false);
        sidePanel.add(new SectionPanel("Tabla de transiciones", tableContent), BorderLayout.CENTER);
        sidePanel.add(new SectionPanel("Probar cadena", testPanel), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, diagramSection, sidePanel);
        split.setResizeWeight(DIAGRAM_WEIGHT);
        split.setDividerSize(Theme.SPACE_M);
        split.setContinuousLayout(true);
        split.setBorder(null);
        split.setOpaque(false);
        // La proporción solo se puede fijar cuando la ventana ya tiene tamaño
        SwingUtilities.invokeLater(() -> split.setDividerLocation(DIAGRAM_WEIGHT));
        return split;
    }

    // ---------- Conversión ----------

    private void onExample(String regex) {
        regexField.setText(regex);
        onConvert();
    }

    private void onConvert() {
        String regex = regexField.getText().strip();
        regexField.setText(regex);
        try {
            showResult(converter.convert(regex), "Conversión lista.");
        } catch (RegexSyntaxException ex) {
            showSyntaxError(regex, ex);
        } catch (UnsupportedOperationException ex) {
            statusBar.setMessage(ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Conversión no disponible", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showResult(ConversionResult result, String message) {
        player.stop();
        current = result;
        automatonPanel.setDfa(result.minimalDfa());
        tableModel.setDfa(result.minimalDfa());
        diagramSection.setTitle(result.regex().isEmpty() ? DIAGRAM_TITLE : DIAGRAM_TITLE + "  ·  " + result.regex());
        testPanel.clear();
        statusBar.setMessage(message);
        statusBar.showPipeline(result);
        setResultAvailable(true);
    }

    private void showSyntaxError(String regex, RegexSyntaxException ex) {
        int position = Math.max(0, Math.min(ex.getPosition(), regex.length()));
        statusBar.setError("Error de sintaxis en la posición " + (position + 1) + ": " + ex.getErrortype() + ".");
        JOptionPane.showMessageDialog(this, syntaxErrorMessage(regex, position, ex), "Error de sintaxis",
                JOptionPane.WARNING_MESSAGE);
        regexField.requestFocusInWindow();
        regexField.select(position, Math.min(position + 1, regex.length()));
    }

    // Muestra la expresión con un "^" bajo el carácter con error
    private static JComponent syntaxErrorMessage(String regex, int position, RegexSyntaxException ex) {
        JLabel message = new JLabel(ex.getErrortype() + " (posición " + (position + 1) + "): " + ex.getMessage());
        JTextArea pointer = new JTextArea(regex + "\n" + " ".repeat(position) + "^");
        pointer.setEditable(false);
        pointer.setFocusable(false);
        pointer.setOpaque(false);
        pointer.setFont(Theme.monoFont(Theme.MONO_LARGE));
        pointer.setForeground(Theme.INK);

        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_S));
        panel.setOpaque(false);
        panel.add(message, BorderLayout.NORTH);
        panel.add(pointer, BorderLayout.CENTER);
        return panel;
    }

    // ---------- Simulación ----------

    private void onTestString(String input) {
        if (current == null) return;
        try {
            SimulationTrace trace = SimulationTrace.run(current.minimalDfa(), input);
            testPanel.showRunning();
            player.play(trace);
        } catch (AutomatonException ex) {
            statusBar.setError(ex.getMessage());
        }
    }

    private void showStep(State state) {
        automatonPanel.setActiveState(state);
        table.highlightRow(tableModel.rowOf(state));
    }

    private void onSimulationFinished(SimulationTrace trace) {
        testPanel.showResult(trace);
        String input = trace.input().isEmpty() ? "ε" : "\"" + trace.input() + "\"";
        statusBar.setMessage("Cadena " + input + (trace.accepted() ? " aceptada." : " rechazada."));
    }

    // ---------- Archivos ----------

    private void onOpenAny() {
        fileDialogs.open(this, FileDialogs.SUPPORTED_FILES).ifPresent(file -> {
            if (FileDialogs.hasExtension(file, FileDialogs.JSON)) loadConversion(file);
            else loadRegex(file);
        });
    }

    private void onOpenRegex() {
        fileDialogs.open(this, FileDialogs.REGEX_FILES).ifPresent(this::loadRegex);
    }

    private void onOpenConversion() {
        fileDialogs.open(this, FileDialogs.CONVERSION_FILES).ifPresent(this::loadConversion);
    }

    private void loadRegex(File file) {
        try {
            regexField.setText(RegexFileReader.readRegex(file.getPath()));
            onConvert();
        } catch (FileFormatException ex) {
            showFileError("No se pudo abrir la expresión", ex);
        }
    }

    private void loadConversion(File file) {
        try {
            DFA dfa = AutomatonJsonManager.loadDFA(file.getPath());
            requireComplete(dfa);
            regexField.setText("");
            showResult(ConversionResult.ofMinimal("", dfa), "Conversión cargada desde " + file.getName() + ".");
        } catch (FileFormatException ex) {
            showFileError("No se pudo abrir la conversión", ex);
        }
    }

    private void onSave() {
        if (current == null) return;
        fileDialogs.save(this, FileDialogs.CONVERSION_FILES, FileDialogs.JSON).ifPresent(file -> {
            try {
                AutomatonJsonManager.saveDFA(current.minimalDfa(), file.getPath());
                statusBar.setMessage("Conversión guardada en " + file.getName() + ".");
            } catch (FileFormatException ex) {
                showFileError("No se pudo guardar la conversión", ex);
            }
        });
    }

    // Gson puede crear un DFA con campos nulos si el .json no tiene la forma esperada
    private static void requireComplete(DFA dfa) {
        boolean complete = dfa.getInitial_state() != null && dfa.getStates() != null && dfa.getAlphabet() != null
                && dfa.getFinal_states() != null && dfa.getDelta() != null;
        if (!complete) throw new FileFormatException("El archivo .json no corresponde a un autómata válido");
    }

    private void showFileError(String title, FileFormatException ex) {
        statusBar.setError(ex.getMessage());
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    // ---------- Utilidades ----------

    private void onAbout() {
        JOptionPane.showMessageDialog(this,
                APP_NAME + "\nConvierte una expresión regular en su AFD mínimo,\n"
                        + "muestra el diagrama y la tabla de transiciones\ny permite probar cadenas.",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setResultAvailable(boolean available) {
        saveAction.setEnabled(available);
        testPanel.setInputEnabled(available);
    }

    private static Action action(String name, KeyStroke accelerator, Runnable handler) {
        Action action = new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.run();
            }
        };
        if (accelerator != null) action.putValue(Action.ACCELERATOR_KEY, accelerator);
        return action;
    }

    // Cmd en macOS, Ctrl en Windows/Linux
    private static KeyStroke shortcut(int keyCode, int extraModifiers) {
        return KeyStroke.getKeyStroke(keyCode, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | extraModifiers);
    }

    private static JButton button(Action action, String text) {
        JButton button = new JButton(action);
        button.setText(text);
        return button;
    }
}
