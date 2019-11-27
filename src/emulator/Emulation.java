package emulator;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Emulation {
	
	/*  
	* 	Original Intel 8080 components:
	*   PC is 16 bits (Unsigned)
	*   SP is 16 bits (Unsigned)
	*   All registers (A-E,H,L) and flags are 8 bit (Unsigned)
	*   Memory is an array of Unsigned 8 bit with size being 0xffff.
	*/  
	
	/*
	 *  As Java does not support 8 bit or 16 bit "unsigned" integer types,
	 *  We are using short instead of uint8 and int instead of uint16.
	 *  
	 *  Following Data will be used to take care of overflow. 
	 *  { Overflow: 0xff + 1 == 0x00 if data type is uint8 but is 0x100 if the data type is more than 8 bits }
	 *  { We take care of Overflow by doing a bitwise and (&). for example, 0x100 & 0xff = 0x00 }
	 *  
	 *  short is 16 bits (Signed)
	 *  int is 32 bits (Signed)
	 *  
	 *  0xff(Hex) -> 255(Deciman) -> 11111111 (Binary) => 8 bits (1 Byte)
	 *  0xffff(Hex) -> 65535 (Decimal) -> 1111111111111111 (Binary) => 16 bits (2 Bytes)
	 *  
	 *  Google if you don't know about signed and unsigned data types.
	 */
	
	
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
			case 0x00: { break; } //NOP
			
			case 0x01: { //LXI B,D16
				cpu.c = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.b = cpu.memory[((cpu.pc+2) & 0xffff)];
				cpu.pc = ((cpu.pc + 2) & 0xffff);
				break;
			}
				
			case 0x02: { //STAX B
				int x = (cpu.b & 0xff) << 8;
				int y = cpu.c & 0xff;
				int addr = x | y;
				cpu.memory[addr] = (short) (cpu.a & 0xff);
				break;
			}
				
			case 0x03: { //INX B 
				cpu.b = (short) ((cpu.b + 1) & 0xff);
				cpu.c = (short) ((cpu.c + 1) & 0xff);
				break;
			}
				
			case 0x18: { break; } //NOP
			
			case 0x24: { //INR H
				short ans = (short) (cpu.h + 1);
				cpu.cc.z = (short) (((ans & 0xff) == 0) ? 1 : 0);
				cpu.cc.s = (short) (((ans & 0x80) != 0) ? 1 : 0);
				cpu.cc.p = Parity((short) (ans & 0xff));
				cpu.h = (short) (ans & 0xff);
				break;
			}
			
			
			case 0x80: { //ADD B
				short ans = (short) (cpu.a + cpu.b);
				cpu.cc.z = (short) (((ans & 0xff) == 0) ? 1 : 0);
				cpu.cc.s = (short) (((ans & 0x80) != 0) ? 1 : 0);
				cpu.cc.cy = (short) ((ans > 0xff) ? 1:0);
				cpu.cc.p = Parity((short) (ans&0xff));
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			
			case 0x86: { //ADD M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short) (cpu.a + cpu.memory[(offset)]);
				cpu.cc.z = (short) (((ans & 0xff) == 0) ? 1 : 0);
				cpu.cc.s = (short) (((ans & 0x80) != 0) ? 1 : 0);
				cpu.cc.cy = (short) ((ans > 0xff) ? 1:0);
				cpu.cc.p = Parity((short) (ans&0xff));
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0xc3: { //JMP addr
				cpu.pc = (int) (((cpu.memory[cpu.pc + 2] & 0xff) << 8) | (cpu.memory[cpu.pc + 1] & 0xff));
				break;
			}
			
			
			case 0xc6: { //ADI Byte
				short ans = (short) (cpu.a + cpu.memory[cpu.pc + 1]);
				cpu.cc.z = (short) (((ans & 0xff) == 0) ? 1 : 0);
				cpu.cc.s = (short) (((ans & 0x80) != 0) ? 1 : 0);
				cpu.cc.cy = (short) ((ans > 0xff) ? 1:0);
				cpu.cc.p = Parity((short) (ans&0xff));
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			default: System.out.println("UNIMPLEMENTED OPCODE: "+"0x"+String.format("%02x", opcode)); System.exit(1); break;
		}
		cpu.pc += 1;
	}
	
	short Parity(short x) {
		return 0;
	}

}
