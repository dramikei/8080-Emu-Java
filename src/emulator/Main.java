package emulator;


import java.time.Instant;

import javax.swing.JFrame;

public class Main {
	static long lastInterrupt = 0;
	static double now = 0.0;
	public static short in_port1;
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Emulation emulator = new Emulation();
		in_port1 = 0;
		emulator.loadGame(cpu, "invaders");
		
		// Screen upscale factor
		int displayScale = 3;
		
		
		//TO BE USED FOR CPU TESTS ONLY
//		//JMP to 0x100
//		cpu.memory[0]=0xc3;    
//		cpu.memory[1]=0;    
//		cpu.memory[2]=0x01;
//		
//		//JMP to Skip DAA tests
//		cpu.memory[0x59d] = 0xc3;
//		cpu.memory[0x59e] = 198;   
//		cpu.memory[0x59f] = 5;    
		
		Screen screen = new Screen(cpu, displayScale);
		Frame f = new Frame(cpu, screen);
		
		
		while(true) {
			//Emulation will be stopped when CPU encounters opcode: 0x76 (HLT)
			now = Instant.now().toEpochMilli();
			if(now - cpu.lastTimer >= 16.6667) {
				
			}
			Thread obj =new Thread(f);
			obj.start();
			
			if(cpu.lastTimer == 0.0) {
				cpu.lastTimer = now;
				cpu.nextInterrupt = cpu.lastTimer + 16.0;
				cpu.whichInterrupt = 1;
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
			
			double sinceLast = (now - cpu.lastTimer);
			int cycles_to_catch_up = (int)(2 * sinceLast);
	        int cycles = 0;
	        while (cycles_to_catch_up > cycles) {
//	        	if(cpu.pc == 0x59c) {
//	    			cpu.memory[0x59c] = 0xc3;
//	    		}

	        	cycles += emulator.Emulate8080(cpu);
	        }
	        cpu.lastTimer = now;
		}
	}
}
