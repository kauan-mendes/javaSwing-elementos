// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.util.stream.IntStream;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private static final String LF = "\n";

  private MainPanel() {
    super(new BorderLayout());
    StringBuilder buf = new StringBuilder();
    IntStream.range(0, 1000).forEach(i -> buf.append(i).append(LF));

    JSplitPane sp = new JSplitPane();
    sp.setLeftComponent(new JScrollPane(new JTextArea(buf.toString())));

    UIManager.put("ScrollBar.minimumThumbSize", new Dimension(32, 32));
    sp.setRightComponent(new JScrollPane(new JTextArea(buf.toString())));
    sp.setResizeWeight(.5);
    add(sp);
  }

  @Override public Dimension getPreferredSize() {
    return new Dimension(320, 240);
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
