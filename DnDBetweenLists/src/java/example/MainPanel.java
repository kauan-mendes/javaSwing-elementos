// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
    TransferHandler h = new ListItemTransferHandler();
    p.setBorder(BorderFactory.createTitledBorder("Drag & Drop between JLists"));
    p.add(new JScrollPane(makeList(h)));
    p.add(new JScrollPane(makeList(h)));
    add(p);
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    setPreferredSize(new Dimension(320, 240));
  }

  private static JList<Color> makeList(TransferHandler handler) {
    DefaultListModel<Color> listModel = new DefaultListModel<>();
    listModel.addElement(Color.RED);
    listModel.addElement(Color.BLUE);
    listModel.addElement(Color.GREEN);
    listModel.addElement(Color.CYAN);
    listModel.addElement(Color.ORANGE);
    listModel.addElement(Color.PINK);
    listModel.addElement(Color.MAGENTA);
    JList<Color> list = new JList<Color>(listModel) {
      @Override public void updateUI() {
        setSelectionBackground(null); // Nimbus
        setCellRenderer(null);
        super.updateUI();
        ListCellRenderer<? super Color> renderer = getCellRenderer();
        setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
          Component c = renderer.getListCellRendererComponent(
              list, value, index, isSelected, cellHasFocus);
          c.setForeground(value);
          return c;
        });
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDropMode(DropMode.INSERT);
        setDragEnabled(true);
        setTransferHandler(handler);
      }
    };

    // Disable row Cut, Copy, Paste
    ActionMap map = list.getActionMap();
    Action dummy = new AbstractAction() {
      @Override public void actionPerformed(ActionEvent e) {
        /* Dummy action */
      }
    };
    map.put(TransferHandler.getCutAction().getValue(Action.NAME), dummy);
    map.put(TransferHandler.getCopyAction().getValue(Action.NAME), dummy);
    map.put(TransferHandler.getPasteAction().getValue(Action.NAME), dummy);

    return list;
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

// Demo - BasicDnD (The Java™ Tutorials > ... > Drag and Drop and Data Transfer)
// https://docs.oracle.com/javase/tutorial/uiswing/dnd/basicdemo.html
class ListItemTransferHandler extends TransferHandler {
  protected static final DataFlavor FLAVOR = new DataFlavor(List.class, "List of items");
  private JList<?> source;
  private final List<Integer> indices = new ArrayList<>();
  private int addIndex = -1; // Location where items were added
  private int addCount; // Number of items added.

  // protected ListItemTransferHandler() {
  //   super();
  //   localObjectFlavor = new ActivationDataFlavor(
  //       Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
  //   // localObjectFlavor = new DataFlavor(Object[].class, "Array of items");
  // }

  @Override protected Transferable createTransferable(JComponent c) {
    source = (JList<?>) c;
    // Object[] transferredObjects = source.getSelectedValuesList().toArray(new Object[0]);
    // List<?> transferredObjects = source.getSelectedValuesList();
    // return new DataHandler(transferredObjects, FLAVOR.getMimeType());
    return new Transferable() {
      @Override public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {FLAVOR};
      }

      @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
        return Objects.equals(FLAVOR, flavor);
      }

      @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
          JList<?> src = (JList<?>) c;
          for (int i : src.getSelectedIndices()) {
            indices.add(i);
          }
          return src.getSelectedValuesList();
        } else {
          throw new UnsupportedFlavorException(flavor);
        }
      }
    };
  }

  @Override public boolean canImport(TransferHandler.TransferSupport info) {
    return info.isDrop() && info.isDataFlavorSupported(FLAVOR);
  }

  @Override public int getSourceActions(JComponent c) {
    return TransferHandler.MOVE; // TransferHandler.COPY_OR_MOVE;
  }

  @SuppressWarnings("unchecked")
  @Override public boolean importData(TransferHandler.TransferSupport info) {
    TransferHandler.DropLocation tdl = info.getDropLocation();
    if (!(tdl instanceof JList.DropLocation)) {
      return false;
    }
    JList.DropLocation dl = (JList.DropLocation) tdl;
    JList<?> target = (JList<?>) info.getComponent();
    DefaultListModel<Object> listModel = (DefaultListModel<Object>) target.getModel();
    // boolean insert = dl.isInsert();
    int max = listModel.getSize();
    int index = dl.getIndex();
    // index = index < 0 ? max : index; // If it is out of range, it is appended to the end
    // index = Math.min(index, max);
    index = index >= 0 && index < max ? index : max;
    addIndex = index;
    try {
      // Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
      List<?> values = (List<?>) info.getTransferable().getTransferData(FLAVOR);
      for (Object o : values) {
        int i = index++;
        listModel.add(i, o);
        target.addSelectionInterval(i, i);
      }
      addCount = target.equals(source) ? values.size() : 0;
      return true;
    } catch (UnsupportedFlavorException | IOException ex) {
      return false;
    }
  }

  @Override protected void exportDone(JComponent c, Transferable data, int action) {
    cleanup(c, action == TransferHandler.MOVE);
  }

  private void cleanup(JComponent c, boolean remove) {
    if (remove && !indices.isEmpty()) {
      // If we are moving items around in the same list, we
      // need to adjust the indices accordingly, since those
      // after the insertion point have moved.
      if (addCount > 0) {
        for (int i = 0; i < indices.size(); i++) {
          if (indices.get(i) >= addIndex) {
            indices.set(i, indices.get(i) + addCount);
          }
        }
      }
      JList<?> src = (JList<?>) c;
      DefaultListModel<?> model = (DefaultListModel<?>) src.getModel();
      for (int i = indices.size() - 1; i >= 0; i--) {
        model.remove(indices.get(i));
      }
    }
    indices.clear();
    addCount = 0;
    addIndex = -1;
  }
}
