// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    String[] columnNames = {"String", "Integer", "Boolean"};
    Object[][] data = {
        {"aaa", 12, true}, {"bbb", 5, false}, {"CCC", 92, true}, {"DDD", 0, false}
    };
    TableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
      }
    };
    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    sp.setTopComponent(new JScrollPane(new JTable(model)));
    sp.setBottomComponent(new JScrollPane(new JTree()));

    JRadioButton r0 = new JRadioButton("0.0", true);
    r0.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        sp.setResizeWeight(0d);
      }
    });

    JRadioButton r1 = new JRadioButton("0.5");
    r1.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        sp.setResizeWeight(.5);
      }
    });

    JRadioButton r2 = new JRadioButton("1.0");
    r2.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        sp.setResizeWeight(1d);
      }
    });

    ButtonGroup bg = new ButtonGroup();
    JPanel p = new JPanel();
    p.add(new JLabel("JSplitPane#setResizeWeight: "));
    Stream.of(r0, r1, r2).forEach(r -> {
      bg.add(r);
      p.add(r);
    });
    add(p, BorderLayout.NORTH);
    add(sp);
    setPreferredSize(new Dimension(320, 240));

    EventQueue.invokeLater(() -> sp.setDividerLocation(.5));
    // TEST: EventQueue.invokeLater(() -> sp.setResizeWeight(.5));
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
