// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    String key = "ColorChooser.showPreviewPanelText";
    JTextArea log = new JTextArea();
    JCheckBox check = new JCheckBox(key, getBoolean(UIManager.get(key))) {
      @Override public void updateUI() {
        super.updateUI();
        boolean b = getBoolean(UIManager.getLookAndFeelDefaults().get(key));
        setSelected(b);
        LookAndFeel laf = UIManager.getLookAndFeel();
        log.append(String.format("%s%n  %s: %s%n", laf, key, b));
        UIManager.put(key, b);
      }
    };

    JButton button = new JButton("JColorChooser.showDialog(...)");
    button.addActionListener(e -> {
      UIManager.put(key, check.isSelected());
      Color color = JColorChooser.showDialog(getRootPane(), "JColorChooser", null);
      log.append(String.format("color: %s%n", color));
    });

    JMenuBar mb = new JMenuBar();
    mb.add(LookAndFeelUtil.createLookAndFeelMenu());
    EventQueue.invokeLater(() -> getRootPane().setJMenuBar(mb));

    JPanel p = new JPanel(new GridLayout(2, 1, 10, 10));
    p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    p.add(button);
    p.add(check);
    add(p, BorderLayout.NORTH);
    add(new JScrollPane(log));
    setPreferredSize(new Dimension(320, 240));
  }

  public static boolean getBoolean(Object o) {
    boolean b = false;
    if (o instanceof Boolean) {
      b = (Boolean) o;
    } else if (o == null) {
      b = true;
    }
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
