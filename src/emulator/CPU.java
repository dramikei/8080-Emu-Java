package emulator;

public class CPU {
	short a;
	short b;
	short c;
	short d;
	short e;
	short h;
	short l;
	int sp;
	int pc;
	short[] memory;
	short int_enable;
	ConditionCode cc;
	
	public CPU() {
		a = 0;
		b = 0;
		c = 0;
		d = 0;
		e = 0;
		h = 0;
		l = 0;
		sp = 0x0;
		pc = 0x0;
		memory = new short[0xffff];
		cc = new ConditionCode();
	}
	
	
	
}

class ConditionCode {
	short z;
	short s;
	short p;
	short cy;
	short ac;
	short pad;
	
	public ConditionCode() {
		z = 0;
		s = 0;
		p = 0;
		cy = 0;
		ac = 0;
		pad = 0;
	}
}