package emulator;
import java.awt.*;

import javax.swing.*;

public class Screen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graphics g;
	private int scale = 1;
	private int width = 224 * scale;
    private int height = 256 * scale;
    private CPU cpu;
    
	public Screen(CPU cpu) {
		this.cpu = cpu;
		System.out.println("Screen initialised.");
	}
	
	
	public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
	
    public void paintPixel(boolean white, int x, int y) {
        if (white) {
            if (y > 32 & y <= 64) {
                g.setColor(Color.RED);
            } else if (y > 184 && y <= 240 && x >= 0 && x <= 223) {
                g.setColor(Color.GREEN);
            } else if (y > 238 & y <= 256 & x >= 16 && x < 132) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            int newx = (int) (x * width / 224.0);
            int newy = (int) (y * height / 256.0);
            int pixelwidth = (int) (((x + 1) * width / 224.0) - newx);
            int pixelheight = (int) (((y + 1) * height / 256.0) - newy);
            g.fillRect(newx, newy, pixelwidth, pixelheight);
        }

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
            			paintPixel(true,x,y);
        			} else {
        				//Black
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

//        print_messages();
//        Main.frames_completed++;
    }
	
}
