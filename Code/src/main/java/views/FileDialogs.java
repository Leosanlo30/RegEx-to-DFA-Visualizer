package views;

import java.awt.Component;
import java.io.File;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

// Diálogos para abrir y guardar archivos. Recuerda la última carpeta usada.

final class FileDialogs {

    static final String TXT = "txt";
    static final String JSON = "json";
    static final FileNameExtensionFilter REGEX_FILES = new FileNameExtensionFilter("Expresión regular (*.txt)", TXT);
    static final FileNameExtensionFilter CONVERSION_FILES = new FileNameExtensionFilter("Conversión (*.json)", JSON);
    static final FileNameExtensionFilter SUPPORTED_FILES =
            new FileNameExtensionFilter("Expresión o conversión (*.txt, *.json)", TXT, JSON);

    private final JFileChooser chooser = new JFileChooser();

    Optional<File> open(Component parent, FileNameExtensionFilter filter) {
        prepare(filter);
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return Optional.empty();
        return Optional.of(chooser.getSelectedFile());
    }

    // Agrega la extensión si falta y pide confirmación antes de sobrescribir
    Optional<File> save(Component parent, FileNameExtensionFilter filter, String extension) {
        prepare(filter);
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return Optional.empty();
        File selected = chooser.getSelectedFile();
        File file = hasExtension(selected, extension) ? selected : new File(selected.getPath() + "." + extension);
        if (file.exists()) {
            int answer = JOptionPane.showConfirmDialog(parent,
                    "\"" + file.getName() + "\" ya existe. ¿Quieres reemplazarlo?",
                    "Reemplazar archivo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer != JOptionPane.YES_OPTION) return Optional.empty();
        }
        return Optional.of(file);
    }

    static boolean hasExtension(File file, String extension) {
        return file.getName().toLowerCase(Locale.ROOT).endsWith("." + extension);
    }

    private void prepare(FileNameExtensionFilter filter) {
        chooser.resetChoosableFileFilters();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(null);
    }
}
