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
		case 0x01: System.out.println(String.format("%04d", pc)+"	LXI	B,0x"+String.format("%x", dat[pc+2])+" 0x"+String.format("%x", dat[pc+1])); opbytes = 3; break;
		case 0x02: System.out.println(String.format("%04d", pc)+"	STAX	B"); break;
		case 0x03: System.out.println(String.format("%04d", pc)+"	INX	B"); break;
		case 0x04: System.out.println(String.format("%04d", pc)+"	INR	B"); break;
		case 0x05: System.out.println(String.format("%04d", pc)+"	DCR	B"); break;
		case 0x06: System.out.println(String.format("%04d", pc)+"	MVI	B,$"+String.format("%x", dat[pc+1]));opbytes=1; break;
		case 0x07: System.out.println(String.format("%04d", pc)+"	RLC"); break;
		case 0x08: System.out.println(String.format("%04d", pc)+"	NOP"); break;
		
		case 0x3e: System.out.println(String.format("%04d", pc)+"	MVI	A,#$"+String.format("%x", dat[pc+1]));opbytes=2; break;
		
		case 0xc3: System.out.println(String.format("%04d", pc)+"	JMP	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		}
		return opbytes;
	}

}
