package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Command {
	public static enum COMMANDS {
		LDA,
		STA,
		LDX,
		STX,
		INA,
		DEA,
		INX,
		DEX,
		ADD,
		SUB,
		BLT,
		BLC,
		BRT,
		BRC,
		AND,
		ORR,
		XOR,
		NOT,
		JMP,
		JMS,
		JZR,
		JZS,
		JPO,
		JPS,
		JNE,
		JNS,
		JOV,
		JOS,
		RFS,
		REL,
		REP,
		PIN,
		RIN,
		CIN,
		TGI,
		FAA,
		NOP,
		SHD;
		
		public static COMMANDS[] NA = {
				INA,
				DEA,
				INX,
				DEX,
				BLT,
				BLC,
				BRT,
				BRC,
				NOT,
				RFS,
				REL,
				REP,
				PIN,
				RIN,
				CIN,
				FAA,
				NOP,
				SHD
		};
		
		public static boolean isNA(COMMANDS c) {
			for (var na : NA) {
				if (c.equals(na)) {
					return true;
				}
			}
			return false;
		}
		
		public static COMMANDS[] I = {
				LDA,
				LDX,
				ADD,
				SUB,
				AND,
				ORR,
				XOR,
				TGI
		};
		
		public static COMMANDS[] I_1 = {
			LDA,
			ADD,
			SUB,
			AND,
			ORR,
			XOR
		};
		
		public static boolean isI(COMMANDS c) {
			for (var i : I) {
				if (c.equals(i)) {
					return true;
				}
			}
			return false;
		}
		
		public static boolean isI_1(COMMANDS c) {
			for (var i : I_1) {
				if (c.equals(i)) {
					return true;
				}
			}
			return false;
		}
		
		public static COMMANDS[] L = {
				LDA,
				STA,
				LDX,
				STX,
				ADD,
				SUB,
				AND,
				ORR,
				XOR,
				JMP,
				JMS,
				JZR,
				JZS,
				JPO,
				JPS,
				JNE,
				JNS,
				JOV,
				JOS,
				TGI,
				FAA
		};
		
		public static boolean isL(COMMANDS c) {
			for (var l : L) {
				if (c.equals(l)) {
					return true;
				}
			}
			return false;
		}
		
		public static COMMANDS[] X_LX = {
				LDA,
				STA,
				ADD,
				SUB,
				AND,
				ORR,
				XOR,
				TGI,
				FAA
		};
		
		public static boolean isX(COMMANDS c) {
			for (var x : X_LX) {
				if (c.equals(x)) {
					return true;
				}
			}
			return false;
		}
		
		public static boolean isLX(COMMANDS c) {
			return isX(c);
		}
		
		public static COMMANDS[] P = {
				LDA,
				STA,
				ADD,
				SUB,
				AND,
				ORR,
				XOR,
				JMP,
				JMS,
				JZR,
				JZS,
				JPO,
				JPS,
				JNE,
				JNS,
				JOV,
				JOS,
				TGI,
				FAA
		};
		
		public static boolean isP(COMMANDS c) {
			for (var p : P) {
				if (c.equals(p)) {
					return true;
				}
			}
			return false;
		}
		
		public static COMMANDS[] PNP = {
				LDA,
				STA,
				ADD,
				SUB,
				AND,
				ORR,
				XOR,
				FAA
		};
		
		public static boolean isPNP(COMMANDS c) {
			for (var pnp : PNP) {
				if (c.equals(pnp)) {
					return true;
				}
			}
			return false;
		}
		
		public int getValue(MODIFIERS m) {
			int out = commandValues.get(this);
			if (m.equals(MODIFIERS.L)) {
				return out + 0x20;
			}
			if (m.equals(MODIFIERS.X)) {
				return out + 0x40;
			}
			if (m.equals(MODIFIERS.LX)) {
				return out + 0x60;
			}
			if (m.equals(MODIFIERS.P)) {
				return out + 0x80;
			}
			if (m.equals(MODIFIERS.PNP)) {
				return out + 0xA0;
			}
			return out;
		}
		
		private static HashMap<COMMANDS, Integer> commandValues = new HashMap<COMMANDS, Integer>() {
			private static final long serialVersionUID = 849315236749283206L;
		{
			put(LDA, 0x02);
			put(STA, 0x03);
			put(LDX, 0x04);
			put(STX, 0x05);
			put(INA, 0x08);
			put(DEA, 0x09);
			put(INX, 0x0A);
			put(DEX, 0x0B);
			put(ADD, 0x0C);
			put(SUB, 0x0D);
			put(BLT, 0x0E);
			put(BLC, 0x1E);
			put(BRT, 0x0F);
			put(BRC, 0x1F);
			put(AND, 0x18);
			put(ORR, 0x19);
			put(XOR, 0x1A);
			put(NOT, 0x1B);
			put(JMP, 0x10);
			put(JMS, 0x50);
			put(JZR, 0x11);
			put(JZS, 0x51);
			put(JPO, 0x12);
			put(JPS, 0x52);
			put(JNE, 0x13);
			put(JNS, 0x53);
			put(JOV, 0x14);
			put(JOS, 0x54);
			put(RFS, 0x15);
			put(REL, 0x01);
			put(REP, 0x81);
			put(PIN, 0x35);
			put(RIN, 0x55);
			put(CIN, 0x75);
			put(TGI, 0x06);
			put(FAA, 0x16);
			put(NOP, 0x00);
			put(SHD, 0xFF);
	}};
		
	};
	public static enum MODIFIERS {
		NA,
		I,
		L,
		X,
		LX,
		P,
		PNP;

		public static MODIFIERS getModifier(String s, COMMANDS com, Referenceable reference) {
			if (s == null) {
				if (COMMANDS.isNA(com)) {
					return NA;
				}
				throw new IllegalArgumentException(com.toString() + " command requires an argument!");
			}
			if (s != null && COMMANDS.isNA(com)) {
				if (!com.equals(COMMANDS.FAA)) {// Special exception
					throw new IllegalArgumentException(com.toString() + " command does not accept an argument!");
				}
			}
			char c = s.charAt(0);
			if (c == '@') {
				if (COMMANDS.isL(com)) {
					return L;
				}
				throw new IllegalArgumentException(com.toString() + " does not support the L modifier!");
			} else if (c == '^') {
				if (s.length() > 1) {
					throw new IllegalArgumentException("X type commands do not accept literals or variables!");
				}
				if (COMMANDS.isX(com)) {
					return X;
				}
				throw new IllegalArgumentException(com.toString() + " does not support the X modifier");
			} else if (c == '*') {
				if (COMMANDS.isLX(com)) {
					return LX;
				}
				throw new IllegalArgumentException(com.toString() + " does not support the LX modifier");
			} else if (c == '$') {
				if (COMMANDS.isP(com)) {
					return P;
				}
				throw new IllegalArgumentException(com.toString() + " does not support the P modifier");
			}
			if (reference == null) {
				if (COMMANDS.isI(com)) {
					return I;
				}
				throw new IllegalArgumentException(com.toString() + " does not support the I modifier");
			} else if (reference instanceof Label) {
				return L;
			} else if (reference instanceof Variable) {
				var type = ((Variable) reference).getType();
				if (type.equals(Variable.VAR_TYPE.VAR) || type.equals(Variable.VAR_TYPE.DBL)) {
					return L;
				}
				if (s.charAt(s.length() - 1) == '+') {
					if (type.equals(Variable.VAR_TYPE.PND) && COMMANDS.isPNP(com)) {
						return PNP;
					}
					throw new IllegalArgumentException("The \"+\" modifier is not supported in this use");
				}
				return P;
			}
			throw new IllegalArgumentException("Could not resolve modifier!");
		}
	}
	
	
	
	private static char[] reservedChars = {'@', '^', '*', '$', '+'};

	private int literal = -1;
	private int plusModifier = 0;
	private int rOffset = 0;
	private Referenceable reference = null;
	private COMMANDS command = null;
	private MODIFIERS modifier = null;
	
	public Command(COMMANDS command, String argument, ArrayList<Referenceable> references) {
		if (command == null) {
			throw new IllegalArgumentException("Command cannot be null!");
		}
		this.command = command;
		if (argument == null) {
			if (COMMANDS.isNA(this.command)) {
				modifier = MODIFIERS.NA;
				return;
			}
			throw new IllegalArgumentException("Command \"" + command.toString() + "\" requires an argumet!");
		}
		
		// Parsing referenceable arguments
		String cleanedArgument = argument;
		if (cleanedArgument.length() > 0 && isReservedChar(cleanedArgument.charAt(0))) {
			cleanedArgument = cleanedArgument.substring(1);
		}
		if (cleanedArgument.length() > 0 && isReservedChar(cleanedArgument.charAt(cleanedArgument.length() - 1))) {
			cleanedArgument = cleanedArgument.substring(0, cleanedArgument.length() - 1);
		}
		for (var r : references) {
			if (cleanedArgument.equals(r.getName())) {
				reference = r;
				break;
			}
		}
		
		if (reference == null) { // literals
			try {
				char c = argument.charAt(0);
				if (c == '@' || c == '*' || c == '$') {
					literal = parseInt(argument.substring(1));
				} else if (c == '^') {
					literal = 0;
				} else {
					literal = parseInt(argument);
				}
				modifier = MODIFIERS.getModifier(argument, this.command, null);
				if (literal < 0
						|| (literal > 0xff && modifier.equals(MODIFIERS.I) && COMMANDS.isI_1(this.command))
						|| literal > 0xffff) {
					throw new IllegalArgumentException("Literal out of range!");
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid literal format or variable not defined!");
			}
		} else {
			modifier = MODIFIERS.getModifier(argument, this.command, reference);
		}
		if (argument.endsWith("+")) {
			if (reference instanceof Variable && ((Variable) reference).getType().getSize() == 2) {
				if (!modifier.equals(MODIFIERS.PNP)) {
					plusModifier = 1;
				}
				
			} else {
				throw new IllegalArgumentException("Cannot use the + modifier here!");
			}
		}
		
	}
	
	public void setRelativeOffset(int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset cannot be less than zero!");
		}
		rOffset = offset;
	}
	
	public String[] getOutput() {
		String[] out = new String[getLength()];
		int i = 0;
		if (reference != null && (reference instanceof Label ||
				(reference instanceof Variable && ((Variable) reference).isRelative()))) {
			// Relative commands and all labels require a REL modifier
			out[i] = String.format("%1$02X", COMMANDS.REL.getValue(MODIFIERS.NA));
			i++;
		}
		
		// The command itself
		out[i] = String.format("%1$02X", command.getValue(modifier));
		i++;
		
		if (modifier.equals(MODIFIERS.NA) || modifier.equals(MODIFIERS.X)) {
			// No more arguments, we're done!
			return out;
		}
		
		if (modifier.equals(MODIFIERS.I)) {
			// Immediate commands can have either 1 or 2 byte arugments
			if (COMMANDS.isI_1(command)) {
				out[i] = String.format("%1$02X", literal);
				i++;
			} else {
				out[i] = String.format("%1$02X", (literal & 0xFF00) >> 8);
				i++;
				out[i] = String.format("%1$02X", literal & 0xFF);
				i++;
			}
			return out;
		}
		
		// Only remaining modifiers are I, L, LX, P, and PNP which all take an address as an argument
		
		if (reference != null) {
			if (reference instanceof Label || (reference instanceof Variable && ((Variable) reference).isRelative())) {
				// Relatives and labels need to use rOffset
				out[i] = String.format("%1$02X", ((reference.getLine() - rOffset - 1 + plusModifier) & 0xFF00) >> 8);
				i++;
				out[i] = String.format("%1$02X", (reference.getLine() - rOffset - 1 + plusModifier) & 0xFF);
				i++;
			} else {
				out[i] = String.format("%1$02X", ((reference.getLine() + plusModifier) & 0xFF00) >> 8);
				i++;
				out[i] = String.format("%1$02X", (reference.getLine() + plusModifier) & 0xFF);
				i++;
			}
		} else {
			// No reference means a literal was used
			out[i] = String.format("%1$02X", ((literal + plusModifier) & 0xFF00) >> 8);
			i++;
			out[i] = String.format("%1$02X", (literal + plusModifier) & 0xFF);
			i++;
		}
		
		if (modifier.equals(MODIFIERS.PNP)) {
			// PNP uses an extra byte at the end to reference another byte a the
			// pointer's location. Currently, we only support pointers to doubles
			out[i] = "01";
			i++;
		}
		
		return out;
	}
	
	public int getLength() {
		int len = 1; // base command
		if (reference != null) {
			if (reference instanceof Variable) {
				if (((Variable) reference).isRelative()) {
					len += 1; // space for the REL modifier
				}
			} else if (reference instanceof Label) {
				len += 1; // space for the REL modifier
			}
		}

		if (modifier.equals(MODIFIERS.PNP)) {
			len += 3; // 2 for the address, 1 for the +
		} else if (modifier.equals(MODIFIERS.I)) {
			if (COMMANDS.isI_1(command)) {
				len += 1; // arguments with only 1 byte
			} else {
				len += 2; // arguments with 2 bytes
			}
		} else if (!modifier.equals(MODIFIERS.NA) && !modifier.equals(MODIFIERS.X)) {
			len += 2; // Not I, NA, or X, so the argument is an address
		}
		return len;
	}
	
	public static char[] getReservedChars() {
		return reservedChars;
	}
	
	private static boolean isReservedChar(char c) {
		for (char r : reservedChars) {
			if (r == c) {
				return true;
			}
		}
		return false;
	}
	
	private static int parseInt(String s) {
		if (s.startsWith("0x")) {
			return Integer.parseInt(s.substring(2), 16);
		} else if (s.startsWith("0b")) {
			return Integer.parseInt(s.substring(2), 2);
		}
		return Integer.parseInt(s);
	}
}











