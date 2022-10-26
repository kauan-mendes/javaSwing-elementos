// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    String[] columnNames = {"JSpinner", "Buttons"};
    Object[][] data = {
        {50, 100}, {100, 50}, {30, 20}, {0, 100}
    };
    TableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return Integer.class;
      }
    };
    JTable table = new JTable(model) {
      @Override public void updateUI() {
        super.updateUI();
        EventQueue.invokeLater(() -> {
          TableColumn column = getColumnModel().getColumn(0);
          column.setCellRenderer(new SpinnerRenderer());
          column.setCellEditor(new SpinnerEditor());
          column = getColumnModel().getColumn(1);
          column.setCellRenderer(new ButtonsRenderer());
          column.setCellEditor(new ButtonsEditor());
          repaint();
        });
      }
    };
    table.setRowHeight(36);
    table.setAutoCreateRowSorter(true);
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

class SpinnerPanel extends JPanel {
  private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(100, 0, 200, 1));

  protected SpinnerPanel() {
    super(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.weightx = 1d;
    c.insets = new Insets(0, 10, 0, 10);
    c.fill = GridBagConstraints.HORIZONTAL;

    setOpaque(true);
    add(spinner, c);
  }

  protected JSpinner getSpinner() {
    return spinner;
  }
}

class SpinnerRenderer implements TableCellRenderer {
  private final SpinnerPanel renderer = new SpinnerPanel();

  @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    renderer.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
    renderer.getSpinner().setValue(value);
    return renderer;
  }
}

class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
  private final SpinnerPanel renderer = new SpinnerPanel();

  @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    renderer.setBackground(table.getSelectionBackground());
    renderer.getSpinner().setValue(value);
    return renderer;
  }

  @Override public Object getCellEditorValue() {
    return renderer.getSpinner().getValue();
  }

  // @Override public boolean isCellEditable(EventObject e) {
  //   return true;
  // }

  // @Override public boolean shouldSelectCell(EventObject anEvent) {
  //   return true;
  // }

  @Override public boolean stopCellEditing() {
    JSpinner spinner = renderer.getSpinner();
    try {
      spinner.commitEdit();
    } catch (ParseException ex) {
      UIManager.getLookAndFeel().provideErrorFeedback(spinner);
      return false;
    }
    return super.stopCellEditing();
    // fireEditingStopped();
    // return true;
  }
}

// class SpinnerRenderer extends SpinnerPanel implements TableCellRenderer {
//   @Override public void updateUI() {
//     super.updateUI();
//     setName("Table.cellRenderer");
//   }
//
//   @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//     setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
//     spinner.setValue((Integer) value);
//     return this;
//   }
// }
//
// class SpinnerEditor extends SpinnerPanel implements TableCellEditor {
//   private transient ChangeEvent changeEvent;
//   @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//     this.setBackground(table.getSelectionBackground());
//     spinner.setValue((Integer) value);
//     return this;
//   }
//
//   @Override public Object getCellEditorValue() {
//     return spinner.getValue();
//   }
//
//   // Copied from AbstractCellEditor
//   // protected EventListenerList listenerList = new EventListenerList();
//   @Override public boolean isCellEditable(EventObject e) {
//     return true;
//   }
//
//   @Override public boolean shouldSelectCell(EventObject anEvent) {
//     return true;
//   }
//
//   @Override public boolean stopCellEditing() {
//     try {
//       spinner.commitEdit();
//     } catch (ParseException ex) {
//       UIManager.getLookAndFeel().provideErrorFeedback(spinner);
//       return false;
//     }
//     fireEditingStopped();
//     return true;
//   }
//
//   @Override public void cancelCellEditing() {
//     fireEditingCanceled();
//   }
//
//   @Override public void addCellEditorListener(CellEditorListener l) {
//     listenerList.add(CellEditorListener.class, l);
//   }
//
//   @Override public void removeCellEditorListener(CellEditorListener l) {
//     listenerList.remove(CellEditorListener.class, l);
//   }
//
//   public CellEditorListener[] getCellEditorListeners() {
//     return listenerList.getListeners(CellEditorListener.class);
//   }
//
//   protected void fireEditingStopped() {
//     // Guaranteed to return a non-null array
//     Object[] listeners = listenerList.getListenerList();
//     // Process the listeners last to first, notifying
//     // those that are interested in this event
//     for (int i = listeners.length - 2; i >= 0; i -= 2) {
//       if (listeners[i] == CellEditorListener.class) {
//         // Lazily create the event:
//         if (Objects.isNull(changeEvent)) {
//           changeEvent = new ChangeEvent(this);
//         }
//         ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
//       }
//     }
//   }
//
//   protected void fireEditingCanceled() {
//     // Guaranteed to return a non-null array
//     Object[] listeners = listenerList.getListenerList();
//     // Process the listeners last to first, notifying
//     // those that are interested in this event
//     for (int i = listeners.length - 2; i >= 0; i -= 2) {
//       if (listeners[i] == CellEditorListener.class) {
//         // Lazily create the event:
//         if (Objects.isNull(changeEvent)) {
//           changeEvent = new ChangeEvent(this);
//         }
//         ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
//       }
//     }
//   }
// }

class ButtonsPanel extends JPanel {
  public final JButton[] buttons = {new JButton("+"), new JButton("-")};
  public final JLabel label = new JLabel(" ", SwingConstants.RIGHT) {
    @Override public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.width = 50;
      return d;
    }
  };
  protected int counter = -1;

  protected ButtonsPanel() {
    super();
    setOpaque(true);
    add(label);
    for (JButton b : buttons) {
      b.setFocusable(false);
      b.setRolloverEnabled(false);
      add(b);
    }
  }
}

