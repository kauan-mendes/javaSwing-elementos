// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    String[] columnNames = {"String", "Integer", "Boolean"};
    Object[][] data = {
        {"aaa", 12, true}, {"bbb", 5, false}, {"CCC", 92, true}, {"DDD", 0, false}
    };
    TableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
      }
    };
    JTable table0 = new JTable(model);
    table0.setAutoCreateRowSorter(true);
    table0.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    JTable table1 = makeTable(model);
    table1.setAutoCreateRowSorter(true);
    table1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    sp.setTopComponent(new JScrollPane(table0));
    sp.setBottomComponent(new JScrollPane(table1));
    sp.setResizeWeight(.5);
    add(sp);
    setPreferredSize(new Dimension(320, 240));
  }

  private static JTable makeTable(TableModel model) {
    return new JTable(model) {
      @Override public void updateUI() {
        // Changing to Nimbus LAF and back doesn't reset look and feel of JTable completely
        // https://bugs.openjdk.org/browse/JDK-6788475
        // XXX: set dummy ColorUIResource
        setSelectionForeground(new ColorUIResource(Color.RED));
        setSelectionBackground(new ColorUIResource(Color.RED));
        super.updateUI();
        updateRenderer();
        JCheckBox checkBox = makeBooleanEditor(this);
        setDefaultEditor(Boolean.class, new DefaultCellEditor(checkBox));
      }

      private void updateRenderer() {
        TableModel m = getModel();
        for (int i = 0; i < m.getColumnCount(); i++) {
          TableCellRenderer r = getDefaultRenderer(m.getColumnClass(i));
          if (r instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component) r);
          }
        }
      }

      @Override public Component prepareEditor(TableCellEditor editor, int row, int column) {
        Component c = super.prepareEditor(editor, row, column);
        if (c instanceof JCheckBox) {
          JCheckBox b = (JCheckBox) c;
          b.setBackground(getSelectionBackground());
          b.setBorderPainted(true);
        }
        return c;
      }
    };
  }

  public static JCheckBox makeBooleanEditor(JTable table) {
    JCheckBox checkBox = new JCheckBox();
    checkBox.setHorizontalAlignment(SwingConstants.CENTER);
    checkBox.setBorderPainted(true);
    checkBox.setOpaque(true);
    checkBox.addMouseListener(new MouseAdapter() {
      @Override public void mousePressed(MouseEvent e) {
        JCheckBox cb = (JCheckBox) e.getComponent();
        ButtonModel m = cb.getModel();
        int editingRow = table.getEditingRow();
        if (m.isPressed() && table.isRowSelected(editingRow) && e.isControlDown()) {
          if (editingRow % 2 == 0) {
            cb.setOpaque(false);
            // cb.setBackground(getBackground());
          } else {
            cb.setOpaque(true);
            cb.setBackground(UIManager.getColor("Table.alternateRowColor"));
          }
        } else {
          cb.setBackground(table.getSelectionBackground());
          cb.setOpaque(true);
        }
      }

      @Override public void mouseExited(MouseEvent e) {
        // in order to drag table row selection
        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
          table.getCellEditor().cancelCellEditing();
        }
      }
    });
    return checkBox;
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
