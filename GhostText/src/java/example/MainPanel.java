// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;
import javax.swing.*;
import javax.swing.plaf.LayerUI;
import javax.swing.text.JTextComponent;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JTextField field1 = new JTextField("Please enter your E-mail address");
    field1.addFocusListener(new PlaceholderFocusListener(field1));

    JTextField field2 = new JTextField("History Search");
    field2.addFocusListener(new PlaceholderFocusListener(field2));

    Box box = Box.createVerticalBox();
    box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    box.add(makeTitledPanel("E-mail", field1));
    box.add(Box.createVerticalStrut(10));
    box.add(makeTitledPanel("Search", field2));
    box.add(Box.createVerticalStrut(10));
    LayerUI<JTextComponent> layerUI = new PlaceholderLayerUI<>("JLayer version");
    box.add(makeTitledPanel("JLayer", new JLayer<>(new JTextField(), layerUI)));

    add(box, BorderLayout.NORTH);
    setPreferredSize(new Dimension(320, 240));
  }

  private static Component makeTitledPanel(String title, Component c) {
    JPanel p = new JPanel(new BorderLayout());
    p.setBorder(BorderFactory.createTitledBorder(title));
    p.add(c);
    return p;
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

class PlaceholderFocusListener implements FocusListener {
  private static final Color INACTIVE = UIManager.getColor("TextField.inactiveForeground");
  private final String hintMessage;

  protected PlaceholderFocusListener(JTextComponent tf) {
    hintMessage = tf.getText();
    tf.setForeground(INACTIVE);
  }

  @Override public void focusGained(FocusEvent e) {
    JTextComponent tf = (JTextComponent) e.getComponent();
    if (hintMessage.equals(tf.getText()) && INACTIVE.equals(tf.getForeground())) {
      tf.setForeground(UIManager.getColor("TextField.foreground"));
      tf.setText("");
    }
  }

  @Override public void focusLost(FocusEvent e) {
    JTextComponent tf = (JTextComponent) e.getComponent();
    if ("".equals(tf.getText().trim())) {
      tf.setForeground(INACTIVE);
      tf.setText(hintMessage);
    }
  }
}

class PlaceholderLayerUI<V extends JTextComponent> extends LayerUI<V> {
  private final JLabel hint = new JLabel() {
    @Override public void updateUI() {
      super.updateUI();
      setForeground(UIManager.getColor("TextField.inactiveForeground"));
    }
  };

  protected PlaceholderLayerUI(String hintMessage) {
    super();
    hint.setText(hintMessage);
  }

  @Override public void paint(Graphics g, JComponent c) {
    super.paint(g, c);
    Optional.ofNullable(c)
        .filter(JLayer.class::isInstance).map(JLayer.class::cast)
        .map(JLayer::getView)
        .filter(JTextComponent.class::isInstance).map(JTextComponent.class::cast)
        .filter(tc -> tc.getText().isEmpty() && !tc.hasFocus())
        .ifPresent(tc -> paintHint(g, tc));
    // if (c instanceof JLayer) {
    //   JTextComponent tc = (JTextComponent) ((JLayer<?>) c).getView();
    //   if (tc.getText().isEmpty() && !tc.hasFocus()) {
    //     paintHint(g, tc);
    //   }
    // }
  }

  private void paintHint(Graphics g, JTextComponent tc) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setPaint(hint.getBackground());
    Insets i = tc.getInsets();
    Dimension d = hint.getPreferredSize();
    SwingUtilities.paintComponent(g2, hint, tc, i.left, i.top, d.width, d.height);
    // int baseline = tc.getBaseline(tc.getWidth(), tc.getHeight());
    // Font font = tc.getFont();
    // FontRenderContext frc = g2.getFontRenderContext();
    // TextLayout tl = new TextLayout(hintMessage, font, frc);
    // tl.draw(g2, i.left + 2, baseline);
    g2.dispose();
  }

  @Override public void installUI(JComponent c) {
    super.installUI(c);
    if (c instanceof JLayer) {
      ((JLayer<?>) c).setLayerEventMask(AWTEvent.FOCUS_EVENT_MASK);
    }
  }

  @Override public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    if (c instanceof JLayer) {
      ((JLayer<?>) c).setLayerEventMask(0);
    }
  }

  @Override protected void processFocusEvent(FocusEvent e, JLayer<? extends V> l) {
    l.getView().repaint();
  }
}
