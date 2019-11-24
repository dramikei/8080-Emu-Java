package disassembler;

import java.io.FileInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException {
		File file = new File(System.getProperty("user.dir") + "/src/disassembler/" + "invaders.h");
		DataInputStream data = new DataInputStream(new FileInputStream(file));
		int[] dat = new int[(int) file.length()];
		for(int i=0;i<(int) file.length();i++) {
			dat[i]=(int) data.read();			
		}
		data.close();
		int pc = 0;
		int fileSize = (int) file.length();
		while(pc < fileSize) {
			if(dat != null) {
				pc += Disassemble808O(dat,pc);
			}
		}
		
		
		
	}
	static /* codeBuffer the Buffer where the 8080 rom is loaded. 
	 * "pc" is the program counter */
	int Disassemble808O(int[] dat, int pc) {
		int code = dat[pc];
		int opbytes = 1;
//		System.out.println("Opcode: 0x"+String.format("%x", code));
		switch(code) {
		case 0x00: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		case 0x01: System.out.println(String.format("%04d", pc)+"	LXI	B,#$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes = 3; break;
		case 0x02: System.out.println(String.format("%04d", pc)+"	STAX	B"); break;
		case 0x03: System.out.println(String.format("%04d", pc)+"	INX	B"); break;
		case 0x04: System.out.println(String.format("%04d", pc)+"	INR	B"); break;
		case 0x05: System.out.println(String.format("%04d", pc)+"	DCR	B"); break;
		case 0x06: System.out.println(String.format("%04d", pc)+"	MVI	B,$"+String.format("%x", dat[pc+1]));opbytes=1; break;
		case 0x07: System.out.println(String.format("%04d", pc)+"	RLC"); break;
		case 0x08: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		case 0x09: System.out.println(String.format("%04d", pc)+"	DAD	B"); break;
		case 0x0a: System.out.println(String.format("%04d", pc)+"	LDAX	B"); break;
		case 0x0b: System.out.println(String.format("%04d", pc)+"	DCX	B"); break;
		case 0x0c: System.out.println(String.format("%04d", pc)+"	INC	C"); break;
		case 0x0d: System.out.println(String.format("%04d", pc)+"	DCR	C"); break;
		case 0x0e: System.out.println(String.format("%04d", pc)+"	MVI	C,"+String.format("%x", dat[pc+1]));opbytes=2; break;
		case 0x0f: System.out.println(String.format("%04d", pc)+"	RRC"); break;
		case 0x10: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		case 0x11: System.out.println(String.format("%04d", pc)+"	LXI	D,#$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes=3; break;
		case 0x12: System.out.println(String.format("%04d", pc)+"	STAX	D"); break;
		case 0x13: System.out.println(String.format("%04d", pc)+"	INX	D"); break;
		case 0x14: System.out.println(String.format("%04d", pc)+"	INR	D"); break;
		case 0x15: System.out.println(String.format("%04d", pc)+"	DCR	D"); break;
		case 0x16: System.out.println(String.format("%04d", pc)+"	MVI	D,$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0x17: System.out.println(String.format("%04d", pc)+"	RAL"); break;
		case 0x18: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		case 0x19: System.out.println(String.format("%04d", pc)+"	DAD	D"); break;
		case 0x1a: System.out.println(String.format("%04d", pc)+"	LDAX	D"); break;
		case 0x1b: System.out.println(String.format("%04d", pc)+"	DCX	D"); break;
		case 0x1c: System.out.println(String.format("%04d", pc)+"	INR	E"); break;
		case 0x1d: System.out.println(String.format("%04d", pc)+"	DCR	E"); break;
		case 0x1e: System.out.println(String.format("%04d", pc)+"	MVI	E,$"+String.format("%x", dat[pc+1]));opbytes=2; break;
		case 0x1f: System.out.println(String.format("%04d", pc)+"	RAR"); break;
		case 0x20: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		case 0x21: System.out.println(String.format("%04d", pc)+"	LXI	H,#$"+String.format("%x", dat[pc+2]) + "" + String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0x22: System.out.println(String.format("%04d", pc)+"	SHLD,#$"+String.format("%x", dat[pc+1])+""+String.format("%x", dat[pc+2]));opbytes=3; break;
		case 0x23: System.out.println(String.format("%04d", pc)+"	INX	H"); break;
		case 0x24: System.out.println(String.format("%04d", pc)+"	INR	H"); break;
		case 0x25: System.out.println(String.format("%04d", pc)+"	DCR	H"); break;
		case 0x26: System.out.println(String.format("%04d", pc)+"	MVI	H,$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0x27: System.out.println(String.format("%04d", pc)+"	DAA"); break;
		case 0x28: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		case 0x29: System.out.println(String.format("%04d", pc)+"	DAD	H"); break;
		case 0x2a: System.out.println(String.format("%04d", pc)+"	LHLD,#$"+String.format("%x", dat[pc+1])+""+String.format("%x", dat[pc+2]));opbytes=3; break;
		case 0x2b: System.out.println(String.format("%04d", pc)+"	DCX	H"); break;
		case 0x2c: System.out.println(String.format("%04d", pc)+"	INR	L"); break;
		case 0x2d: System.out.println(String.format("%04d", pc)+"	DCR	L"); break;
		case 0x2e: System.out.println(String.format("%04d", pc)+"	MVI L,$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0x2f: System.out.println(String.format("%04d", pc)+"	CMA"); break;
		
		case 0x3e: System.out.println(String.format("%04d", pc)+"	MVI	A,#$"+String.format("%x", dat[pc+1]));opbytes=2; break;
		
		case 0xc3: System.out.println(String.format("%04d", pc)+"	JMP	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		}
		return opbytes;
	}

}
