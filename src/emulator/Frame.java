package emulator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Frame extends JFrame implements KeyListener, Runnable {
	private static final long serialVersionUID = 1L;
	CPU cpu;
	public Frame(CPU cpu, JPanel screen) {
		this.cpu = cpu;
		add(screen);
		setTitle("Space Invaders!");
		pack();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        setVisible(true);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyChar()) {
			case 'z':
			case 'Z':
				Main.in_port1 |= 0x20;
				break;
			case 'x':
			case 'X':
				Main.in_port1 |= 0x40;
				break;
			case '.':
				Main.in_port1 |= 0x10;
				break;
			case 'c':
			case 'C':
				Main.in_port1 |= 0x1;
				break;
			case '1':
				Main.in_port1 |= 0x04;
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyChar()) {
			case 'z':
			case 'Z':
				Main.in_port1 &= ~0x20;
				break;
			case 'x':
			case 'X':
				Main.in_port1 &= ~0x40;
				break;
			case '.':
				Main.in_port1 &= ~0x10;
				break;
			case 'c':
			case 'C':
				Main.in_port1 &= ~0x1;
				break;
			case '1':
				Main.in_port1 &= ~0x04;
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {	
	}

	@Override
	public void run() {
		repaint();
		
	}

}
