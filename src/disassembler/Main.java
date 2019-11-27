package disassembler;

import java.io.FileInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException {
		File file = new File(System.getProperty("user.dir") + "/src/Games/" + "invaders");
		DataInputStream data = new DataInputStream(new FileInputStream(file));
		int fileSize = (int) file.length();
		int[] dat = new int[fileSize];
		for(int i=0;i<fileSize;i++) {
			dat[i]=(int) data.read();
		}
		data.close();
		int pc = 0;
		while(pc < fileSize) {
			if(dat != null) {
				pc += Disassemble808O(dat,pc);
			}
		}
		
		
		
	} 

	
	/*
	 *
	 * 		If You're wondering what "#$" and "$" mean then read the following:
	 * 		In most assembly languages, # is an indicator of a literal number, also called an immediate value. 
	 * 		For example in "lea   a1, #$1000", the code will actually put a #0x1000 into register a1. 
	 * 		If the code instead read "move.l a1, ($1000)", I would expect a1 to get the contents of memory at location 0x1000
	 *  
	 * 
	 */
	
	
	
	/* 
	 * 		In the below function:
	 * 		"dat" is the Buffer array where the 8080 ROM is loaded. 
	 * 		"pc" is the program counter 
	 */
	
	static int Disassemble808O(int[] dat, int pc) {
		int code = dat[pc];
		int opbytes = 1;
		System.out.println("OPCODE: 0x"+String.format("%02x", code));
		switch(code) {
		case 0x00: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x01: System.out.println(String.format("%04x", pc)+"	LXI	B,#$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes = 3; break;
		case 0x02: System.out.println(String.format("%04x", pc)+"	STAX	B"); break;
		case 0x03: System.out.println(String.format("%04x", pc)+"	INX	B"); break;
		case 0x04: System.out.println(String.format("%04x", pc)+"	INR	B"); break;
		case 0x05: System.out.println(String.format("%04x", pc)+"	DCR	B"); break;
		case 0x06: System.out.println(String.format("%04x", pc)+"	MVI	B,$"+String.format("%x", dat[pc+1]));opbytes=1; break;
		case 0x07: System.out.println(String.format("%04x", pc)+"	RLC"); break;
		case 0x08: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x09: System.out.println(String.format("%04x", pc)+"	DAD	B"); break;
		case 0x0a: System.out.println(String.format("%04x", pc)+"	LDAX	B"); break;
		case 0x0b: System.out.println(String.format("%04x", pc)+"	DCX	B"); break;
		case 0x0c: System.out.println(String.format("%04x", pc)+"	INC	C"); break;
		case 0x0d: System.out.println(String.format("%04x", pc)+"	DCR	C"); break;
		case 0x0e: System.out.println(String.format("%04x", pc)+"	MVI	C,"+String.format("%x", dat[pc+1]));opbytes=2; break;
		case 0x0f: System.out.println(String.format("%04x", pc)+"	RRC"); break;
		case 0x10: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x11: System.out.println(String.format("%04x", pc)+"	LXI	D,#$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes=3; break;
		case 0x12: System.out.println(String.format("%04x", pc)+"	STAX	D"); break;
		case 0x13: System.out.println(String.format("%04x", pc)+"	INX	D"); break;
		case 0x14: System.out.println(String.format("%04x", pc)+"	INR	D"); break;
		case 0x15: System.out.println(String.format("%04x", pc)+"	DCR	D"); break;
		case 0x16: System.out.println(String.format("%04x", pc)+"	MVI	D,$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0x17: System.out.println(String.format("%04x", pc)+"	RAL"); break;
		case 0x18: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x19: System.out.println(String.format("%04x", pc)+"	DAD	D"); break;
		case 0x1a: System.out.println(String.format("%04x", pc)+"	LDAX	D"); break;
		case 0x1b: System.out.println(String.format("%04x", pc)+"	DCX	D"); break;
		case 0x1c: System.out.println(String.format("%04x", pc)+"	INR	E"); break;
		case 0x1d: System.out.println(String.format("%04x", pc)+"	DCR	E"); break;
		case 0x1e: System.out.println(String.format("%04x", pc)+"	MVI	E,$"+String.format("%x", dat[pc+1]));opbytes=2; break;
		case 0x1f: System.out.println(String.format("%04x", pc)+"	RAR"); break;
		case 0x20: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x21: System.out.println(String.format("%04x", pc)+"	LXI	H,#$"+String.format("%x", dat[pc+2]) + "" + String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0x22: System.out.println(String.format("%04x", pc)+"	SHLD,	$"+String.format("%x", dat[pc+1])+""+String.format("%x", dat[pc+2]));opbytes=3; break;
		case 0x23: System.out.println(String.format("%04x", pc)+"	INX	H"); break;
		case 0x24: System.out.println(String.format("%04x", pc)+"	INR	H"); break;
		case 0x25: System.out.println(String.format("%04x", pc)+"	DCR	H"); break;
		case 0x26: System.out.println(String.format("%04x", pc)+"	MVI	H,$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0x27: System.out.println(String.format("%04x", pc)+"	DAA"); break;
		case 0x28: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x29: System.out.println(String.format("%04x", pc)+"	DAD	H"); break;
		case 0x2a: System.out.println(String.format("%04x", pc)+"	LHLD,	$"+String.format("%x", dat[pc+1])+""+String.format("%x", dat[pc+2]));opbytes=3; break;
		case 0x2b: System.out.println(String.format("%04x", pc)+"	DCX	H"); break;
		case 0x2c: System.out.println(String.format("%04x", pc)+"	INR	L"); break;
		case 0x2d: System.out.println(String.format("%04x", pc)+"	DCR	L"); break;
		case 0x2e: System.out.println(String.format("%04x", pc)+"	MVI	L,$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0x2f: System.out.println(String.format("%04x", pc)+"	CMA"); break;
		case 0x30: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x31: System.out.println(String.format("%04x", pc)+"	LXI	SP,#$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes=3; break;
		case 0x32: System.out.println(String.format("%04x", pc)+"	STA	$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes=3; break;
		case 0x33: System.out.println(String.format("%04x", pc)+"	INX	SP"); break;
		case 0x34: System.out.println(String.format("%04x", pc)+"	INR	M"); break;
		case 0x35: System.out.println(String.format("%04x", pc)+"	DCR	M"); break;
		case 0x36: System.out.println(String.format("%04x", pc)+"	MVI	M,#$"+String.format("%x",dat[pc+1]));opbytes=2; break;
		case 0x37: System.out.println(String.format("%04x", pc)+"	STC"); break;
		case 0x38: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0x39: System.out.println(String.format("%04x", pc)+"	DAD	SP"); break;
		case 0x3a: System.out.println(String.format("%04x", pc)+"	LDA	$"+String.format("%x", dat[pc+2])+""+String.format("%x", dat[pc+1])); opbytes=3; break;
		case 0x3b: System.out.println(String.format("%04x", pc)+"	DCX	SP"); break;
		case 0x3c: System.out.println(String.format("%04x", pc)+"	INR	A"); break;
		case 0x3d: System.out.println(String.format("%04x", pc)+"	DCR	A"); break;
		case 0x3e: System.out.println(String.format("%04x", pc)+"	MVI	A,#$"+String.format("%x", dat[pc+1]));opbytes=2; break;
		case 0x3f: System.out.println(String.format("%04x", pc)+"	CMC"); break;
		case 0x40: System.out.println(String.format("%04x", pc)+"	MOV	B,B"); break;
		case 0x41: System.out.println(String.format("%04x", pc)+"	MOV	B,C"); break; 
		case 0x42: System.out.println(String.format("%04x", pc)+"	MOV	B,D"); break;
		case 0x43: System.out.println(String.format("%04x", pc)+"	MOV	B,E"); break;
		case 0x44: System.out.println(String.format("%04x", pc)+"	MOV	B,H"); break;
		case 0x45: System.out.println(String.format("%04x", pc)+"	MOV	B,L"); break;
		case 0x46: System.out.println(String.format("%04x", pc)+"	MOV	B,M"); break;
		case 0x47: System.out.println(String.format("%04x", pc)+"	MOV	B,A"); break;
		case 0x48: System.out.println(String.format("%04x", pc)+"	MOV	C,B"); break;
		case 0x49: System.out.println(String.format("%04x", pc)+"	MOV	C,C"); break;
		case 0x4a: System.out.println(String.format("%04x", pc)+"	MOV	C,D"); break;
		case 0x4b: System.out.println(String.format("%04x", pc)+"	MOV	C,E"); break;
		case 0x4c: System.out.println(String.format("%04x", pc)+"	MOV	C,H"); break;
		case 0x4d: System.out.println(String.format("%04x", pc)+"	MOV	C,L"); break;
		case 0x4e: System.out.println(String.format("%04x", pc)+"	MOV	C,M"); break;
		case 0x4f: System.out.println(String.format("%04x", pc)+"	MOV	C,A"); break;
		case 0x50: System.out.println(String.format("%04x", pc)+"	MOV	D,B"); break;
		case 0x51: System.out.println(String.format("%04x", pc)+"	MOV	D,C"); break;
		case 0x52: System.out.println(String.format("%04x", pc)+"	MOV	D,D"); break;
		case 0x53: System.out.println(String.format("%04x", pc)+"	MOV	D,E"); break;
		case 0x54: System.out.println(String.format("%04x", pc)+"	MOV	D,H"); break;
		case 0x55: System.out.println(String.format("%04x", pc)+"	MOV	D,L"); break;
		case 0x56: System.out.println(String.format("%04x", pc)+"	MOV	D,M"); break;
		case 0x57: System.out.println(String.format("%04x", pc)+"	MOV	D,A"); break;
		case 0x58: System.out.println(String.format("%04x", pc)+"	MOV	E,B"); break;
		case 0x59: System.out.println(String.format("%04x", pc)+"	MOV	E,C"); break;
		case 0x5a: System.out.println(String.format("%04x", pc)+"	MOV	E,D"); break;
		case 0x5b: System.out.println(String.format("%04x", pc)+"	MOV	E,E"); break;
		case 0x5c: System.out.println(String.format("%04x", pc)+"	MOV	E,H"); break;
		case 0x5d: System.out.println(String.format("%04x", pc)+"	MOV	E,L"); break;
		case 0x5e: System.out.println(String.format("%04x", pc)+"	MOV	E,M"); break;
		case 0x5f: System.out.println(String.format("%04x", pc)+"	MOV	E,A"); break;
		case 0x60: System.out.println(String.format("%04x", pc)+"	MOV	H,B"); break;
		case 0x61: System.out.println(String.format("%04x", pc)+"	MOV	H,C"); break;
		case 0x62: System.out.println(String.format("%04x", pc)+"	MOV	H,D"); break;
		case 0x63: System.out.println(String.format("%04x", pc)+"	MOV	H,E"); break;
		case 0x64: System.out.println(String.format("%04x", pc)+"	MOV	H,H"); break;
		case 0x65: System.out.println(String.format("%04x", pc)+"	MOV	H,L"); break;
		case 0x66: System.out.println(String.format("%04x", pc)+"	MOV	H,M"); break;
		case 0x67: System.out.println(String.format("%04x", pc)+"	MOV	H,A"); break;
		case 0x68: System.out.println(String.format("%04x", pc)+"	MOV	L,B"); break;
		case 0x69: System.out.println(String.format("%04x", pc)+"	MOV	L,C"); break;
		case 0x6a: System.out.println(String.format("%04x", pc)+"	MOV	L,D"); break;
		case 0x6b: System.out.println(String.format("%04x", pc)+"	MOV	L,E"); break;
		case 0x6c: System.out.println(String.format("%04x", pc)+"	MOV	L,H"); break;
		case 0x6d: System.out.println(String.format("%04x", pc)+"	MOV	L,L"); break;
		case 0x6e: System.out.println(String.format("%04x", pc)+"	MOV	L,M"); break;
		case 0x6f: System.out.println(String.format("%04x", pc)+"	MOV	L,A"); break;
		case 0x70: System.out.println(String.format("%04x", pc)+"	MOV	M,B"); break;
		case 0x71: System.out.println(String.format("%04x", pc)+"	MOV	M,C"); break;
		case 0x72: System.out.println(String.format("%04x", pc)+"	MOV	M,D"); break;
		case 0x73: System.out.println(String.format("%04x", pc)+"	MOV	M,E"); break;
		case 0x74: System.out.println(String.format("%04x", pc)+"	MOV	M,H"); break;
		case 0x75: System.out.println(String.format("%04x", pc)+"	MOV	M,L"); break;
		case 0x76: System.out.println(String.format("%04x", pc)+"	HLT"); break;
		case 0x77: System.out.println(String.format("%04x", pc)+"	MOV	M,A"); break;
		case 0x78: System.out.println(String.format("%04x", pc)+"	MOV	A,B"); break;
		case 0x79: System.out.println(String.format("%04x", pc)+"	MOV	A,C"); break;
		case 0x7a: System.out.println(String.format("%04x", pc)+"	MOV	A,D"); break;
		case 0x7b: System.out.println(String.format("%04x", pc)+"	MOV	A,E"); break;
		case 0x7c: System.out.println(String.format("%04x", pc)+"	MOV	A,H"); break;
		case 0x7d: System.out.println(String.format("%04x", pc)+"	MOV	A,L"); break;
		case 0x7e: System.out.println(String.format("%04x", pc)+"	MOV	A,M"); break;
		case 0x7f: System.out.println(String.format("%04x", pc)+"	MOV	A,A"); break;
		case 0x80: System.out.println(String.format("%04x", pc)+"	ADD	B"); break;
		case 0x81: System.out.println(String.format("%04x", pc)+"	ADD	C"); break;
		case 0x82: System.out.println(String.format("%04x", pc)+"	ADD	D"); break;
		case 0x83: System.out.println(String.format("%04x", pc)+"	ADD	E"); break;
		case 0x84: System.out.println(String.format("%04x", pc)+"	ADD	H"); break;
		case 0x85: System.out.println(String.format("%04x", pc)+"	ADD	L"); break;
		case 0x86: System.out.println(String.format("%04x", pc)+"	ADD	M"); break;
		case 0x87: System.out.println(String.format("%04x", pc)+"	ADD	A"); break;
		case 0x88: System.out.println(String.format("%04x", pc)+"	ADC	B"); break;
		case 0x89: System.out.println(String.format("%04x", pc)+"	ADC	C"); break;
		case 0x8a: System.out.println(String.format("%04x", pc)+"	ADC	D"); break;
		case 0x8b: System.out.println(String.format("%04x", pc)+"	ADC	E"); break;
		case 0x8c: System.out.println(String.format("%04x", pc)+"	ADC	H"); break;
		case 0x8d: System.out.println(String.format("%04x", pc)+"	ADC	L"); break;
		case 0x8e: System.out.println(String.format("%04x", pc)+"	ADC	M"); break;
		case 0x8f: System.out.println(String.format("%04x", pc)+"	ADC	A"); break;
		case 0x90: System.out.println(String.format("%04x", pc)+"	SUB	B"); break;
		case 0x91: System.out.println(String.format("%04x", pc)+"	SUB	C"); break;
		case 0x92: System.out.println(String.format("%04x", pc)+"	SUB	D"); break;
		case 0x93: System.out.println(String.format("%04x", pc)+"	SUB	E"); break;
		case 0x94: System.out.println(String.format("%04x", pc)+"	SUB	H"); break;
		case 0x95: System.out.println(String.format("%04x", pc)+"	SUB	L"); break;
		case 0x96: System.out.println(String.format("%04x", pc)+"	SUB	M"); break;
		case 0x97: System.out.println(String.format("%04x", pc)+"	SUB	A"); break;
		case 0x98: System.out.println(String.format("%04x", pc)+"	SBB	B"); break;
		case 0x99: System.out.println(String.format("%04x", pc)+"	SBB	C"); break;
		case 0x9a: System.out.println(String.format("%04x", pc)+"	SBB	D"); break;
		case 0x9b: System.out.println(String.format("%04x", pc)+"	SBB	E"); break;
		case 0x9c: System.out.println(String.format("%04x", pc)+"	SBB	H"); break;
		case 0x9d: System.out.println(String.format("%04x", pc)+"	SBB	L"); break;
		case 0x9e: System.out.println(String.format("%04x", pc)+"	SBB	M"); break;
		case 0x9f: System.out.println(String.format("%04x", pc)+"	SBB	A"); break;
		case 0xa0: System.out.println(String.format("%04x", pc)+"	ANA	B"); break;
		case 0xa1: System.out.println(String.format("%04x", pc)+"	ANA	C"); break;
		case 0xa2: System.out.println(String.format("%04x", pc)+"	ANA	D"); break;
		case 0xa3: System.out.println(String.format("%04x", pc)+"	ANA	E"); break;
		case 0xa4: System.out.println(String.format("%04x", pc)+"	ANA	H"); break;
		case 0xa5: System.out.println(String.format("%04x", pc)+"	ANA	L"); break;
		case 0xa6: System.out.println(String.format("%04x", pc)+"	ANA	M"); break;
		case 0xa7: System.out.println(String.format("%04x", pc)+"	ANA	A"); break;
		case 0xa8: System.out.println(String.format("%04x", pc)+"	XRA	B"); break;
		case 0xa9: System.out.println(String.format("%04x", pc)+"	XRA	C"); break;
		case 0xaa: System.out.println(String.format("%04x", pc)+"	XRA	D"); break;
		case 0xab: System.out.println(String.format("%04x", pc)+"	XRA	E"); break;
		case 0xac: System.out.println(String.format("%04x", pc)+"	XRA	H"); break;
		case 0xad: System.out.println(String.format("%04x", pc)+"	XRA	L"); break;
		case 0xae: System.out.println(String.format("%04x", pc)+"	XRA	M"); break;
		case 0xaf: System.out.println(String.format("%04x", pc)+"	XRA	A"); break;
		case 0xb0: System.out.println(String.format("%04x", pc)+"	ORA	B"); break;
		case 0xb1: System.out.println(String.format("%04x", pc)+"	ORA	C"); break;
		case 0xb2: System.out.println(String.format("%04x", pc)+"	ORA	D"); break;
		case 0xb3: System.out.println(String.format("%04x", pc)+"	ORA	E"); break;
		case 0xb4: System.out.println(String.format("%04x", pc)+"	ORA	H"); break;
		case 0xb5: System.out.println(String.format("%04x", pc)+"	ORA	L"); break;
		case 0xb6: System.out.println(String.format("%04x", pc)+"	ORA	M"); break;
		case 0xb7: System.out.println(String.format("%04x", pc)+"	ORA	A"); break;
		case 0xb8: System.out.println(String.format("%04x", pc)+"	CMP	B"); break;
		case 0xb9: System.out.println(String.format("%04x", pc)+"	CMP	C"); break;
		case 0xba: System.out.println(String.format("%04x", pc)+"	CMP	D"); break;
		case 0xbb: System.out.println(String.format("%04x", pc)+"	CMP	E"); break;
		case 0xbc: System.out.println(String.format("%04x", pc)+"	CMP	H"); break;
		case 0xbd: System.out.println(String.format("%04x", pc)+"	CMP	L"); break;
		case 0xbe: System.out.println(String.format("%04x", pc)+"	CMP	M"); break;
		case 0xbf: System.out.println(String.format("%04x", pc)+"	CMP	A"); break;
		case 0xc0: System.out.println(String.format("%04x", pc)+"	RNZ"); break;
		case 0xc1: System.out.println(String.format("%04x", pc)+"	POP	B"); break;
		case 0xc2: System.out.println(String.format("%04x", pc)+"	JNZ	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xc3: System.out.println(String.format("%04x", pc)+"	JMP	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xc4: System.out.println(String.format("%04x", pc)+"	CNZ	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xc5: System.out.println(String.format("%04x", pc)+"	PUSH	B"); break;
		case 0xc6: System.out.println(String.format("%04x", pc)+"	ADI	$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xc7: System.out.println(String.format("%04x", pc)+"	RST	0"); break;
		case 0xc8: System.out.println(String.format("%04x", pc)+"	RZ"); break;
		case 0xc9: System.out.println(String.format("%04x", pc)+"	RET"); break;
		case 0xca: System.out.println(String.format("%04x", pc)+"	JZ	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xcb: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0xcc: System.out.println(String.format("%04x", pc)+"	CZ	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xcd: System.out.println(String.format("%04x", pc)+"	CALL	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xce: System.out.println(String.format("%04x", pc)+"	ACI	$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xcf: System.out.println(String.format("%04x", pc)+"	RST	1"); break;
		case 0xd0: System.out.println(String.format("%04x", pc)+"	RNC"); break;
		case 0xd1: System.out.println(String.format("%04x", pc)+"	POP	D"); break;
		case 0xd2: System.out.println(String.format("%04x", pc)+"	JNC	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xd3: System.out.println(String.format("%04x", pc)+"	OUT	#$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xd4: System.out.println(String.format("%04x", pc)+"	CNC	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xd5: System.out.println(String.format("%04x", pc)+"	PUSH	D"); break;
		case 0xd6: System.out.println(String.format("%04x", pc)+"	SUI	$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xd7: System.out.println(String.format("%04x", pc)+"	RST	2"); break;
		case 0xd8: System.out.println(String.format("%04x", pc)+"	RC"); break;
		case 0xd9: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0xda: System.out.println(String.format("%04x", pc)+"	JC	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xdb: System.out.println(String.format("%04x", pc)+"	IN	#$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xdc: System.out.println(String.format("%04x", pc)+"	CC	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xdd: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0xde: System.out.println(String.format("%04x", pc)+"	SBI	$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xdf: System.out.println(String.format("%04x", pc)+"	RST	3"); break;
		case 0xe0: System.out.println(String.format("%04x", pc)+"	RPO	1"); break;
		case 0xe1: System.out.println(String.format("%04x", pc)+"	POP	H"); break;
		case 0xe2: System.out.println(String.format("%04x", pc)+"	JPO	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xe3: System.out.println(String.format("%04x", pc)+"	XTHL"); break;
		case 0xe4: System.out.println(String.format("%04x", pc)+"	CPO	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xe5: System.out.println(String.format("%04x", pc)+"	PUSH	H"); break;
		case 0xe6: System.out.println(String.format("%04x", pc)+"	ANI	#$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xe7: System.out.println(String.format("%04x", pc)+"	RST	4"); break;
		case 0xe8: System.out.println(String.format("%04x", pc)+"	RPE"); break;
		case 0xe9: System.out.println(String.format("%04x", pc)+"	PCHL"); break;
		case 0xea: System.out.println(String.format("%04x", pc)+"	JPE	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xeb: System.out.println(String.format("%04x", pc)+"	XCHG"); break;
		case 0xec: System.out.println(String.format("%04x", pc)+"	CPE $"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xed: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0xee: System.out.println(String.format("%04x", pc)+"	XRI	#$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xef: System.out.println(String.format("%04x", pc)+"	RST	5"); break;
		case 0xf0: System.out.println(String.format("%04x", pc)+"	RP"); break;
		case 0xf1: System.out.println(String.format("%04x", pc)+"	POP	PSW"); break;
		case 0xf2: System.out.println(String.format("%04x", pc)+"	JP	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xf3: System.out.println(String.format("%04x", pc)+"	DI"); break;
		case 0xf4: System.out.println(String.format("%04x", pc)+"	CP	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xf5: System.out.println(String.format("%04x", pc)+"	PUSH	PSW"); break;
		case 0xf6: System.out.println(String.format("%04x", pc)+"	ORI	#$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xf7: System.out.println(String.format("%04x", pc)+"	RST	6"); break;
		case 0xf8: System.out.println(String.format("%04x", pc)+"	RM"); break;
		case 0xf9: System.out.println(String.format("%04x", pc)+"	SPHL"); break;
		case 0xfa: System.out.println(String.format("%04x", pc)+"	JM	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xfb: System.out.println(String.format("%04x", pc)+"	EL"); break;
		case 0xfc: System.out.println(String.format("%04x", pc)+"	CM	$"+String.format("%x", dat[pc+2])+String.format("%x", dat[pc+1]));opbytes=3; break;
		case 0xfd: System.out.println(String.format("%04x", pc)+"	NOP"); break;
		case 0xfe: System.out.println(String.format("%04x", pc)+"	CPI	#$"+String.format("%x",dat[pc+1])); opbytes=2; break;
		case 0xff: System.out.println(String.format("%04x", pc)+"	RST	7"); break;
		
		}
		return opbytes;
	}

}
