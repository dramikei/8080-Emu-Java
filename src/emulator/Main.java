package emulator;

public class Main {

	public static void main(String[] args) {
		CPU cpu = new CPU();
		Emulation emulator = new Emulation();
		emulator.loadGame(cpu, "invaders");
		for(int i =0;i<100000;i++) {
			// Loop is a placeholder, will be removed in future version.
			emulator.Emulate8080(cpu);
		}
		
	}
}
