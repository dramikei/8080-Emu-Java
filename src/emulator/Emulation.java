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
	 *  0xff(Hex) -> 255(Decimal) -> 11111111 (Binary) => 8 bits (1 Byte)
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
			
			case 0x04: { //INR B
				short ans = (short) (cpu.b +1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.b = (short) (ans & 0xff);
				break;
			}
			
			case 0x05: { //DCR B
				short ans = (short) (cpu.b -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.b = (short) (ans & 0xff);
				break;
			}
			
			case 0x06: { //MVI B,D8
				cpu.b = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.pc = ((cpu.pc + 1) & 0xffff);
				break;
			}
			
			case 0x09: { //DAD B
				int BC = ((cpu.b << 8) | (cpu.c))&0xffff;
				int HL = ((cpu.h << 8) | (cpu.l))&0xffff;
				int ans = HL + BC;
				cpu.h = (short)((ans >> 8)&0xff);
				cpu.l = (short) (ans&0xff);
				set_cc_carry_pair(ans,cpu);
				break;
			}
			
			case 0x0b: { //DCX B
				cpu.b = (short) ((cpu.b - 1) & 0xff);
				cpu.c = (short) ((cpu.c - 1) & 0xff);
				break;
			}
			
			case 0x0c: { //INR C
				short ans = (short) (cpu.c + 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.c = (short) (ans & 0xff);
				break;
			}
			
			case 0x0d: { //DCR C
				short ans = (short) (cpu.c -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.c = (short) (ans & 0xff);
				break;
			}
			
			case 0x0e: { //MVI C,D8
				cpu.c = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.pc = ((cpu.pc + 1) & 0xffff);
				break;
			}
			
			case 0x0f: { //RRC
				short ans = cpu.a;
				cpu.a = (short) (((ans&1) << 7) | (ans >> 1));
				cpu.cc.cy = (short) (((ans & 1) == 1) ? 1 : 0);
				break;
			}
			
			case 0x11: { //LXI D, D16
				cpu.e = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.d = cpu.memory[((cpu.pc+2) & 0xffff)];
				cpu.pc = ((cpu.pc + 2) & 0xffff);
				break;
			}
			
			case 0x12: { //STAX D
				int x = (cpu.d & 0xff) << 8;
				int y = cpu.e & 0xff;
				int addr = x | y;
				cpu.memory[addr] = (short) (cpu.a & 0xff);
				break;
			}
			
			case 0x13: { //INX D
				int DE = (((cpu.d << 8) | (cpu.e))&0xffff) + 1;
				cpu.d = (short) ((DE>>8) & 0xff);
				cpu.e = (short) ((DE) & 0xff);
				break;
			}
			
			case 0x14: { //INR D
				short ans = (short) (cpu.d + 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.d = (short) (ans & 0xff);
				break;
			}
			case 0x15: { //DCR D
				short ans = (short) (cpu.d -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.d = (short) (ans & 0xff);
				break;
			}
				
			case 0x18: { break; } //NOP
			
			case 0x19: { //DAD D
				int DE = ((cpu.d << 8) | (cpu.e))&0xffff;
				int HL = ((cpu.h << 8) | (cpu.l))&0xffff;
				int ans = HL + DE;
				cpu.h = (short)((ans >> 8)&0xff);
				cpu.l = (short) (ans&0xff);
				set_cc_carry_pair(ans,cpu);
				break;
			}
			
			case 0x1a: { //LDAX D
				cpu.a = cpu.memory[((cpu.d << 8) | (cpu.e))&0xffff];
				break;
			}
			
			case 0x1b: { //DCX D
				cpu.d = (short) ((cpu.b - 1) & 0xff);
				cpu.e = (short) ((cpu.c - 1) & 0xff);
				break;
			}
			
			
			case 0x1c: { //INR E
				short ans = (short) (cpu.e + 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.e = (short) (ans & 0xff);
				break;
			}
			
			case 0x1d: { //DCR E
				short ans = (short) (cpu.e -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.e = (short) (ans & 0xff);
				break;
			}
			
			case 0x1f: { //RAR
				short ans = cpu.a;
				cpu.a = (short)(((cpu.cc.cy << 7) | (ans >> 1))&0xff);
				break;
			}
			
			case 0x21: { //LXI H
				cpu.l = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.h = cpu.memory[((cpu.pc+2) & 0xffff)];
				cpu.pc = ((cpu.pc + 2) & 0xffff);
				break;
			}
			
			case 0x23: { //INX H
				cpu.h = (short) ((cpu.h + 1) & 0xff);
				cpu.l = (short) ((cpu.l + 1) & 0xff);
				break;
			}
			
			case 0x24: { //INR H
				short ans = (short) (cpu.h + 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.h = (short) (ans & 0xff);
				break;
			}
			
			case 0x25: { //DCR H
				short ans = (short) (cpu.h -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.h = (short) (ans & 0xff);
				break;
			}
			
			
			case 0x26: { //MVI H,D8
				cpu.h = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.pc = ((cpu.pc + 1) & 0xffff);
				break;
			}

//
///////////// IGNORED as it depends on Auxiliary Carry which is NOT implemented. ////////////////////
//			case 0x27: { //DAA 
//
//				short lsn = (short) (cpu.a&0xf); // Least significant Nibble
//				short msn = (short) ((cpu.a >> 4)&0xf); // Most significant Nibble
//				
//				break;
//			}
			
			case 0x29: { //DAD H
				int HL = ((cpu.h << 8) | (cpu.l))&0xffff;
				int ans = HL <<1;
				cpu.h = (short)((ans >> 8)&0xff);
				cpu.l = (short) (ans&0xff);
				set_cc_carry_pair(ans,cpu);
				break;
			}
			
			case 0x2b: { //DCX H
				cpu.h = (short) ((cpu.b - 1) & 0xff);
				cpu.l = (short) ((cpu.c - 1) & 0xff);
				break;
			}
			
			case 0x2c: { //INR L
				short ans = (short) (cpu.l + 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.l = (short) (ans & 0xff);
				break;
			}
			
			case 0x2d: { //DCR L
				short ans = (short) (cpu.l -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.l = (short) (ans & 0xff);
				break;
			}
			
			case 0x2e: { //MVI L,D8
				cpu.l = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.pc = ((cpu.pc + 1) & 0xffff);
				break;
			}
			
			case 0x2f: { //CMA
				cpu.a = (short) ((~cpu.a)&0xff);
				break;
			}
			
			case 0x31: { //LXI SP
				cpu.sp = ((cpu.memory[((cpu.pc+2) & 0xffff)]&0xff) << 8) | (cpu.memory[((cpu.pc+1) & 0xffff)]&0xff);
				cpu.pc = ((cpu.pc + 2) & 0xffff);
				break;
			}
			
			case 0x32: { //STA addr
				int addr = (((cpu.memory[cpu.pc+2]&0xff)<<8) | (cpu.memory[cpu.pc+1])&0xff)&0xffff;
				cpu.memory[addr] = cpu.a;
				cpu.pc = (cpu.pc+2)&0xffff;
				break;
			}
			
			case 0x33: { //INX SP
				cpu.sp = (short) ((cpu.sp + 1) & 0xffff);
				break;
			}
			
			
			case 0x34: { //INR M
				short ans = (short) (cpu.memory[(((cpu.h&0xff)<<8)|(cpu.l&0xff))&0xffff]+1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.memory[(((cpu.h&0xff)<<8)|(cpu.l&0xff))&0xffff] = (short) (ans & 0xff);
				break;
			}
			
			case 0x35: { //DCR M
				short ans = (short) (cpu.memory[(((cpu.h&0xff)<<8)|(cpu.l&0xff))&0xffff]-1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.memory[(((cpu.h&0xff)<<8)|(cpu.l&0xff))&0xffff] = (short) (ans & 0xff);
				break;
			}
			
			case 0x36: { //MVI M,D8
				int mem = ((cpu.h&0xff) << 8) | (cpu.l&0xff);
				cpu.memory[mem] = cpu.memory[(cpu.pc+1)&0xffff];
				cpu.pc = ((cpu.pc + 1) & 0xffff);
				break;
			}
			
			case 0x37: { //STC
				cpu.cc.cy = 1;
				break;
			}
			
			
			case 0x39: { //DAD SP
				int HL = ((cpu.h << 8) | (cpu.l))&0xffff;
				int ans = HL + cpu.sp;
				cpu.h = (short)((ans >> 8)&0xff);
				cpu.l = (short) (ans&0xff);
				set_cc_carry_pair(ans,cpu);
				break;
			}

			case 0x3a: { //LDA addr
				int addr = (((cpu.memory[cpu.pc+2]&0xff)<<8) | (cpu.memory[cpu.pc+1])&0xff)&0xffff;
				cpu.a = cpu.memory[addr];
				cpu.pc = (cpu.pc + 2)&0xffff;
				break;
			}

			case 0x3b: { //DCX SP
				cpu.sp = (short) ((cpu.sp - 1) & 0xffff);
				break;
			}
			
			case 0x3c: { //INR A
				short ans = (short) (cpu.a + 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x3d: { //DCR A
				short ans = (short) (cpu.a -1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}

			case 0x3e: { //MVI A,D8
				cpu.a = (short) (cpu.memory[cpu.pc + 1]&0xff);
				break;
			}
			
			case 0x3f: { //CMC
				cpu.cc.cy = (short) (cpu.cc.cy == 0 ? 1:0);
				break;
			}

			case 0x56: { //MOV D,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.d = cpu.memory[addr];
				break;
			}

			case 0x5e: { //MOV E,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.d = cpu.memory[addr];
				break;
			}

			case 0x66: { //MOV H,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.h = cpu.memory[addr];
				break;
			}

			case 0x6f: { //MOV L,A
				cpu.l = cpu.a;
				break;
			}
			
			case 0x76: { //HLT
				System.exit(0);
				break;
			}

			case 0x77: { //MOV M,A
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.a;
				break;
			}
			
			case 0x7a: { //MOV A,D
				cpu.a = cpu.d;
				break;
			}
			case 0x7b: { //MOV A,E
				cpu.a = cpu.e;
				break;
			}
			case 0x7c: { //MOV A,H
				cpu.a = cpu.h;
				break;
			}
			case 0x7e: { //MOV A,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.a = cpu.memory[addr];
				break;
			}
			
			
			case 0x80: { //ADD B
				short ans = (short) (cpu.a + cpu.b);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x81: { //ADD C
				short ans = (short) (cpu.a + cpu.c);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x82: { //ADD D
				short ans = (short) (cpu.a + cpu.d);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x83: { //ADD E
				short ans = (short) (cpu.a + cpu.e);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x84: { //ADD H
				short ans = (short) (cpu.a + cpu.h);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x85: { //ADD L
				short ans = (short) (cpu.a + cpu.l);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x86: { //ADD M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short) (cpu.a + cpu.memory[(offset)]);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x87: { //ADD A
				short ans = (short) (cpu.a + cpu.a);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}
			
			case 0x88: { //ADC B
				short ans = (short) (cpu.a + cpu.b + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			case 0x89: { //ADC C
				short ans = (short) (cpu.a + cpu.c + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			
			case 0x8a: { //ADC D
				short ans = (short) (cpu.a + cpu.d + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			
			case 0x8b: { //ADC E
				short ans = (short) (cpu.a + cpu.e + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			case 0x8c: { //ADC h
				short ans = (short) (cpu.a + cpu.h + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			case 0x8d: { //ADC L
				short ans = (short) (cpu.a + cpu.l + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			case 0x8e: { //ADC M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short) (cpu.a + cpu.memory[(offset)] + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}
			
			case 0x8f: { //ADC A
				short ans = (short) (cpu.a + cpu.a + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
			}

			case 0xa0: { //ANA B
				cpu.a = (short) ((cpu.a&cpu.b)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa1: { //ANA C
				cpu.a = (short) ((cpu.a&cpu.c)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa2: { //ANA D
				cpu.a = (short) ((cpu.a&cpu.d)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa3: { //ANA E
				cpu.a = (short) ((cpu.a&cpu.e)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa4: { //ANA H
				cpu.a = (short) ((cpu.a&cpu.h)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa5: { //ANA L
				cpu.a = (short) ((cpu.a&cpu.l)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa6: { //ANA M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.a = (short) ((cpu.a&cpu.memory[offset&0xffff])&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa7: { //ANA A
				cpu.a = (short) ((cpu.a&cpu.a)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xa8: { //XRA B
				cpu.a = (short) ((cpu.a^cpu.b)&0xff);
				cpu.cc.cy = 0;
				break;
			}
			
			case 0xa9: { //XRA C
				cpu.a = (short) ((cpu.a^cpu.c)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xaa: { //XRA D
				cpu.a = (short) ((cpu.a^cpu.d)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xab: { //XRA E
				cpu.a = (short) ((cpu.a^cpu.e)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xac: { //XRA H
				cpu.a = (short) ((cpu.a^cpu.h)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xad: { //XRA L
				cpu.a = (short) ((cpu.a^cpu.l)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xae: { //XRA M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.a = (short) ((cpu.a^cpu.memory[offset&0xffff])&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xaf: { //XRA A
				cpu.a = (short) ((cpu.a^cpu.a)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb0: { //ORA B
				cpu.a = (short) ((cpu.a|cpu.b)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb1: { //ORA C
				cpu.a = (short) ((cpu.a|cpu.c)&0xff);
				cpu.cc.cy = 0;
				break;
			}


			case 0xb2: { //ORA D
				cpu.a = (short) ((cpu.a|cpu.d)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb3: { //ORA E
				cpu.a = (short) ((cpu.a|cpu.e)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb4: { //ORA H
				cpu.a = (short) ((cpu.a|cpu.h)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb5: { //ORA L
				cpu.a = (short) ((cpu.a|cpu.l)&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb6: { //ORA M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.a = (short) ((cpu.a|cpu.memory[offset&0xffff])&0xff);
				cpu.cc.cy = 0;
				break;
			}

			case 0xb7: { //ORA A
				cpu.a = (short) ((cpu.a|cpu.a)&0xff);
				cpu.cc.cy = 0;
				break;
			}
			
			case 0xc1: { //POP B
				cpu.c = cpu.memory[cpu.sp];
				cpu.b = cpu.memory[(cpu.sp+1)&0xffff];
				cpu.sp = (cpu.sp + 2)&0xffff;
				break;
			}

			case 0xc2: { //JNZ addr
				if (cpu.cc.z == 0) {
					jump_to_addr(cpu);
				} else {
					cpu.pc = (cpu.pc + 2)&0xffff;
				}
				break;
			}
			
			case 0xc3: { //JMP addr
				jump_to_addr(cpu);
				break;
			}
			
			case 0xc5: { //PUSH B
				cpu.memory[(cpu.sp-1)&0xffff] = (short) (cpu.b&0xff);
				cpu.memory[(cpu.sp-2)&0xffff] = (short) (cpu.c&0xff);
				cpu.sp = (cpu.sp-2)&0xffff;
				break;
			}
			
			case 0xc6: { //ADI Byte
				short ans = (short) (cpu.a + cpu.memory[(cpu.pc + 1) & 0xffff]);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				cpu.pc = (short) ((cpu.pc + 1)&0xffff);
				break;
			}
			
			case 0xc9: { //RET
				ret(cpu);
				break;
			}
			
			case 0xcd: { //CALL addr
				call(cpu);
				break;
			}
			
			case 0xce: { //ACI D8
				short data = (short)cpu.memory[(cpu.pc+1)&0xffff];
				short ans = (short) (cpu.a + data + cpu.cc.cy);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				cpu.pc = (short) ((cpu.pc + 1)&0xffff);
				break;
			}
			
			case 0xd1: { //POP D
				cpu.e = cpu.memory[cpu.sp];
				cpu.d = cpu.memory[(cpu.sp+1)&0xffff];
				cpu.sp = (cpu.sp + 2)&0xffff;
				break;
			}
			
			case 0xd5: { //PUSH D
				cpu.memory[(cpu.sp-1)&0xffff] = (short) (cpu.d&0xff);
				cpu.memory[(cpu.sp-2)&0xffff] = (short) (cpu.e&0xff);
				cpu.sp = (cpu.sp-2)&0xffff;
				break;
			}
			
			case 0xe1: { //POP H
				cpu.l = cpu.memory[cpu.sp];
				cpu.h = cpu.memory[(cpu.sp+1)&0xffff];
				cpu.sp = (cpu.sp + 2)&0xffff;
				break;
			}
			
			case 0xe5: { //PUSH H
				cpu.memory[(cpu.sp-1)&0xffff] = (short) (cpu.h&0xff);
				cpu.memory[(cpu.sp-2)&0xffff] = (short) (cpu.l&0xff);
				cpu.sp = (cpu.sp-2)&0xffff;
				break;
			}
			
			case 0xe6: { //ANI Byte
				short ans = (short) ((cpu.a &0xff)& cpu.memory[(cpu.pc+1)&0xffff]);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.cc.cy = 0;
				cpu.a = ans;
				cpu.pc = (cpu.pc + 1)&0xffff;
				break;
			}
			
			case 0xf1: { //POP PSW
				cpu.a = cpu.memory[(cpu.sp+1)&0xffff];
				short psw = (short) (cpu.memory[cpu.sp&0xffff]&0xff);
				cpu.cc.z = (short) ((0x01 == (psw & 0x01)) ? 1:0);
				cpu.cc.s = (short) ((0x02 == (psw & 0x02)) ? 1:0);
				cpu.cc.p = (short) ((0x04 == (psw & 0x04)) ? 1:0);
				cpu.cc.cy = (short) ((0x05 == (psw & 0x05)) ? 1:0);
				cpu.cc.ac = (short) ((0x10 == (psw & 0x10)) ? 1:0);
				cpu.sp = (cpu.sp +2)&0xffff;
				break;
			}
			
			
			default: System.out.println("UNIMPLEMENTED OPCODE: "+"0x"+String.format("%02x", opcode)); System.exit(1); break;
		}
		cpu.pc = (cpu.pc+1)&0xffff;
	}
	
	void set_cc_zero(short ans, CPU cpu ) {
		cpu.cc.z = (short) (((ans & 0xff) == 0) ? 1 : 0);
	}
	void set_cc_sign(short ans, CPU cpu) {
		cpu.cc.s = (short) (((ans & 0x80) != 0) ? 1 : 0);
	}
	void set_cc_carry(short ans, CPU cpu) {
		cpu.cc.cy = (short) ((ans > 0xff) ? 1:0);
	}
	void set_cc_carry_pair(int ans, CPU cpu) {
		cpu.cc.cy = (short) ((ans > 0xffff) ? 1:0);
	}
	void set_cc_parity(short ans, CPU cpu) {
		String byt = Integer.toBinaryString(ans&0xff);
		int count = 0;
		for(int i = 0; i<byt.length(); i++) {
		    char c = byt.charAt(i);
		    if (c == '1') {
		        count +=1;
		    }
		}
		
		cpu.cc.p = (short) ((count % 2) == 0 ? 1 : 0);
	}
	
	void jump_to_addr(CPU cpu) {
		cpu.pc = (int) (((cpu.memory[(cpu.pc + 2) & 0xffff] & 0xff) << 8) | (cpu.memory[(cpu.pc + 1) & 0xffff] & 0xff));
		cpu.pc = (cpu.pc-1)&0xffff; //as pc gets incremented by 1 in the emulation block
	}
	
	void ret(CPU cpu) {
		cpu.pc = (cpu.memory[cpu.sp&0xffff]&0xff) | ((cpu.memory[(cpu.sp+1)&0xffff] << 8)&0xff);
		cpu.sp = (cpu.sp + 2)&0xffff;
	}
	
	void call(CPU cpu) {
		int ret = (cpu.pc +2)&0xffff;
		cpu.memory[(cpu.sp - 1) & 0xffff] = (short) ((ret >> 8) & 0xff);
		cpu.memory[(cpu.sp - 2) & 0xffff] = (short) (ret & 0xff);
		cpu.sp = (cpu.sp-2)&0xffff;
		jump_to_addr(cpu);
	}

}
