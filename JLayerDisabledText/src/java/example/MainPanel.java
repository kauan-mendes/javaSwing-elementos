// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.plaf.LayerUI;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super();
    UIManager.put("Button.disabledText", Color.RED);
    JButton button1 = makeButton("Default");
    JButton button2 = makeButton("setForeground");
    DisableInputLayerUI<AbstractButton> layerUI = new DisableInputLayerUI<>();

    JCheckBox check = new JCheckBox("setEnabled", true);
    check.addActionListener(e -> {
      boolean isSelected = ((JCheckBox) e.getSource()).isSelected();
      button1.setEnabled(isSelected);
      button2.setEnabled(isSelected);
      button2.setForeground(isSelected ? Color.BLACK : Color.RED);
      layerUI.setLocked(!isSelected);
    });

    JPanel p1 = new JPanel();
    p1.setBorder(BorderFactory.createTitledBorder("setEnabled"));
    p1.add(button1);
    p1.add(button2);
    p1.add(new JLayer<>(makeButton("JLayer"), layerUI));

    JPanel p2 = new JPanel();
    p2.setBorder(BorderFactory.createTitledBorder("Focus dummy"));
    p2.add(new JTextField(16));
    p2.add(new JButton("dummy"));

    JPanel panel = new JPanel(new GridLayout(2, 1));
    panel.add(p1);
    panel.add(p2);

    add(panel);
    add(check);

    JMenuBar mb = new JMenuBar();
    mb.add(LookAndFeelUtil.createLookAndFeelMenu());
    EventQueue.invokeLater(() -> getRootPane().setJMenuBar(mb));

    setPreferredSize(new Dimension(320, 240));
  }

  private static JButton makeButton(String title) {
    JPopupMenu pop = new JPopupMenu();
    pop.add(title);
    JButton button = new JButton(title);
    if (title.length() > 0) {
      button.setMnemonic(title.codePointAt(0));
    }
    button.setToolTipText(title);
    button.setComponentPopupMenu(pop);
    return button;
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

class DisableInputLayerUI<V extends AbstractButton> extends LayerUI<V> {
  private static final String CMD_BLOCKING = "lock";
  private static final boolean DEBUG_POPUP_BLOCK = false;
  private final transient MouseListener dmyMouseListener = new MouseAdapter() {
    /* Dummy listener */
  };
  private final transient KeyListener dmyKeyListener = new KeyAdapter() {
    /* Dummy listener */
  };
  private boolean isBlocking;

  @Override public void installUI(JComponent c) {
    super.installUI(c);
    if (c instanceof JLayer) {
      JLayer<?> layer = (JLayer<?>) c;
      if (DEBUG_POPUP_BLOCK) {
        layer.getGlassPane().addMouseListener(dmyMouseListener);
        layer.getGlassPane().addKeyListener(dmyKeyListener);
      }
      layer.setLayerEventMask(
          AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
          | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK
          | AWTEvent.FOCUS_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
    }
  }

  @Override public void uninstallUI(JComponent c) {
    if (c instanceof JLayer) {
      JLayer<?> layer = (JLayer<?>) c;
      layer.setLayerEventMask(0);
      if (DEBUG_POPUP_BLOCK) {
        layer.getGlassPane().removeMouseListener(dmyMouseListener);
        layer.getGlassPane().removeKeyListener(dmyKeyListener);
      }
    }
    super.uninstallUI(c);
  }

  @Override protected void processComponentEvent(ComponentEvent e, JLayer<? extends V> l) {
    System.out.println("processComponentEvent");
  }

  @Override protected void processKeyEvent(KeyEvent e, JLayer<? extends V> l) {
    System.out.println("processKeyEvent");
  }

  @Override protected void processFocusEvent(FocusEvent e, JLayer<? extends V> l) {
    System.out.println("processFocusEvent");
  }

  @Override public void eventDispatched(AWTEvent e, JLayer<? extends V> l) {
    if (isBlocking && e instanceof InputEvent) {
      ((InputEvent) e).consume();
    }
  }

  public void setLocked(boolean flag) {
    boolean old = isBlocking;
    isBlocking = flag;
    firePropertyChange(CMD_BLOCKING, old, isBlocking);
  }

  @Override public void applyPropertyChange(PropertyChangeEvent e, JLayer<? extends V> l) {
    if (CMD_BLOCKING.equals(e.getPropertyName())) {
      AbstractButton b = l.getView();
      b.setFocusable(!isBlocking);
      b.setMnemonic(isBlocking ? 0 : b.getText().codePointAt(0));
      b.setForeground(isBlocking ? Color.RED : Color.BLACK);
      l.getGlassPane().setVisible((Boolean) e.getNewValue());
    }
  }
}

// class LockingGlassPane extends JPanel {
//   protected LockingGlassPane() {
//     super();
//     setOpaque(false);
//     setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
//       @Override public boolean accept(Component c) {
//         return false;
//       }
//     });
//     addKeyListener(new KeyAdapter() {});
//     addMouseListener(new MouseAdapter() {});
//     requestFocusInWindow();
//     setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//   }
//   @Override public void setVisible(boolean flag) {
//     super.setVisible(flag);
//     setFocusTraversalPolicyProvider(flag);
//   }
// }

// class LockingGlassPane extends JPanel {
//   protected LockingGlassPane() {
//     super();
//     setOpaque(false);
//     setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//   }
//   @Override public void setVisible(boolean isVisible) {
//     boolean oldVisible = isVisible();
//     super.setVisible(isVisible);
//     JRootPane rootPane = SwingUtilities.getRootPane(this);
//     if (rootPane != null && isVisible() != oldVisible) {
//       rootPane.getLayeredPane().setVisible(!isVisible);
//     }
//   }
//   @Override protected void paintComponent(Graphics g) {
//     JRootPane rootPane = SwingUtilities.getRootPane(this);
//     if (rootPane != null) {
//       // http://weblogs.java.net/blog/alexfromsun/archive/2008/01/
//       // it is important to call print() instead of paint() here
//       // because print() doesn't affect the frame's double buffer
//       rootPane.getLayeredPane().print(g);
//     }
//     super.paintComponent(g);
//   }
// }

// class PrintGlassPane extends JPanel {
//   // TexturePaint texture = TextureFactory.createCheckerTexture(4);
//   protected PrintGlassPane() {
//     super((LayoutManager) null);
//     setOpaque(false);
//   }
//   @Override public void setVisible(boolean isVisible) {
//     boolean oldVisible = isVisible();
//     super.setVisible(isVisible);
//     JRootPane rootPane = SwingUtilities.getRootPane(this);
//     if (rootPane != null && isVisible() != oldVisible) {
//       rootPane.getLayeredPane().setVisible(!isVisible);
//     }
//   }
//   @Override protected void paintComponent(Graphics g) {
//     super.paintComponent(g);
//     JRootPane rootPane = SwingUtilities.getRootPane(this);
//     if (rootPane != null) {
//       // http://weblogs.java.net/blog/alexfromsun/archive/2008/01/
//       // it is important to call print() instead of paint() here
//       // because print() doesn't affect the frame's double buffer
//       rootPane.getLayeredPane().print(g);
//     }
//     // Graphics2D g2 = (Graphics2D) g.create();
//     // g2.setPaint(texture);
//     // g2.fillRect(0, 0, getWidth(), getHeight());
//     // g2.dispose();
//   }
// }

// @see https://java.net/projects/swingset3/sources/svn/content/trunk/SwingSet3/src/com/sun/swingset3/SwingSet3.java
final class LookAndFeelUtil {
  private static String lookAndFeel = UIManager.getLookAndFeel().getClass().getName();

  private LookAndFeelUtil() {
    /* Singleton */
  }

  public static JMenu createLookAndFeelMenu() {
    JMenu menu = new JMenu("LookAndFeel");
    ButtonGroup lafGroup = new ButtonGroup();
    for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
      menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName(), lafGroup));
    }
    return menu;
  }

  private static JMenuItem createLookAndFeelItem(String laf, String lafClass, ButtonGroup bg) {
    JMenuItem lafItem = new JRadioButtonMenuItem(laf, lafClass.equals(lookAndFeel));
    lafItem.setActionCommand(lafClass);
    lafItem.setHideActionText(true);
    lafItem.addActionListener(e -> {
      ButtonModel m = bg.getSelection();
      try {
        setLookAndFeel(m.getActionCommand());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        UIManager.getLookAndFeel().provideErrorFeedback((Component) e.getSource());
      }
    });
    bg.add(lafItem);
    return lafItem;
  }

  private static void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    String oldLookAndFeel = LookAndFeelUtil.lookAndFeel;
    if (!oldLookAndFeel.equals(lookAndFeel)) {
      UIManager.setLookAndFeel(lookAndFeel);
      LookAndFeelUtil.lookAndFeel = lookAndFeel;
      updateLookAndFeel();
      // firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);
    }
  }

  private static void updateLookAndFeel() {
    for (Window window : Window.getWindows()) {
      SwingUtilities.updateComponentTreeUI(window);
    }
  }
}
