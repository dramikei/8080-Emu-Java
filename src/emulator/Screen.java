package emulator;
import java.awt.*;

import javax.swing.*;

public class Screen extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Graphics g;
	private int width = 224;
    private int height = 256;
    private CPU cpu;
    
	public Screen(CPU cpu) {
		this.cpu = cpu;
		System.out.println("Screen initialised.");
	}
	
	
	public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
	
    public void paintPixel(int x, int y) {
    	if (y > 32 & y <= 64) {
            g.setColor(Color.RED);
        } else if (y > 184 && y <= 240 && x >= 0 && x <= 223) {
            g.setColor(Color.GREEN);
        } else if (y > 238 & y <= 256 & x >= 16 && x < 132) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(x, y, 1, 1);
    }
    
    /**
     * Paints full screen from screen memory.
     */
    private void paintFullScreen() {
        width=getWidth();
        height=getHeight();
        int i = 0x2400;
        for (int col = 0;col<width;col++) {
        	for (int row = height; row>0; row -=8) {
        		for (int j = 0; j<8;j++) {
        			int idx = (row-j)*width+col;
        			if ((cpu.memory[i] & 1 << j) != 0) {
        				//White
        				int x = (int)(idx%width);
            			int y = (int)(Math.floor(idx/width));
            			paintPixel(x,y);
        			}
        		}
        		i++;
        	}
        }
    }

    /**
     * Paints full screen from screen memory. Public.
     */
    public void paintScreen() {
        repaint();
    }


    /**
     * Paints the component. It has to be called through paintScreen().
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        paintFullScreen();
    }
}