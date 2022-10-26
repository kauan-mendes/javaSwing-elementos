// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

public final class MainPanel extends JPanel {
  public static final char SHARP = '#';

  private MainPanel() {
    super(new BorderLayout());
    StringBuilder sb = new StringBuilder();
    String dummy = "1111111111111111\n";
    String comment = "#comment\n";
    IntStream.range(0, 200).forEach(i -> {
      sb.append(dummy);
      if (i % 16 == 0) {
        sb.append(comment);
      }
    });

    JTextArea textArea = new JTextArea();
    JScrollPane scroll = new JScrollPane(textArea);
    textArea.setText(sb.toString());
    scroll.setRowHeaderView(new LineNumberView(textArea));
    textArea.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

    JButton button = new JButton("count commented lines: startsWith(\"#\")");

    button.addActionListener(e -> {
      int count = 0;
      StringTokenizer st = new StringTokenizer(textArea.getText(), "\n");
      while (st.hasMoreTokens()) {
        // if (st.nextToken().startsWith(SHARP)) {
        // if (st.nextToken().charAt(0) == SHARP) {
        if (st.nextToken().codePointAt(0) == SHARP) {
          count++;
        }
      }
      // // String#split >>>>
      // for (String line : textArea.getText().split("\\n")) {
      //   if (!line.isEmpty() && line.codePointAt(0) == SHARP) {
      //     count++;
      //   }
      // }
      // // <<<< String#split

      // // LineNumberReader >>>>
      // try (LineNumberReader lnr = new LineNumberReader(new StringReader(textArea.getText()))) {
      //   String line = null;
      //   while ((line = lnr.readLine()) != null) {
      //     if (!line.isEmpty() && line.codePointAt(0) == SHARP) {
      //       count++;
      //     }
      //   }
      // } catch (java.io.IOException ex) {
      //   ex.printStackTrace();
      // }
      // // <<<< LineNumberReader

      // // ElementCount >>>>
      // Document doc = textArea.getDocument();
      // Element root = doc.getDefaultRootElement();
      // try {
      //   for (int i = 0; i < root.getElementCount(); i++) {
      //     Element elm = root.getElement(i);
      //     int len = elm.getEndOffset() - elm.getStartOffset();
      //     String line = doc.getText(elm.getStartOffset(), len);
      //     if (line.codePointAt(0) == SHARP) {
      //       count++;
      //     }
      //   }
      // } catch (BadLocationException ex) {
      //   throw new RuntimeException(ex); // should never happen
      // }
      // // <<<< ElementCount
      String msg = "commented lines: " + count;
      JOptionPane.showMessageDialog(scroll, msg, "title", JOptionPane.INFORMATION_MESSAGE);
    });
    // frame.getRootPane().setDefaultButton(button);
    EventQueue.invokeLater(() -> getRootPane().setDefaultButton(button));

    add(button, BorderLayout.NORTH);
    add(scroll);
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

class LineNumberView extends JComponent {
  private static final int MARGIN = 5;
  private final JTextArea textArea;
  private final FontMetrics fontMetrics;
  // private final int topInset;
  private final int fontAscent;
  private final int fontHeight;
  private final int fontDescent;
  private final int fontLeading;

  protected LineNumberView(JTextArea textArea) {
    super();
    this.textArea = textArea;
    Font font = textArea.getFont();
    fontMetrics = getFontMetrics(font);
    fontHeight = fontMetrics.getHeight();
    fontAscent = fontMetrics.getAscent();
    fontDescent = fontMetrics.getDescent();
    fontLeading = fontMetrics.getLeading();
    // topInset = textArea.getInsets().top;

    textArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override public void insertUpdate(DocumentEvent e) {
        repaint();
      }

      @Override public void removeUpdate(DocumentEvent e) {
        repaint();
      }

      @Override public void changedUpdate(DocumentEvent e) {
        /* not needed */
      }
    });
    textArea.addComponentListener(new ComponentAdapter() {
      @Override public void componentResized(ComponentEvent e) {
        revalidate();
        repaint();
      }
    });
    Insets i = textArea.getInsets();
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
        BorderFactory.createEmptyBorder(i.top, MARGIN, i.bottom, MARGIN - 1)));
    setOpaque(true);
    setBackground(Color.WHITE);
    setFont(font);
  }

  private int getComponentWidth() {
    // Document doc = textArea.getDocument();
    // Element root = doc.getDefaultRootElement();
    // int lineCount = root.getElementIndex(doc.getLength());
    int lineCount = textArea.getLineCount();
    int maxDigits = Math.max(3, Objects.toString(lineCount).length());
    Insets i = getInsets();
    return maxDigits * fontMetrics.stringWidth("0") + i.left + i.right;
    // return 48;
  }

  private int getLineAtPoint(int y) {
    Element root = textArea.getDocument().getDefaultRootElement();
    int pos = textArea.viewToModel(new Point(0, y));
    // Java 9: int pos = textArea.viewToModel2D(new Point(0, y));
    return root.getElementIndex(pos);
  }

  @Override public Dimension getPreferredSize() {
    return new Dimension(getComponentWidth(), textArea.getHeight());
  }

  @Override protected void paintComponent(Graphics g) {
    g.setColor(getBackground());
    Rectangle clip = g.getClipBounds();
    g.fillRect(clip.x, clip.y, clip.width, clip.height);

    g.setColor(getForeground());
    int base = clip.y;
    int start = getLineAtPoint(base);
    int end = getLineAtPoint(base + clip.height);
    int y = start * fontHeight;
    int rmg = getInsets().right;
    for (int i = start; i <= end; i++) {
      String text = Objects.toString(i + 1);
      int x = getComponentWidth() - rmg - fontMetrics.stringWidth(text);
      y += fontAscent;
      g.drawString(text, x, y);
      y += fontDescent + fontLeading;
    }
  }
}
