// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());

    String[] columnNames = {"Integer", "String", "Date"};
    @SuppressWarnings("JavaUtilDate")
    Object[][] data = {
        {-1, "AAA", new Date()}, {2, "BBB", new Date()},
        {-9, "EEE", new Date()}, {1, "", new Date()},
        {10, "CCC", new Date()}, {7, "FFF", new Date()},
    };
    TableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
      }
    };
    JTable table = new JTable(model) {
      @Override public void updateUI() {
        super.updateUI();
        // setShowGrid(false);
        // setAutoCreateRowSorter(true);
        setSurrendersFocusOnKeystroke(true);

        ((JLabel) getDefaultRenderer(Date.class)).setHorizontalAlignment(SwingConstants.LEFT);
        setDefaultEditor(Date.class, new SpinnerCellEditor());
      }
    };

    add(new JScrollPane(table));
    setPreferredSize(new Dimension(320, 240));
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
  private final JSpinner spinner = new JSpinner(new SpinnerDateModel());

  protected SpinnerCellEditor() {
    super();
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy/MM/dd");
    spinner.setEditor(editor);
    spinner.setBorder(BorderFactory.createEmptyBorder());
    setArrowButtonEnabled(false);

    editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
    editor.getTextField().addFocusListener(new FocusListener() {
      @Override public void focusLost(FocusEvent e) {
        setArrowButtonEnabled(false);
      }

      @Override public void focusGained(FocusEvent e) {
        // System.out.println("getTextField");
        setArrowButtonEnabled(true);
        EventQueue.invokeLater(() -> {
          JTextField field = (JTextField) e.getComponent();
          field.setCaretPosition(8);
          field.setSelectionStart(8);
          field.setSelectionEnd(10);
        });
      }
    });
  }

  protected final void setArrowButtonEnabled(boolean flag) {
    for (Component c : spinner.getComponents()) {
      if (c instanceof JButton) {
        c.setEnabled(flag);
      }
    }
  }

  @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    spinner.setValue(value);
    return spinner;
  }

  @Override public Object getCellEditorValue() {
    return spinner.getValue();
  }
}
