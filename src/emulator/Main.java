package emulator;

public class Main {

	public static void main(String[] args) {
		CPU cpu = new CPU();
		Emulation emulator = new Emulation();
		emulator.loadGame(cpu, "invaders");
		
		while(true) {
			//Emulation will be stopped when CPU encounters opcode: 0x76 (HLT)
			emulator.Emulate8080(cpu);
		}
		
	}
}
