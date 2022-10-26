// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import com.sun.java.swing.plaf.windows.WindowsSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.metal.MetalSliderUI;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JSlider slider1 = new JSlider(SwingConstants.VERTICAL, 0, 1000, 500);
    setSliderUI(slider1);

    JSlider slider2 = new JSlider(0, 1000, 500);
    setSliderUI(slider2);

    Box box1 = Box.createHorizontalBox();
    box1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    box1.add(new JSlider(SwingConstants.VERTICAL, 0, 1000, 100));
    box1.add(Box.createHorizontalStrut(20));
    box1.add(slider1);
    box1.add(Box.createHorizontalGlue());

    Box box2 = Box.createVerticalBox();
    box2.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));
    box2.add(makeTitledPanel("Default", new JSlider(0, 1000, 100)));
    box2.add(Box.createVerticalStrut(20));
    box2.add(makeTitledPanel("Jump to clicked position", slider2));
    box2.add(Box.createVerticalGlue());

    add(box1, BorderLayout.WEST);
    add(box2);
    // setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
    setPreferredSize(new Dimension(320, 240));
  }

  private static void setSliderUI(JSlider slider) {
    if (slider.getUI() instanceof WindowsSliderUI) {
      slider.setUI(new WindowsJumpToClickedPositionSliderUI(slider));
    } else {
      // NullPointerException ???
      UIManager.put("Slider.trackWidth", 0); // Meaningless settings that are not used?
      UIManager.put("Slider.majorTickLength", 8); // BasicSliderUI#getTickLength(): 8
      Icon missingIcon = UIManager.getIcon("html.missingImage");
      UIManager.put("Slider.verticalThumbIcon", missingIcon);
      UIManager.put("Slider.horizontalThumbIcon", missingIcon);
      slider.setUI(new MetalJumpToClickedPositionSliderUI());
    }
    // slider.setSnapToTicks(false);
    // slider.setPaintTicks(true);
    // slider.setPaintLabels(true);
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

class WindowsJumpToClickedPositionSliderUI extends WindowsSliderUI {
  protected WindowsJumpToClickedPositionSliderUI(JSlider slider) {
    super(slider);
  }

  // // JSlider question: Position after left-click - Stack Overflow
  // // https://stackoverflow.com/questions/518471/jslider-question-position-after-leftclick
  // // TEST:
  // protected void scrollDueToClickInTrack(int direction) {
  //   int value = slider.getValue();
  //   if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
  //     value = this.valueForXPosition(slider.getMousePosition().x);
  //   } else if (slider.getOrientation() == SwingConstants.VERTICAL) {
  //     value = this.valueForYPosition(slider.getMousePosition().y);
  //   }
  //   slider.setValue(value);
  // }

  @Override protected TrackListener createTrackListener(JSlider slider) {
    return new TrackListener() {
      @Override public void mousePressed(MouseEvent e) {
        // boolean b = UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag");
        if (SwingUtilities.isLeftMouseButton(e)) {
          JSlider slider = (JSlider) e.getComponent();
          switch (slider.getOrientation()) {
            case SwingConstants.VERTICAL:
              slider.setValue(valueForYPosition(e.getY()));
              break;
            case SwingConstants.HORIZONTAL:
              slider.setValue(valueForXPosition(e.getX()));
              break;
            default:
              String msg = "orientation must be one of: VERTICAL, HORIZONTAL";
              throw new IllegalArgumentException(msg);
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

class MetalJumpToClickedPositionSliderUI extends MetalSliderUI {
  @Override protected TrackListener createTrackListener(JSlider slider) {
    return new TrackListener() {
      @Override public void mousePressed(MouseEvent e) {
        // boolean b = UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag");
        if (SwingUtilities.isLeftMouseButton(e)) {
          JSlider slider = (JSlider) e.getComponent();
          switch (slider.getOrientation()) {
            case SwingConstants.VERTICAL:
              slider.setValue(valueForYPosition(e.getY()));
              break;
            case SwingConstants.HORIZONTAL:
              slider.setValue(valueForXPosition(e.getX()));
              break;
            default:
              String msg = "orientation must be one of: VERTICAL, HORIZONTAL";
              throw new IllegalArgumentException(msg);
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
