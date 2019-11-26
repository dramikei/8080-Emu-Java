package emulator;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Emulation {
	
	
	void loadGame(CPU cpu, String name) {
		File file = new File(System.getProperty("user.dir") + "/src/Games/" + name);
		try {
			DataInputStream data = new DataInputStream(new FileInputStream(file));
			int fileSize = (int) file.length();
			for(int i=0;i<fileSize;i++) {
				cpu.memory[i] = (short) data.read();
			}
			data.close();
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	
	
	void Emulate8080(CPU cpu) {
		short opcode = cpu.memory[cpu.pc];
		System.out.println("0x"+String.format("%02x", opcode));
		switch(opcode) {
			case 0x00: break; //NOP
			case 0x01:
				cpu.c = cpu.memory[cpu.pc+1];
				cpu.b = cpu.memory[cpu.pc+2];
				cpu.pc += 2;
				break;
			default: System.out.println("UNIMPLEMENTED OPCODE: "+"0x"+String.format("%02x", opcode)); break;
		}
		cpu.pc += 1;
	}

}
