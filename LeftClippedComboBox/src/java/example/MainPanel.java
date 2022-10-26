// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    ComboBoxModel<String> model = makeComboBoxModel();

    JComboBox<String> combo = new JComboBox<>(model);
    initComboBoxRenderer(combo);

    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(makeTitledPanel("Left Clip JComboBox", combo), BorderLayout.NORTH);
    add(makeTitledPanel("Default JComboBox", new JComboBox<>(model)), BorderLayout.SOUTH);
    setPreferredSize(new Dimension(320, 240));
  }

  private static JButton getArrowButton(Container box) {
    for (Component c : box.getComponents()) {
      if (c instanceof JButton) { // && "ComboBox.arrowButton".equals(c.getName())) {
        // System.out.println(c.getName());
        return (JButton) c;
      }
    }
    return null;
  }

  private static Component makeTitledPanel(String title, Component c) {
    Box box = Box.createVerticalBox();
    box.setBorder(BorderFactory.createTitledBorder(title));
    box.add(Box.createVerticalStrut(2));
    box.add(c);
    return box;
  }

  private static ComboBoxModel<String> makeComboBoxModel() {
    String str = String.join("/", Collections.nCopies(5, "12345678901234567890"));
    DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
    m.addElement(str + ".jpg");
    m.addElement("aaa.tif");
    m.addElement("\\1234567890\\1234567890\\1234567890.avi");
    m.addElement("1234567890.pdf");
    m.addElement("c:/" + str + ".mpg");
    m.addElement("https://localhost/" + str + ".jpg");
    return m;
  }

  private static void initComboBoxRenderer(JComboBox<String> combo) {
    JButton arrowButton = getArrowButton(combo);
    combo.setRenderer(new DefaultListCellRenderer() {
      @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel) {
          String text = Objects.toString(value, "");
          FontMetrics fm = c.getFontMetrics(c.getFont());
          int width = getAvailableWidth(combo, index);
          ((JLabel) c).setText(getLeftClippedText(text, fm, width));
        }
        return c;
      }

      private int getAvailableWidth(JComboBox<String> combo, int index) {
        int itb = 0;
        int ilr = 0;
        Insets insets = getInsets();
        itb += insets.top + insets.bottom;
        ilr += insets.left + insets.right;
        insets = combo.getInsets();
        itb += insets.top + insets.bottom;
        ilr += insets.left + insets.right;
        int availableWidth = combo.getWidth() - ilr;
        if (index < 0) {
          // @see BasicComboBoxUI#rectangleForCurrentValue
          int buttonSize = combo.getHeight() - itb;
          if (Objects.nonNull(arrowButton)) {
            buttonSize = arrowButton.getWidth();
          }
          availableWidth -= buttonSize;
          JTextField tf = (JTextField) combo.getEditor().getEditorComponent();
          insets = tf.getMargin();
          // availableWidth -= insets.left;
          availableWidth -= insets.left + insets.right;
        }
        return availableWidth;
      }

      private String getLeftClippedText(String text, FontMetrics fm, int availableWidth) {
        // <blockquote cite="https://tips4java.wordpress.com/2008/11/12/left-dot-renderer/">
        // @title Left Dot Renderer
        // @author Rob Camick
        // FontMetrics fm = getFontMetrics(getFont());
        // if (fm.stringWidth(text) > availableWidth) {
        //   String dots = "...";
        //   int textWidth = fm.stringWidth(dots);
        //   int nChars = text.length() - 1;
        //   while (nChars > 0) {
        //     textWidth += fm.charWidth(text.charAt(nChars));
        //     if (textWidth > availableWidth) {
        //       break;
        //     }
        //     nChars--;
        //   }
        //   setText(dots + text.substring(nChars + 1));
        // }
        // </blockquote>
        if (fm.stringWidth(text) <= availableWidth) {
          return text;
        }
        String dots = "...";
        int textWidth = fm.stringWidth(dots);
        int len = text.length();
        // @see Unicode surrogate programming with the Java language
        // https://www.ibm.com/developerworks/library/j-unicode/index.html
        // https://www.ibm.com/developerworks/jp/ysl/library/java/j-unicode_surrogate/index.html
        int[] acp = new int[text.codePointCount(0, len)];
        int j = acp.length;
        for (int i = len; i > 0; i = text.offsetByCodePoints(i, -1)) {
          int cp = text.codePointBefore(i);
          textWidth += fm.charWidth(cp);
          if (textWidth > availableWidth) {
            break;
          }
          acp[--j] = cp;
        }
        return dots + new String(acp, j, acp.length - j);
      }
    });
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
