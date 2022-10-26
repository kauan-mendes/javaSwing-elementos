// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

// @version 1.0 02/25/99
// @author Nobuo Tamemasa
// modified by aterai aterai@outlook.com
public class SortableTableModel extends DefaultTableModel {
  @SuppressWarnings({"unchecked", "JdkObsolete"})
  public final void sortByColumn(int column, boolean isAscent) {
    getDataVector().sort(new ColumnComparator(column, isAscent));
    fireTableDataChanged();
  }
}

class ColumnComparator implements Comparator<Object>, Serializable {
  private static final long serialVersionUID = 1L;
  protected final int index;
  protected final boolean ascending;

  protected ColumnComparator(int index, boolean ascending) {
    this.index = index;
    this.ascending = ascending;
  }

  @SuppressWarnings("unchecked")
  @Override public int compare(Object one, Object two) {
    if (one instanceof List && two instanceof List) {
      Comparable<Object> o1 = (Comparable<Object>) ((List<Object>) one).get(index);
      Comparable<Object> o2 = (Comparable<Object>) ((List<Object>) two).get(index);
      int c = Objects.compare(o1, o2, Comparator.nullsFirst(Comparator.naturalOrder()));
      return c * (ascending ? 1 : -1);
    }
    return 0;
  }

  // @Override public int compare(Object one, Object two) {
  //   if (one instanceof Vector && two instanceof Vector) {
  //     Object oOne = ((Vector) one).get(index);
  //     Object oTwo = ((Vector) two).get(index);
  //     int dir = ascending ? 1 : -1;
  //     if (oOne instanceof Comparable && oTwo instanceof Comparable) {
  //       Comparable cOne = (Comparable) oOne;
  //       Comparable cTwo = (Comparable) oTwo;
  //       return cOne.compareTo(cTwo) * dir;
  //     } else if (oOne == null && oTwo == null) {
  //       return 0;
  //     } else if (oOne == null) {
  //       return -1 * dir;
  //     } else { // if (oTwo == null) {
  //       return 1 * dir;
  //     }
  //   }
  //   return 1;
  // }
  // @Override public int compare(Number o1, Number o2) {
  //   return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString()));
  //   // double n1 = o1.doubleValue();
  //   // double n2 = o2.doubleValue();
  //   // if (n1 < n2) {
  //   //   return -1;
  //   // } else if (n1 > n2) {
  //   //   return 1;
  //   // } else {
  //   //   return 0;
  //   // }
  // }
}

class SortButtonRenderer extends JButton implements TableCellRenderer {
  public static final int NONE = 0;
  public static final int DOWN = 1;
  public static final int UP = 2;
  // private transient Icon ascendingSortIcon = UIManager.getIcon("Table.ascendingSortIcon");
  // private transient Icon descendingSortIcon = UIManager.getIcon("Table.descendingSortIcon");
  // private transient Icon noneSortIcon = new EmptyIcon(ascendingSortIcon);
  private Dimension iconSize;
  private int pushedColumn = -1;
  private final Map<Integer, Integer> state = new ConcurrentHashMap<>();

  @Override public void updateUI() {
    super.updateUI();
    // ascendingSortIcon = UIManager.getIcon("Table.ascendingSortIcon");
    // descendingSortIcon = UIManager.getIcon("Table.descendingSortIcon");
    // noneSortIcon = new EmptyIcon(ascendingSortIcon);
    Icon i = UIManager.getIcon("Table.ascendingSortIcon");
    iconSize = new Dimension(i.getIconWidth(), i.getIconHeight());
    setIcon(new EmptyIcon(iconSize));
    setHorizontalTextPosition(SwingConstants.LEFT);
  }

  @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    setText(Objects.toString(value, ""));
    setIcon(new EmptyIcon(iconSize));
    int modelColumn = table.convertColumnIndexToModel(column);
    Integer iv = state.get(modelColumn);
    if (iv == DOWN) {
      setIcon(UIManager.getIcon("Table.ascendingSortIcon"));
      // setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
      // setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));
    } else if (iv == UP) {
      setIcon(UIManager.getIcon("Table.descendingSortIcon"));
      // setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
      // setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
    }
    boolean isPressed = modelColumn == pushedColumn;
    getModel().setPressed(isPressed);
    getModel().setArmed(isPressed);
    return this;
  }

  public void setPressedColumn(int col) {
    pushedColumn = col;
  }

  public void setSelectedColumn(int col) {
    if (col < 0) {
      state.clear();
      return;
    }
    Integer obj = state.get(col);
    Integer value = obj != null && obj == DOWN ? UP : DOWN;
    state.clear();
    state.put(col, value);
  }

  public int getState(int col) {
    Integer i = state.get(col);
    return i == null ? NONE : i;
  }
}

class HeaderMouseListener extends MouseAdapter {
  @Override public void mousePressed(MouseEvent e) {
    JTableHeader h = (JTableHeader) e.getComponent();
    TableColumnModel columnModel = h.getColumnModel();
    int viewColumn = columnModel.getColumnIndexAtX(e.getX());
    if (viewColumn < 0) {
      return;
    }
    TableCellRenderer tcr = h.getDefaultRenderer();
    int column = columnModel.getColumn(viewColumn).getModelIndex();
    if (column != -1 && tcr instanceof SortButtonRenderer) {
      SortButtonRenderer sbr = (SortButtonRenderer) tcr;
      sbr.setPressedColumn(column);
      sbr.setSelectedColumn(column);
      h.repaint();
      JTable table = h.getTable();
      if (table.isEditing()) {
        table.getCellEditor().stopCellEditing();
      }
      SortableTableModel model = (SortableTableModel) table.getModel();
      model.sortByColumn(column, SortButtonRenderer.DOWN == sbr.getState(column));
    }
  }

  @Override public void mouseReleased(MouseEvent e) {
    e.getComponent().repaint();
  }
}

class EmptyIcon implements Icon {
  private final Dimension size;

  protected EmptyIcon(Dimension size) {
    this.size = size;
  }

  @Override public void paintIcon(Component c, Graphics g, int x, int y) {
    /* Empty icon */
  }

  @Override public int getIconWidth() {
    return size.width;
  }

  @Override public int getIconHeight() {
    return size.height;
  }
}
