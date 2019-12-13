package emulator;

import java.time.Instant;

import javax.swing.JFrame;

public class Main {
	static long lastInterrupt = 0;
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Emulation emulator = new Emulation();
		emulator.loadGame(cpu, "invaders");
		JFrame f = new JFrame();
		Screen screen = new Screen(cpu);
//		frame.setSize(800, 600);
		f.add(screen);
		f.pack();
        f.setFocusable(true);
        f.setVisible(true);
        f.setFocusable(true);
        f.requestFocusInWindow();
        int x = 0;
		while(true) {
			//Emulation will be stopped when CPU encounters opcode: 0x76 (HLT)
			emulator.Emulate8080(cpu);
			if(Instant.now().toEpochMilli() - lastInterrupt > 1.0/60.0) {
				if (cpu.interrupt_enable) {
					emulator.GenerateInterrupt(cpu,2);
					lastInterrupt = Instant.now().toEpochMilli();
					f.repaint();
				}
			}
		}
		
	}
}
