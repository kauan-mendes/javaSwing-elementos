// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());

    JComboBox<String> combo = makeComboBox();
    UIManager.put("ComboBox.font", combo.getFont());

    JCheckBox check = new JCheckBox("<html>addAuxiliaryLookAndFeel<br>(Disable Right Click)");

    LookAndFeel auxLookAndFeel = new AuxiliaryWindowsLookAndFeel();
    UIManager.addPropertyChangeListener(e -> {
      if ("lookAndFeel".equals(e.getPropertyName())) {
        String lnf = e.getNewValue().toString();
        if (lnf.contains("Windows")) {
          if (check.isSelected()) {
            UIManager.addAuxiliaryLookAndFeel(auxLookAndFeel);
          }
          check.setEnabled(true);
        } else {
          UIManager.removeAuxiliaryLookAndFeel(auxLookAndFeel);
          check.setEnabled(false);
        }
      }
    });
    check.addActionListener(e -> {
      String lnf = UIManager.getLookAndFeel().getName();
      if (((JCheckBox) e.getSource()).isSelected() && lnf.contains("Windows")) {
        UIManager.addAuxiliaryLookAndFeel(auxLookAndFeel);
      } else {
        UIManager.removeAuxiliaryLookAndFeel(auxLookAndFeel);
      }
      SwingUtilities.updateComponentTreeUI(getRootPane());
    });

    combo.setEditable(true);

    Box box = Box.createVerticalBox();
    box.add(check);
    box.add(Box.createVerticalStrut(5));
    box.add(combo);
    box.add(Box.createVerticalStrut(5));
    box.add(makeComboBox());
    box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JMenuBar mb = new JMenuBar();
    mb.add(LookAndFeelUtil.createLookAndFeelMenu());
    EventQueue.invokeLater(() -> getRootPane().setJMenuBar(mb));

    add(box, BorderLayout.NORTH);
    add(new JScrollPane(new JTree()));
    setPreferredSize(new Dimension(320, 240));
  }

  private static JComboBox<String> makeComboBox() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    model.addElement("aaa aaa");
    model.addElement("aaa ab bb");
    model.addElement("aaa ab bb cc");
    model.addElement("1354123451234513512");
    model.addElement("bbb1");
    model.addElement("bbb12");
    return new JComboBox<>(model);
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
