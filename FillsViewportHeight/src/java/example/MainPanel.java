// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    String[] columnNames = {"String", "Integer", "Boolean"};
    Object[][] data = {
        {"aaa", 12, true}, {"bbb", 5, false}, {"CCC", 92, true}, {"DDD", 0, false}
    };
    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        // ArrayIndexOutOfBoundsException: 0 >= 0
        // [JDK-6967479] JTable sorter fires even if the model is empty - Java Bug System
        // https://bugs.openjdk.org/browse/JDK-6967479
        // return getValueAt(0, column).getClass();
        switch (column) {
          case 0: return String.class;
          case 1: return Number.class;
          case 2: return Boolean.class;
          default: return super.getColumnClass(column);
        }
      }
    };
    JTable table = new JTable(model) {
      private final Color evenColor = new Color(0xF5_F5_F5);

      @Override public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
        Component c = super.prepareRenderer(tcr, row, column);
        if (isRowSelected(row)) {
          c.setForeground(getSelectionForeground());
          c.setBackground(getSelectionBackground());
        } else {
          c.setForeground(getForeground());
          c.setBackground(row % 2 == 0 ? evenColor : getBackground());
        }
        return c;
      }
    };
    JScrollPane scroll = new JScrollPane(table);
    scroll.setBackground(Color.RED);
    scroll.getViewport().setBackground(Color.GREEN);
    // table.setBackground(Color.BLUE);
    // table.setOpaque(false);
    // table.setBackground(scroll.getBackground());
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setComponentPopupMenu(new TablePopupMenu());
    // scroll.getViewport().setComponentPopupMenu(makePop());
    // scroll.setComponentPopupMenu(makePop());
    table.setRowSorter(new TableRowSorter<>(model));

    add(makeToolBox(table), BorderLayout.NORTH);
    add(makeColorBox(table), BorderLayout.SOUTH);
    add(scroll);
    setPreferredSize(new Dimension(320, 240));
  }

  private static Component makeToolBox(JTable table) {
    JCheckBox check = new JCheckBox("FillsViewportHeight");
    check.addActionListener(e -> table.setFillsViewportHeight(check.isSelected()));

    JButton button = new JButton("clearSelection");
    button.addActionListener(e -> table.clearSelection());

    Box box = Box.createHorizontalBox();
    box.add(check);
    box.add(button);
    return box;
  }

  private static Component makeColorBox(JTable table) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    p.add(new JLabel("table.setBackground: "));

    JRadioButton r1 = new JRadioButton("WHITE", true);
    r1.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        table.setBackground(Color.WHITE);
      }
    });

    JRadioButton r2 = new JRadioButton("BLUE");
    r2.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        table.setBackground(Color.BLUE);
      }
    });

    ButtonGroup bg = new ButtonGroup();
    Stream.of(r1, r2).forEach(r -> {
      bg.add(r);
      p.add(r);
    });
    return p;
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

class TablePopupMenu extends JPopupMenu {
  private final JMenuItem delete;

  protected TablePopupMenu() {
    super();
    add("add").addActionListener(e -> {
      JTable table = (JTable) getInvoker();
      DefaultTableModel model = (DefaultTableModel) table.getModel();
      model.addRow(new Object[] {"example", model.getRowCount(), false});
      Rectangle r = table.getCellRect(model.getRowCount() - 1, 0, true);
      table.scrollRectToVisible(r);
    });
    addSeparator();
    delete = add("delete");
    delete.addActionListener(e -> {
      JTable table = (JTable) getInvoker();
      DefaultTableModel model = (DefaultTableModel) table.getModel();
      int[] selection = table.getSelectedRows();
      for (int i = selection.length - 1; i >= 0; i--) {
        model.removeRow(table.convertRowIndexToModel(selection[i]));
      }
    });
  }

  @Override public void show(Component c, int x, int y) {
    if (c instanceof JTable) {
      delete.setEnabled(((JTable) c).getSelectedRowCount() > 0);
      super.show(c, x, y);
    }
  }
}
