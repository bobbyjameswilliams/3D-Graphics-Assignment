import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Museum extends JFrame implements ActionListener {

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Museum_GLEventListener glEventListener;
  private final FPSAnimator animator;
  private Camera camera;

  public static void main(String[] args) {
    Museum b1 = new Museum("Museum - Bobby Williams acb18bw");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Museum(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Museum_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);

    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);

    JPanel p = new JPanel();
      JButton b = new JButton("Pose 1");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Pose 2");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Pose 3");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Pose 4");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Pose 5");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("day");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("night");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("raised arms");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("main lights off");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("main lights on");
      b.addActionListener(this);
      p.add(b);
    this.add(p, BorderLayout.SOUTH);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("Pose 1")) {
      glEventListener.pose1();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Pose 2")) {
      glEventListener.pose2();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Pose 3")) {
      glEventListener.pose3();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Pose 4")) {
      glEventListener.pose4();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Pose 5")) {
      glEventListener.pose5();
    }
    else if (e.getActionCommand().equalsIgnoreCase("day")) {
      glEventListener.outsideDay();
    }
    else if (e.getActionCommand().equalsIgnoreCase("night")) {
      glEventListener.outsideNight();
    }
    else if (e.getActionCommand().equalsIgnoreCase("raised arms")) {
      glEventListener.raisedArms();
    }
    else if (e.getActionCommand().equalsIgnoreCase("main lights off")) {
      glEventListener.turnOffMainLights();
    }
    else if (e.getActionCommand().equalsIgnoreCase("main lights on")) {
      glEventListener.turnOnMainLights();
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }

}

class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;

  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }

  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;

  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }

    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */
  public void mouseMoved(MouseEvent e) {
    lastpoint = e.getPoint();
  }
}