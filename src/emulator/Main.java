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
		while(true) {
			//Emulation will be stopped when CPU encounters opcode: 0x76 (HLT)
			
			double now = Instant.now().toEpochMilli();
//			System.out.println(now);
//			System.out.println(cpu.lastTimer);
			if(cpu.lastTimer == 0.0) {
				cpu.lastTimer = now;
				cpu.nextInterrupt = cpu.lastTimer + 16.0;
				cpu.whichInterrupt = 1;
			}
			if(now - cpu.lastTimer >= 16) {
				f.repaint();
			}
			if ((cpu.interrupt_enable) && (now>cpu.nextInterrupt)) {
				if (cpu.whichInterrupt == 1) {
					emulator.GenerateInterrupt(cpu,1);
					cpu.whichInterrupt = 2;
	            }
	            else {
	            	emulator.GenerateInterrupt(cpu,2);
	            	cpu.whichInterrupt = 1;
	            }    
				cpu.nextInterrupt = now + 8.0;
			}
			
			double sinceLast = now - cpu.lastTimer;
			int cycles_to_catch_up = (int)(2 * sinceLast);
	        int cycles = 0;
			
	        
	        while (cycles_to_catch_up > cycles) {
//	        	System.out.println(now);
//				System.out.println(cpu.lastTimer);
	        	short op = cpu.memory[cpu.pc];
	            if (op == 0xdb) { //machine specific handling for IN
	                //TODO
	                cpu.pc += 2;
	                cycles+=3;
	            }
	            else if (op == 0xd3) { //machine specific handling for OUT
	            	//TODO
	                cpu.pc += 2;
	                cycles+=3;
//	                cpu.playSounds();
	            }
	            else
	                cycles += emulator.Emulate8080(cpu);
	            
	        }
			
			
		}
		
	}
}
