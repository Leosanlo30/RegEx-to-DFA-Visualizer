package views.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import views.Theme;

// JTable con el estilo de la app; la fila seleccionada marca el estado activo de la simulación.

public class TransitionTable extends JTable {

    private static final int ROW_HEIGHT = 30;

    public TransitionTable(TransitionTableModel model) {
        super(model);
        setRowHeight(ROW_HEIGHT);
        setFillsViewportHeight(true);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(Theme.BORDER);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFocusable(false);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setFont(Theme.headingFont());
        setDefaultRenderer(Object.class, new CellRenderer());
    }

    public void highlightRow(int row) {
        if (row < 0) {
            clearSelection();
            return;
        }
        setRowSelectionInterval(row, row);
        scrollRectToVisible(getCellRect(row, 0, true));
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setFont(Theme.monoFont(Theme.MONO_SIZE));
            setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, Theme.SPACE_S, 0, Theme.SPACE_S));
            setForeground(TransitionTableModel.NO_TRANSITION.equals(value) ? Theme.MUTED : Theme.INK);
            return this;
        }
    }
}
