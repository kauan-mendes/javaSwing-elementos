// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Optional;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JPopupMenu pop1 = new JPopupMenu();
    pop1.add("000");
    pop1.add("11111");
    pop1.addSeparator();
    pop1.add("2222222");

    JPopupMenu pop2 = new JPopupMenu();
    pop2.add("33333333333333");
    pop2.addSeparator();
    pop2.add("4444");
    pop2.add("5555555555");

    JToolBar toolBar = new JToolBar();
    toolBar.add(makeButton(pop1, "Text", null));
    Component rigid = Box.createRigidArea(new Dimension(5, 5));
    toolBar.add(rigid);
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    URL url = cl.getResource("example/ei0021-16.png");
    Icon icon = url == null ? UIManager.getIcon("html.missingImage") : new ImageIcon(url);
    toolBar.add(makeButton(pop2, "", icon));
    toolBar.add(rigid);
    toolBar.add(makeButton(pop2, "Icon+Text", icon));
    toolBar.add(Box.createGlue());

    add(toolBar, BorderLayout.NORTH);
    setPreferredSize(new Dimension(320, 240));
  }

  private AbstractButton makeButton(JPopupMenu pop, String title, Icon icon) {
    MenuToggleButton b = new MenuToggleButton(title, icon);
    b.setPopupMenu(pop);
    return b;
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

class MenuArrowIcon implements Icon {
  @Override public void paintIcon(Component c, Graphics g, int x, int y) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.translate(x, y);
    g2.setPaint(Color.BLACK);
    g2.drawLine(2, 3, 6, 3);
    g2.drawLine(3, 4, 5, 4);
    g2.drawLine(4, 5, 4, 5);
    g2.dispose();
  }

  @Override public int getIconWidth() {
    return 9;
  }

  @Override public int getIconHeight() {
    return 9;
  }
}

class MenuToggleButton extends JToggleButton {
  private static final Icon ARROW_ICON = new MenuArrowIcon();
  private JPopupMenu popup;

  protected MenuToggleButton() {
    this("", null);
  }

  protected MenuToggleButton(Icon icon) {
    this("", icon);
  }

  protected MenuToggleButton(String text) {
    this(text, null);
  }

  protected MenuToggleButton(String text, Icon icon) {
    super();
    Action action = new AbstractAction(text) {
      @Override public void actionPerformed(ActionEvent e) {
        Component b = (Component) e.getSource();
        Optional.ofNullable(getPopupMenu()).ifPresent(p -> p.show(b, 0, b.getHeight()));
      }
    };
    action.putValue(Action.SMALL_ICON, icon);
    setAction(action);
    setFocusable(false);
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4 + ARROW_ICON.getIconWidth()));
  }

  public JPopupMenu getPopupMenu() {
    return popup;
  }

  public void setPopupMenu(JPopupMenu pop) {
    this.popup = pop;
    pop.addPopupMenuListener(new PopupMenuListener() {
      @Override public void popupMenuCanceled(PopupMenuEvent e) {
        /* not needed */
      }

      @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        /* not needed */
      }

      @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        setSelected(false);
      }
    });
  }

  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension dim = getSize();
    Insets ins = getInsets();
    int x = dim.width - ins.right;
    int y = ins.top + (dim.height - ins.top - ins.bottom - ARROW_ICON.getIconHeight()) / 2;
    ARROW_ICON.paintIcon(this, g, x, y);
  }
}
