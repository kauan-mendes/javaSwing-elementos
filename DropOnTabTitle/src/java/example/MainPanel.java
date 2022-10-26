// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.IOException;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("00000000", new JScrollPane(makeList(0)));
    tabs.addTab("11111111", new JScrollPane(makeList(1)));
    tabs.addTab("22222222", new JScrollPane(makeList(2)));
    add(tabs);

    new DropTarget(tabs, DnDConstants.ACTION_MOVE, new TabTitleDropTargetListener(), true);
    setPreferredSize(new Dimension(320, 240));
  }

  private static JList<String> makeList(int index) {
    DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement(index + " - 1111");
    model.addElement(index + " - 22222222");
    model.addElement(index + " - 333333333333");
    model.addElement(index + " - asdfasdfasdfasdfasd");
    model.addElement(index + " - AAAAAAAAAAAAAA");
    model.addElement(index + " - ****");
    return new DnDList<>(model);
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

class DnDList<E> extends JList<E> implements DragGestureListener, DragSourceListener, Transferable {
  private static final String NAME = "test";

  protected DnDList() {
    this(null);
  }

  protected DnDList(ListModel<E> model) {
    super(model);
    initDragGestureRecognizer();
  }

  private void initDragGestureRecognizer() {
    DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
        (Component) this, DnDConstants.ACTION_MOVE, (DragGestureListener) this);
  }

  // Interface: DragGestureListener
  @Override public void dragGestureRecognized(DragGestureEvent e) {
    try {
      e.startDrag(DragSource.DefaultMoveDrop, (Transferable) this, (DragSourceListener) this);
    } catch (InvalidDnDOperationException ex) {
      throw new IllegalStateException(ex);
    }
  }

  // Interface: DragSourceListener
  @Override public void dragEnter(DragSourceDragEvent e) {
    e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
  }

  @Override public void dragExit(DragSourceEvent e) {
    e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
  }

  @Override public void dragOver(DragSourceDragEvent e) {
    /* not needed */
  }

  @Override public void dragDropEnd(DragSourceDropEvent e) {
    /* not needed */
  }

  @Override public void dropActionChanged(DragSourceDragEvent e) {
    /* not needed */
  }

  // Interface: Transferable
  // DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
  // DataFlavor FLAVOR = new DataFlavor(Object.class, DataFlavor.javaJVMLocalObjectMimeType);
  @Override public Object getTransferData(DataFlavor flavor) {
    return this;
  }

  @Override public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] {new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME)};
  }

  @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
    return NAME.equals(flavor.getHumanPresentableName());
    // return flavor.getRepresentationClass().equals(Object.class);
  }
}

class TabTitleDropTargetListener implements DropTargetListener {
  private int targetTabIndex = -1;

  @Override public void dropActionChanged(DropTargetDragEvent e) {
    // repaint();
  }

  @Override public void dragExit(DropTargetEvent e) {
    // repaint();
  }

  @Override public void dragEnter(DropTargetDragEvent e) {
    // repaint();
  }

  @Override public void dragOver(DropTargetDragEvent e) {
    if (isDropAcceptable(e)) {
      e.acceptDrag(e.getDropAction());
    } else {
      e.rejectDrag();
    }
    e.getDropTargetContext().getComponent().repaint();
  }

  @SuppressWarnings("unchecked")
  @Override public void drop(DropTargetDropEvent e) {
    try {
      DropTargetContext c = e.getDropTargetContext();
      Component o = c.getComponent();
      Transferable t = e.getTransferable();
      DataFlavor[] f = t.getTransferDataFlavors();

      if (o instanceof JTabbedPane) {
        JTabbedPane jtp = (JTabbedPane) o;
        JScrollPane sp = (JScrollPane) jtp.getComponentAt(targetTabIndex);
        JViewport vp = sp.getViewport();
        JList<String> targetList = (JList<String>) SwingUtilities.getUnwrappedView(vp);
        JList<String> sourceList = (JList<String>) t.getTransferData(f[0]);

        DefaultListModel<String> tm = (DefaultListModel<String>) targetList.getModel();
        DefaultListModel<String> sm = (DefaultListModel<String>) sourceList.getModel();

        int[] indices = sourceList.getSelectedIndices();
        for (int j = indices.length - 1; j >= 0; j--) {
          tm.addElement(sm.remove(indices[j]));
        }
        e.dropComplete(true);
      } else {
        e.dropComplete(false);
      }
    } catch (UnsupportedFlavorException | IOException ex) {
      e.dropComplete(false);
    }
  }

  private boolean isDropAcceptable(DropTargetDragEvent e) {
    DropTargetContext c = e.getDropTargetContext();
    Transferable t = e.getTransferable();
    DataFlavor[] f = t.getTransferDataFlavors();
    Point pt = e.getLocation();
    targetTabIndex = -1;
    Component o = c.getComponent();
    if (o instanceof JTabbedPane && t.isDataFlavorSupported(f[0])) {
      JTabbedPane jtp = (JTabbedPane) o;
      for (int i = 0; i < jtp.getTabCount(); i++) {
        if (jtp.getBoundsAt(i).contains(pt)) {
          targetTabIndex = i;
          break;
        }
      }
      return targetTabIndex >= 0 && targetTabIndex != jtp.getSelectedIndex();
    }
    return false;
  }
}
