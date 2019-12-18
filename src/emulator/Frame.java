package emulator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Frame extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	public Frame(JPanel screen) {
		add(screen);
		setTitle("Space Invaders!");
		pack();
        setFocusable(true);
        setVisible(true);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		//TODO
		System.out.println(e.getKeyCode());
		System.exit(1);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//TODO
	}

	@Override
	public void keyTyped(KeyEvent e) {	
	}

}
