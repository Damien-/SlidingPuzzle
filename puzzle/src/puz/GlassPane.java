package puz;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * GlassPane that takes in all events and prevents
 * user from conducting any action during the time 
 * that the GlassPane is set to visible.
 * 
 */
@SuppressWarnings("serial")
class GlassPane extends JPanel implements MouseListener,
  MouseMotionListener, FocusListener {
	
  Toolkit toolkit;
  JMenuBar menuBar;
  Container contentPane;
  boolean inDrag = false;

  // trigger for redispatchig
  boolean needToRedispatch = false;

  public GlassPane(JMenuBar mb, Container cp) {
    toolkit = Toolkit.getDefaultToolkit();
    menuBar = mb;
    contentPane = cp;
    addMouseListener(this);
    addMouseMotionListener(this);
    addFocusListener(this);
  }

  public void setVisible(boolean v) {
    // Make sure we grab the focus so that key events don't go astray.
    if (v)
      requestFocus();
    super.setVisible(v);
  }

  // Once we have focus, keep it if we're visible
  public void focusLost(FocusEvent fe) {
    if (isVisible())
      requestFocus();
  }

  public void focusGained(FocusEvent fe) {
  }

  // We only need to redispatch if we're not visible, but having full control
  // over this might prove handy.
  public void setNeedToRedispatch(boolean need) {
    needToRedispatch = need;
  }

  public void mouseDragged(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseMoved(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseClicked(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseEntered(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseExited(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mousePressed(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseReleased(MouseEvent e) {
    if (needToRedispatch) {
      redispatchMouseEvent(e);
      inDrag = false;
    }
  }

  private void redispatchMouseEvent(MouseEvent e) {
    boolean inButton = false;
    boolean inMenuBar = false;
    Point glassPanePoint = e.getPoint();
    Component component = null;
    Container container = contentPane;
    Point containerPoint = SwingUtilities.convertPoint(this,
        glassPanePoint, contentPane);
    int eventID = e.getID();

    if (containerPoint.y < 0) {
      inMenuBar = true;
      container = menuBar;
      containerPoint = SwingUtilities.convertPoint(this, glassPanePoint,
          menuBar);
      testForDrag(eventID);
    }

    component = SwingUtilities.getDeepestComponentAt(container,
        containerPoint.x, containerPoint.y);

    if (component == null) {
      return;
    } else {
      inButton = true;
      testForDrag(eventID);
    }

    if (inMenuBar || inButton || inDrag) {
      Point componentPoint = SwingUtilities.convertPoint(this,
          glassPanePoint, component);
      component.dispatchEvent(new MouseEvent(component, eventID, e
          .getWhen(), e.getModifiers(), componentPoint.x,
          componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
    }
  }

  private void testForDrag(int eventID) {
    if (eventID == MouseEvent.MOUSE_PRESSED) {
      inDrag = true;
    }
  }
}
