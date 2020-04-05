package emulator;


import java.time.Instant;

import javax.swing.JFrame;

public class Main {
	static int CYCLES_PER_FRAME = 4_000_000 / 60;
	static long lastInterrupt = 0;
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Emulation emulator = new Emulation();
		emulator.loadGame(cpu, "invaders", 0x00);
		
		// Screen upscale factor
		int displayScale = 3;
		
		
		//TO BE USED FOR CPU TESTS ONLY (CPUDIAG.bin)
		//JMP to 0x100
//		cpu.memory[0]=0xc3;    
//		cpu.memory[1]=0;    
//		cpu.memory[2]=0x01;
		
//		cpu.memory[368] = 0x7;
		//JMP to Skip DAA tests (CPUDIAG.bin) 
//		cpu.memory[0x59c] = 0xc3; //JMP    
//	    cpu.memory[0x59d] = 0xc2;    
//	    cpu.memory[0x59e] = 0x05;
//		
		Screen screen = new Screen(cpu, displayScale);
		Frame f = new Frame(cpu, screen);

		while(true) {
			halfStep(cpu,emulator,f,true);
			halfStep(cpu,emulator,f,false);
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        
		}
	}
	
	
	static void halfStep(CPU cpu, Emulation emulator,Frame frame, boolean topHalf) {
		Thread obj =new Thread(frame);
		int cyclesSpent = 0;
		while (cyclesSpent < (CYCLES_PER_FRAME / 2)) {
            int cycles = emulator.Emulate8080(cpu);
            cyclesSpent += cycles;
        }
		System.out.println("Redrawing Screen");
		obj.start();
		if (cpu.interrupt_enable) {
			if(topHalf) {
				emulator.GenerateInterrupt(cpu,1);
			} else {
				emulator.GenerateInterrupt(cpu,2);
			}
			
		}
	}
}
