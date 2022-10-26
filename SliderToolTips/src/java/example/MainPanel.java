// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import com.sun.java.swing.plaf.windows.WindowsSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.metal.MetalSliderUI;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JSlider slider1 = makeSlider();
    JSlider slider2 = makeSlider();
    slider2.setModel(slider1.getModel());
    setSliderUI(slider2);

    MouseAdapter ma = new SliderPopupListener();
    slider2.addMouseMotionListener(ma);
    slider2.addMouseListener(ma);

    Box box = Box.createVerticalBox();
    box.add(Box.createVerticalStrut(5));
    box.add(makeTitledPanel("Default", slider1));
    box.add(Box.createVerticalStrut(25));
    box.add(makeTitledPanel("Show ToolTip", slider2));
    box.add(Box.createVerticalGlue());

    add(box, BorderLayout.NORTH);
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    setPreferredSize(new Dimension(320, 240));
  }

  private static JSlider makeSlider() {
    JSlider slider = new JSlider(0, 100, 0);
    slider.setPaintTicks(true);
    slider.setMajorTickSpacing(10);
    slider.setMinorTickSpacing(5);
    slider.addMouseWheelListener(e -> {
      JSlider s = (JSlider) e.getComponent();
      s.setValue(s.getValue() - e.getWheelRotation());
    });
    return slider;
  }

  private static Component makeTitledPanel(String title, Component c) {
    JPanel p = new JPanel(new BorderLayout());
    p.setBorder(BorderFactory.createTitledBorder(title));
    p.add(c);
    return p;
  }

  private static void setSliderUI(JSlider slider) {
    if (slider.getUI() instanceof WindowsSliderUI) {
      slider.setUI(new WindowsTooltipSliderUI(slider));
    } else {
      slider.setUI(new MetalTooltipSliderUI());
    }
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

class WindowsTooltipSliderUI extends WindowsSliderUI {
  protected WindowsTooltipSliderUI(JSlider slider) {
    super(slider);
  }

  @Override protected TrackListener createTrackListener(JSlider slider) {
    return new TrackListener() {
      @Override public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          JSlider slider = (JSlider) e.getComponent();
          if (slider.getOrientation() == SwingConstants.VERTICAL) {
            slider.setValue(valueForYPosition(e.getY()));
          } else { // SwingConstants.HORIZONTAL
            slider.setValue(valueForXPosition(e.getX()));
          }
          // switch (slider.getOrientation()) {
          //   case SwingConstants.VERTICAL:
          //     slider.setValue(valueForYPosition(e.getY()));
          //     break;
          //   case SwingConstants.HORIZONTAL:
          //     slider.setValue(valueForXPosition(e.getX()));
          //     break;
          //   default:
          //     throw new IllegalArgumentException(
          //         "orientation must be one of: VERTICAL, HORIZONTAL");
          // }
          super.mousePressed(e); // isDragging = true;
          super.mouseDragged(e);
        } else {
          super.mousePressed(e);
        }
      }

      @Override public boolean shouldScroll(int direction) {
        return false;
      }
    };
  }
}

class MetalTooltipSliderUI extends MetalSliderUI {
  @Override protected TrackListener createTrackListener(JSlider slider) {
    return new TrackListener() {
      @Override public void mousePressed(MouseEvent e) {
        // boolean b = UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag");
        if (SwingUtilities.isLeftMouseButton(e)) {
          JSlider slider = (JSlider) e.getComponent();
          if (slider.getOrientation() == SwingConstants.VERTICAL) {
            slider.setValue(valueForYPosition(e.getY()));
          } else { // SwingConstants.HORIZONTAL
            slider.setValue(valueForXPosition(e.getX()));
          }
          super.mousePressed(e); // isDragging = true;
          super.mouseDragged(e);
        } else {
          super.mousePressed(e);
        }
      }

      @Override public boolean shouldScroll(int direction) {
        return false;
      }
    };
  }
}

class SliderPopupListener extends MouseAdapter {
  private final JWindow toolTip = new JWindow();
  private final JLabel label = new JLabel(" ", SwingConstants.CENTER) {
    @Override public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.width = 32;
      return d;
    }
  };
  private int prevValue = -1;

  protected SliderPopupListener() {
    super();
    label.setOpaque(true);
    label.setBackground(UIManager.getColor("ToolTip.background"));
    label.setBorder(UIManager.getBorder("ToolTip.border"));
    toolTip.add(label);
    toolTip.pack();
  }

  protected void updateToolTip(MouseEvent e) {
    JSlider slider = (JSlider) e.getComponent();
    int intValue = slider.getValue();
    if (prevValue != intValue) {
      label.setText(String.format("%03d", slider.getValue()));
      Point pt = e.getPoint();
      pt.y = (int) SwingUtilities.calculateInnerArea(slider, null).getCenterY();
      SwingUtilities.convertPointToScreen(pt, e.getComponent());
      // int gap = 2;
      // int thumbHeight = UIManager.getInt("Slider.thumbHeight");
      // int trackHeight = 6;
      // int h2 = gap + (thumbHeight + trackHeight) / 2;
      int h2 = slider.getPreferredSize().height / 2;
      Dimension d = label.getPreferredSize();
      pt.translate(-d.width / 2, -d.height - h2);
      toolTip.setLocation(pt);
    }
    prevValue = intValue;
  }

  @Override public void mouseDragged(MouseEvent e) {
    updateToolTip(e);
  }

  @Override public void mousePressed(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      toolTip.setVisible(true);
      updateToolTip(e);
    }
  }

  @Override public void mouseReleased(MouseEvent e) {
    toolTip.setVisible(false);
  }
}
