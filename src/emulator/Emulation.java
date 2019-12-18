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
	
	short cycles8080[] = {
			4, 10, 7, 5, 5, 5, 7, 4, 4, 10, 7, 5, 5, 5, 7, 4, //0x00..0x0f
			4, 10, 7, 5, 5, 5, 7, 4, 4, 10, 7, 5, 5, 5, 7, 4, //0x10..0x1f
			4, 10, 16, 5, 5, 5, 7, 4, 4, 10, 16, 5, 5, 5, 7, 4, //etc
			4, 10, 13, 5, 10, 10, 10, 4, 4, 10, 13, 5, 5, 5, 7, 4,
			
			5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5, //0x40..0x4f
			5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
			5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
			7, 7, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 7, 5,
			
			4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, //0x80..8x4f
			4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
			4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
			4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
			
			11, 10, 10, 10, 17, 11, 7, 11, 11, 10, 10, 10, 10, 17, 7, 11, //0xc0..0xcf
			11, 10, 10, 10, 17, 11, 7, 11, 11, 10, 10, 10, 10, 17, 7, 11, 
			11, 10, 10, 18, 17, 11, 7, 11, 11, 5, 10, 5, 17, 17, 7, 11, 
			11, 10, 10, 4, 17, 11, 7, 11, 11, 5, 10, 4, 17, 17, 7, 11
		};
	
	
	
	void GenerateInterrupt(CPU cpu, int interrupt_num) {
		System.out.println("Pushing to Stack(interrupt): 0x"+String.format("%x", cpu.pc));
		cpu.memory[(cpu.sp - 1) & 0xffff] = (short) (((cpu.pc-1) >> 8) & 0xff);
		cpu.memory[(cpu.sp - 2) & 0xffff] = (short) ((cpu.pc-1) & 0xff);
		cpu.sp = (cpu.sp-2)&0xffff;
		
		cpu.pc = 8*interrupt_num;
		cpu.interrupt_enable = false;
	}
	
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
	
	
	
	int Emulate8080(CPU cpu) {
		short opcode = cpu.memory[cpu.pc];
		System.out.println(String.format("%04x", cpu.pc)+":	0x"+String.format("%02x", opcode));
		System.out.println("");
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
				cpu.c = (short) ((cpu.c + 1) & 0xff);
				if(cpu.c == 0) {
					cpu.b = (short) ((cpu.b + 1) & 0xff);
				}
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
			
			case 0x07: { //RLC
				short x = (short) (cpu.a&0xff);
				cpu.a = (short)(((x & 0x80) >> 7) | (x << 1));
				cpu.cc.cy = (short) ((0x80 == (x&0x80)) ? 1:0);
				break;
			}
			
			case 0x08: { break; } //NOP
			
			case 0x09: { //DAD B
				int BC = ((cpu.b << 8) | (cpu.c))&0xffff;
				int HL = ((cpu.h << 8) | (cpu.l))&0xffff;
				int ans = HL + BC;
				cpu.h = (short)((ans >> 8)&0xff);
				cpu.l = (short) (ans&0xff);
				set_cc_carry_pair(ans,cpu);
				break;
			}
			
			case 0x0a: { //LDAX B
				cpu.a = cpu.memory[((cpu.b << 8) | (cpu.c))&0xffff];
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
			
			case 0x10: { break; } //NOP
			
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
				cpu.e = (short) ((cpu.e+1) & 0xff);
				if (cpu.e == 0) {
					cpu.d = (short) ((cpu.d+1) & 0xff);
				}
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
			
			case 0x16: { //MVI D,Byte
				cpu.d = cpu.memory[(cpu.pc+1)&0xffff];
				cpu.pc = (cpu.pc+1)&0xffff;
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
				System.out.println(cpu.memory[((cpu.d << 8) | (cpu.e))&0xffff]);
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
			
			case 0x20: { break; } //NOP
			
			case 0x21: { //LXI H
				cpu.l = cpu.memory[((cpu.pc+1) & 0xffff)];
				cpu.h = cpu.memory[((cpu.pc+2) & 0xffff)];
				cpu.pc = ((cpu.pc + 2) & 0xffff);
				break;
			}
			
			case 0x23: { //INX H
				
				cpu.l = (short)((cpu.l+1)&0xff);
				if (cpu.l == 0) {
					cpu.h = (short)((cpu.h+1)&0xff);
				}
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

			case 0x27: { //DAA
				if ((cpu.a &0xf) > 9) {
					cpu.a = (short)((cpu.a +6)&0xff);
				}
				if ((cpu.a&0xf0) > 0x90) {
					short res = (short)((cpu.a + 0x60)&0xff);
					cpu.a = res;
					set_cc_zero(res,cpu);
					set_cc_sign(res,cpu);
					set_cc_parity(res,cpu);
				}
				break;
			}
			
			case 0x29: { //DAD H
				int HL = ((cpu.h << 8) | (cpu.l))&0xffff;
				int ans = HL + HL;
				cpu.h = (short)((ans >> 8)&0xff);
				cpu.l = (short) (ans&0xff);
				set_cc_carry_pair(ans,cpu);
				break;
			}
			
			case 0x2a: { //LHLD addr
				int offset = ((cpu.memory[cpu.pc+2] << 8) | cpu.memory[cpu.pc+1])&0xffff;
				cpu.l = cpu.memory[offset];
				cpu.h = cpu.memory[(offset+1)&0xffff];
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
				short ans = (short) (cpu.a - 1);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = (short) (ans & 0xff);
				break;
			}

			case 0x3e: { //MVI A,D8
				cpu.a = (short) (cpu.memory[cpu.pc + 1]&0xff);
				cpu.pc = (cpu.pc+1)&0xffff;
				break;
			}
			
			case 0x3f: { //CMC
				cpu.cc.cy = (short) (cpu.cc.cy == 0 ? 1:0);
				break;
			}
			
			case 0x40: { //MOV B,B
				cpu.b = cpu.c;
				break;
			}
			
			case 0x41: { //MOV B,C
				cpu.b = cpu.c;
				break;
			}
			
			case 0x42: { //MOV B,D
				cpu.b = cpu.d;
				break;
			}
			
			case 0x43: { //MOV B,E
				cpu.b = cpu.e;
				break;
			}
			
			case 0x44: { //MOV B,H
				cpu.b = cpu.h;
				break;
			}
			
			case 0x45: { //MOV B,L
				cpu.b = cpu.l;
				break;
			}
			
			case 0x46: { //MOV B,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.b = cpu.memory[addr];
				break;
			}
			
			case 0x47: { //MOV B,A
				cpu.b = cpu.a;
				break;
			}
			
			case 0x48: { //MOV C,B
				cpu.c = cpu.b;
				break;
			}
			
			case 0x49: { //MOV C,C
				cpu.c = cpu.c;
				break;
			}
			
			case 0x4a: { //MOV C,D
				cpu.c = cpu.d;
				break;
			}
			
			case 0x4b: { //MOV C,E
				cpu.c = cpu.e;
				break;
			}
			
			case 0x4c: { //MOV C,H
				cpu.c = cpu.h;
				break;
			}
			
			case 0x4d: { //MOV C,L
				cpu.c = cpu.l;
				break;
			}
			
			case 0x4e: { //MOV C,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.c = cpu.memory[addr];
				break;
			}
			
			case 0x4f: { //MOV C,A
				cpu.c = cpu.a;
				break;
			}
			
			case 0x50: { //MOV D,B
				cpu.d = cpu.b;
				break;
			}

			case 0x56: { //MOV D,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.d = cpu.memory[addr];
				break;
			}
			
			case 0x57: { //MOV D,A
				cpu.d = cpu.a;
				break;
			}
			
			case 0x5e: { //MOV E,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.d = cpu.memory[addr];
				break;
			}
			
			case 0x5f: { //MOV E,A
				cpu.e = cpu.a;
				break;
			}
			
			case 0x60: { //MOV H,B
				cpu.h = cpu.b;
				break;
			}
			
			case 0x61: { //MOV H,C
				cpu.h = cpu.c;
				break;
			}
			
			case 0x62: { //MOV H,D
				cpu.h = cpu.d;
				break;
			}
			
			case 0x63: { //MOV H,E
				cpu.h = cpu.e;
				break;
			}
			
			case 0x64: { //MOV H,H
				cpu.h = cpu.h;
				break;
			}
			
			case 0x65: { //MOV H,L
				cpu.h = cpu.l;
				break;
			}

			case 0x66: { //MOV H,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.h = cpu.memory[addr];
				break;
			}
			
			case 0x67: { //MOV H,A
				cpu.h = cpu.a;
				break;
			}
			
			case 0x68: { //MOV L,B
				cpu.l = cpu.b;
				break;
			}
			
			case 0x69: { //MOV L,C
				cpu.l = cpu.c;
				break;
			}
			
			case 0x6a: { //MOV L,D
				cpu.l = cpu.d;
				break;
			}
			
			case 0x6b: { //MOV L,E
				cpu.l = cpu.e;
				break;
			}
			
			case 0x6c: { //MOV L,H
				cpu.l = cpu.h;
				break;
			}
			
			case 0x6d: { //MOV L,L
				cpu.l = cpu.l;
				break;
			}
			
			case 0x6e: { //MOV L,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.l = cpu.memory[addr];
				break;
			}

			case 0x6f: { //MOV L,A
				cpu.l = cpu.a;
				break;
			}
			
			case 0x70: { //MOV M,B
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.b;
				break;
			}
			
			case 0x71: { //MOV M,C
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.c;
				break;
			}
			
			case 0x72: { //MOV M,D
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.d;
				break;
			}
			
			case 0x73: { //MOV M,E
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.e;
				break;
			}
			
			case 0x74: { //MOV M,H
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.h;
				break;
			}
			
			case 0x75: { //MOV M,L
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.memory[addr] = cpu.l;
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
			
			case 0x78: { //MOV A,B
				cpu.a = cpu.b;
				break;
			}
			
			case 0x79: { //MOV A,C
				cpu.a = cpu.c;
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
			
			case 0x7d: { //MOV A,L
				cpu.a = cpu.l;
				break;
			}
			
			case 0x7e: { //MOV A,M
				int addr = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				cpu.a = cpu.memory[addr];
				break;
			}
			
			case 0x7f: { //MOV A,A
				cpu.a = cpu.a;
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
			
			case 0x90: { //SUB B
				short ans = (short)(cpu.a - cpu.b);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x91: { //SUB C
				short ans = (short)(cpu.a - cpu.c);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x92: { //SUB D
				short ans = (short)(cpu.a - cpu.d);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x93: { //SUB E
				short ans = (short)(cpu.a - cpu.e);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x94: { //SUB H
				short ans = (short)(cpu.a - cpu.h);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x95: { //SUB L
				short ans = (short)(cpu.a - cpu.l);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x96: { //SUB M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short)(cpu.a - cpu.memory[(offset)]);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0x97: { //SUB A
				short ans = (short)(cpu.a - cpu.a);
				cpu.a = (short)(ans&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa0: { //ANA B
				short ans = (short) ((cpu.a&cpu.b)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa1: { //ANA C
				short ans = (short) ((cpu.a&cpu.c)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa2: { //ANA D
				short ans = (short) ((cpu.a&cpu.d)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa3: { //ANA E
				short ans = (short) ((cpu.a&cpu.e)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa4: { //ANA H
				short ans = (short) ((cpu.a&cpu.h)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa5: { //ANA L
				short ans = (short) ((cpu.a&cpu.l)&0xff); 
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa6: { //ANA M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short) ((cpu.a&cpu.memory[offset&0xffff])&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa7: { //ANA A
				short ans = (short)((cpu.a&cpu.a)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xa8: { //XRA B
				short ans = (short) ((cpu.a^cpu.b)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0xa9: { //XRA C
				short ans = (short) ((cpu.a^cpu.c)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xaa: { //XRA D
				short ans = (short) ((cpu.a^cpu.d)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xab: { //XRA E
				short ans = (short) ((cpu.a^cpu.e)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xac: { //XRA H
				short ans = (short) ((cpu.a^cpu.h)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xad: { //XRA L
				short ans = (short) ((cpu.a^cpu.l)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xae: { //XRA M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short) ((cpu.a^cpu.memory[offset&0xffff])&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xaf: { //XRA A
				short ans = (short) ((cpu.a^cpu.a)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb0: { //ORA B
				short ans = (short) ((cpu.a|cpu.b)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb1: { //ORA C
				short ans = (short) ((cpu.a|cpu.c)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}


			case 0xb2: { //ORA D
				short ans = (short) ((cpu.a|cpu.d)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb3: { //ORA E
				short ans = (short) ((cpu.a|cpu.e)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb4: { //ORA H
				short ans = (short) ((cpu.a|cpu.h)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb5: { //ORA L
				short ans = (short) ((cpu.a|cpu.l)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb6: { //ORA M
				int offset = ((cpu.h << 8) | (cpu.l)) & 0xffff;
				short ans = (short) ((cpu.a|cpu.memory[offset&0xffff])&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}

			case 0xb7: { //ORA A
				short ans = (short) ((cpu.a|cpu.a)&0xff);
				cpu.a = ans;
				cpu.cc.cy = 0;
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				break;
			}
			
			case 0xc0: { //RNZ
				if (cpu.cc.z != 0) {
					ret(cpu);
				}
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
					cpu.pc = (cpu.pc+2)&0xffff;
				}
				break;
			}
			
			case 0xc3: { //JMP addr
				jump_to_addr(cpu);
				break;
			}
			
			case 0xc4: { //CNZ
				if(cpu.cc.z == 0) {
					call(cpu);
				} else {
					cpu.pc = (cpu.pc+2)&0xffff;
				}
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
			
			case 0xc8: {
				if(cpu.cc.z == 1) {
					ret(cpu);
				}
				break;
			}
			
			case 0xc9: { //RET
				ret(cpu);
				break;
			}
			
			case 0xca: { //JZ addr
				if (cpu.cc.z == 1) {
					jump_to_addr(cpu);
				} else {
					cpu.pc = (cpu.pc+2)&0xffff;
				}
				break;
			}
			
			case 0xcc: { //CZ addr
				if (cpu.cc.z == 1) {
					call(cpu);
				}
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
			
			case 0xd0: { //RNC
				if(cpu.cc.cy == 0) {
					ret(cpu);
				}
				break;
			}
			
			case 0xd1: { //POP D
				cpu.e = cpu.memory[cpu.sp];
				cpu.d = cpu.memory[(cpu.sp+1)&0xffff];
				cpu.sp = (cpu.sp + 2)&0xffff;
				break;
			}
			
			case 0xd2: {
				if(cpu.cc.cy == 0) {
					jump_to_addr(cpu);
				} else {
					cpu.pc = (cpu.pc+2)&0xffff;
				}
				break;
			}
			
			case 0xd3: { //OUT d8
				//TODO: come back here later
				cpu.pc = (cpu.pc+1)&0xffff;
				break;
			}
			
			case 0xd5: { //PUSH D
				cpu.memory[(cpu.sp-1)&0xffff] = (short) (cpu.d&0xff);
				cpu.memory[(cpu.sp-2)&0xffff] = (short) (cpu.e&0xff);
				cpu.sp = (cpu.sp-2)&0xffff;
				break;
			}
			
			case 0xd6: { //SUI D8
				short ans = (short)(cpu.a - (cpu.memory[(cpu.pc+1)&0xffff]&0xff));
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_carry(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.a = ans;
				break;
			}
			
			case 0xd8: { //RC
				if(cpu.cc.cy != 0) {
					ret(cpu);
				}
				break;
			}
			
			case 0xda: { //JC adr
				if (cpu.cc.cy ==1) {
					jump_to_addr(cpu);
				} else {
					cpu.pc = (cpu.pc+2)&0xffff;
				}
				break;
			}
			
			
			case 0xdb: { //IN d8
				cpu.a = in(cpu, cpu.memory[(cpu.pc+1)&0xffff]);
				cpu.pc = (cpu.pc+1)&0xffff;
				break;
			}
			
			case 0xe1: { //POP H
				cpu.l = cpu.memory[cpu.sp];
				cpu.h = cpu.memory[(cpu.sp+1)&0xffff];
				cpu.sp = (cpu.sp + 2)&0xffff;
				break;
			}
			
			case 0xe3: { //XTHL
				short h = cpu.h;
				short l = cpu.l;
				cpu.l = (short)(cpu.memory[cpu.sp&0xffff]&0xff);
				cpu.h = (short)(cpu.memory[(cpu.sp+1)&0xffff]&0xff);
				cpu.memory[cpu.sp&0xffff] = (short)(l&0xff);
				cpu.memory[(cpu.sp+1)&0xffff] = (short)(h&0xff);
				break;
			}
			
			case 0xe4: { //CP0 addr
				if(cpu.cc.p == 0) {
					call(cpu);
				} else {
					cpu.pc = (cpu.pc + 2)&0xffff;
				}
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
			
			case 0xe9: { //PCHL
				cpu.pc = (((cpu.h&0xff) << 8) | (cpu.l))&0xffff;
				break;
			}
			
			case 0xeb: { //XCHG
				short temp1 = cpu.d;
				short temp2 = cpu.e;
				cpu.d = cpu.h;
				cpu.e = cpu.l;
				cpu.h = temp1;
				cpu.l = temp2;
				break;
			}
			
			case 0xef: { //RST 5
				cpu.memory[(cpu.sp - 1) & 0xffff] = (short) ((cpu.pc >> 8) & 0xff);
				cpu.memory[(cpu.sp - 2) & 0xffff] = (short) (cpu.pc & 0xff);
				cpu.sp = (cpu.sp-2)&0xffff;
				cpu.pc = 0x20;
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
			
			case 0xf3: { //DI
				cpu.interrupt_enable = false;
				break;
			}
			
			case 0xf5: { //PUSH PSW
				cpu.memory[cpu.sp-1] = cpu.a;
				short psw = (short) ((cpu.cc.z | 
						cpu.cc.s << 1 | 
						cpu.cc.p << 2 | 
						cpu.cc.cy << 3 | 
						cpu.cc.ac << 4)&0xff);
				cpu.memory[cpu.sp-2] = psw;
				cpu.sp = (cpu.sp - 2)&0xffff;
				break;
			}
			
			case 0xf6: { //ORI D8
				short ans = (short)((cpu.a | cpu.memory[(cpu.pc+1)&0xffff])&0xff);
				set_cc_zero(ans,cpu);
				set_cc_sign(ans,cpu);
				set_cc_parity(ans,cpu);
				cpu.cc.cy = 0;
				cpu.a = ans;
				cpu.pc = (cpu.pc+1)&0xffff;
				break;
			}
			
			
			case 0xfb: { //EI
				cpu.interrupt_enable = true;
				break;
			}
			
			case 0xfe: { //CPI d8
				short x = (short)((cpu.a - cpu.memory[(cpu.pc +1)&0xffff])&0xff);
				cpu.cc.z = (short)((x==0)?1:0);
				cpu.cc.s = (short)((0x80 == (x & 0x80))?1:0);
				set_cc_parity(x,cpu);
				cpu.cc.cy = (short)((cpu.a < cpu.memory[cpu.pc+1])?1:0);
				cpu.pc = (cpu.pc+1)&0xffff;
				break;
			}
			
			default: System.out.println("UNIMPLEMENTED OPCODE: "+"0x"+String.format("%02x", opcode)); System.exit(1); break;
		}
		cpu.pc = (cpu.pc+1)&0xffff;
		return cycles8080[opcode];
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
		cpu.pc = ((cpu.memory[(cpu.sp+1)&0xffff]&0xff) << 8) | (cpu.memory[cpu.sp&0xffff]&0xff);
		System.out.println("Returning to: 0x"+String.format("%x", cpu.pc+1));
		cpu.sp = (cpu.sp + 2)&0xffff;
	}
	
	void call(CPU cpu) {
		int ret = (cpu.pc +2)&0xffff;
		System.out.println("Pushing to Stack(call): 0x"+String.format("%x", ret));
		cpu.memory[(cpu.sp - 1) & 0xffff] = (short) ((ret >> 8) & 0xff);
		cpu.memory[(cpu.sp - 2) & 0xffff] = (short) (ret & 0xff);
		cpu.sp = (cpu.sp-2)&0xffff;
		jump_to_addr(cpu);
	}
	
	short in(CPU cpu, short port) {
		short a = 0;
		switch(port) {
			case 0: {
				return 0xf;
			}
			case 1: {
				return Main.in_port1;
			}
			case 2: {
				return 0x0;
			}
			default:
				return a;
			
		}
	}

}