class ButtonsRenderer implements TableCellRenderer {
  private final ButtonsPanel renderer = new ButtonsPanel();

  @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Color bgc = isSelected ? table.getSelectionBackground() : table.getBackground();
    renderer.setBackground(bgc);
    Color fgc = isSelected ? table.getSelectionForeground() : table.getForeground();
    renderer.label.setForeground(fgc);
    renderer.label.setText(Objects.toString(value, ""));
    return renderer;
  }
}

class ButtonsEditor extends AbstractCellEditor implements TableCellEditor {
  private final ButtonsPanel renderer = new ButtonsPanel();

  protected ButtonsEditor() {
    super();
    renderer.buttons[0].addActionListener(e -> {
      renderer.counter++;
      renderer.label.setText(Integer.toString(renderer.counter));
      fireEditingStopped();
    });

    renderer.buttons[1].addActionListener(e -> {
      renderer.counter--;
      renderer.label.setText(Integer.toString(renderer.counter));
      fireEditingStopped();
    });

    renderer.addMouseListener(new MouseAdapter() {
      @Override public void mousePressed(MouseEvent e) {
        fireEditingStopped();
      }
    });
  }

  @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    renderer.setBackground(table.getSelectionBackground());
    renderer.label.setForeground(table.getSelectionForeground());
    renderer.counter = (Integer) value;
    renderer.label.setText(Integer.toString(renderer.counter));
    return renderer;
  }

  @Override public Object getCellEditorValue() {
    return renderer.counter;
  }

  // // AbstractCellEditor
  // @Override public boolean isCellEditable(EventObject e) {
  //   return true;
  // }
  //
  // @Override public boolean shouldSelectCell(EventObject anEvent) {
  //   return true;
  // }
  //
  // @Override public boolean stopCellEditing() {
  //   fireEditingStopped();
  //   return true;
  // }
  //
  // @Override public void cancelCellEditing() {
  //   fireEditingCanceled();
  // }
}

// class ButtonsRenderer extends ButtonsPanel implements TableCellRenderer {
//   @Override public void updateUI() {
//     super.updateUI();
//     setName("Table.cellRenderer");
//   }
//
//   @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//     this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
//     label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
//     label.setText(Objects.toString(value, ""));
//     return this;
//   }
// }
//
// class ButtonsEditor extends ButtonsPanel implements TableCellEditor {
//   private transient ChangeEvent changeEvent;
//   protected ButtonsEditor() {
//     super();
//     buttons.get(0).addActionListener(new ActionListener() {
//       @Override public void actionPerformed(ActionEvent e) {
//         i++;
//         label.setText(Integer.toString(i));
//         fireEditingStopped();
//       }
//     });
//
//     buttons.get(1).addActionListener(new ActionListener() {
//       @Override public void actionPerformed(ActionEvent e) {
//         i--;
//         label.setText(Integer.toString(i));
//         fireEditingStopped();
//       }
//     });
//
//     addMouseListener(new MouseAdapter() {
//       @Override public void mousePressed(MouseEvent e) {
//         fireEditingStopped();
//       }
//     });
//   }
//
//   @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//     this.setBackground(table.getSelectionBackground());
//     label.setForeground(table.getSelectionForeground());
//     i = (Integer) value;
//     label.setText(Integer.toString(i));
//     return this;
//   }
//
//   @Override public Object getCellEditorValue() {
//     return i;
//   }
//
//   // Copied from AbstractCellEditor
//   // protected EventListenerList listenerList = new EventListenerList();
//   @Override public boolean isCellEditable(EventObject e) {
//     return true;
//   }
//
//   @Override public boolean shouldSelectCell(EventObject anEvent) {
//     return true;
//   }
//
//   @Override public boolean stopCellEditing() {
//     fireEditingStopped();
//     return true;
//   }
//
//   @Override public void cancelCellEditing() {
//     fireEditingCanceled();
//   }
//
//   @Override public void addCellEditorListener(CellEditorListener l) {
//     listenerList.add(CellEditorListener.class, l);
//   }
//
//   @Override public void removeCellEditorListener(CellEditorListener l) {
//     listenerList.remove(CellEditorListener.class, l);
//   }
//
//   public CellEditorListener[] getCellEditorListeners() {
//     return listenerList.getListeners(CellEditorListener.class);
//   }
//
//   protected void fireEditingStopped() {
//     // Guaranteed to return a non-null array
//     Object[] listeners = listenerList.getListenerList();
//     // Process the listeners last to first, notifying
//     // those that are interested in this event
//     for (int i = listeners.length - 2; i >= 0; i -= 2) {
//       if (listeners[i] == CellEditorListener.class) {
//         // Lazily create the event:
//         if (Objects.isNull(changeEvent)) {
//           changeEvent = new ChangeEvent(this);
//         }
//         ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
//       }
//     }
//   }
//
//   protected void fireEditingCanceled() {
//     // Guaranteed to return a non-null array
//     Object[] listeners = listenerList.getListenerList();
//     // Process the listeners last to first, notifying
//     // those that are interested in this event
//     for (int i = listeners.length - 2; i >= 0; i -= 2) {
//       if (listeners[i] == CellEditorListener.class) {
//         // Lazily create the event:
//         if (Objects.isNull(changeEvent)) {
//           changeEvent = new ChangeEvent(this);
//         }
//         ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
//       }
//     }
//   }
// }
