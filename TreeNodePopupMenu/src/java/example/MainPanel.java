// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.tree.TreePath;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JTree tree = new JTree();
    tree.setComponentPopupMenu(new TreePopupMenu());

    JButton button = new JButton("Clear node selection");
    button.addActionListener(e -> tree.clearSelection());

    add(new JScrollPane(tree));
    add(button, BorderLayout.SOUTH);
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

class TreePopupMenu extends JPopupMenu {
  protected TreePopupMenu() {
    super();
    add("path").addActionListener(e -> {
      JTree tree = (JTree) getInvoker();
      TreePath path = tree.getSelectionPath();
      if (path != null) {
        JOptionPane.showMessageDialog(tree, path, "path", JOptionPane.INFORMATION_MESSAGE);
      }
    });
    add("dummy");
  }

  @Override public void show(Component c, int x, int y) {
    if (c instanceof JTree) {
      JTree tree = (JTree) c;
      TreePath path = tree.getPathForLocation(x, y);
      // tree.getSelectionCount() > 0 == tree.getSelectionPaths() != null
      TreePath[] tsp = tree.getSelectionPaths();
      if (tsp != null && Arrays.asList(tsp).contains(path)) {
        super.show(c, x, y);
      }
    }
  }
}
