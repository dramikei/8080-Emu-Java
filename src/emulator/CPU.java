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
	double lastTimer;    
    double nextInterrupt;    
    int whichInterrupt;
	boolean interrupt_enable;
	ConditionCode cc;
	
	
	short shift0;         //LSB of Space Invader's external shift hardware
    short shift1;         //MSB
    short shift_offset;         // offset for external shift hardware
    short in_port1;
    //output ports for sounds
    short     out_port3, out_port5, last_out_port3, last_out_port5; 
	
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
		memory = new short[0x10000];
		lastTimer = 0.0;
		nextInterrupt = 0.0;
		whichInterrupt = 0;
		interrupt_enable = false;
		in_port1 = 0;
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