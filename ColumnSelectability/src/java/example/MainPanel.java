// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new GridLayout(2, 1));
    String[] columnNames = {"String", "Integer", "Boolean"};
    Object[][] data = {
        {"aaa", 12, true}, {"bbb", 5, false}, {"CCC", 92, true}, {"DDD", 0, false}
    };
    TableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
      }
    };
    JTable table = new JTable(model) {
      private boolean isColumnSelectable(int column) {
        return convertColumnIndexToModel(column) == 0;
      }

      @Override public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        if (!isColumnSelectable(columnIndex)) {
          return;
        }
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
      }

      @Override public boolean isCellEditable(int row, int column) {
        return isColumnSelectable(column);
      }

      @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        if (isColumnSelectable(column)) {
          return super.prepareRenderer(renderer, row, column);
        } else {
          Object o = getValueAt(row, column);
          return renderer.getTableCellRendererComponent(this, o, false, false, row, column);
        }
      }
    };
    table.setCellSelectionEnabled(true);
    // table.putClientProperty("Table.isFileList", Boolean.TRUE);
    // table.getTableHeader().setReorderingAllowed(false);
    add(new JScrollPane(new JTable(model)));
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
