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
				JOS
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
				LDX,
				ADD,
				SUB,
				AND,
				ORR,
				XOR
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
				LDX,
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
				JOS
		};
		
		public static boolean isP(COMMANDS c) {
			for (var p : P) {
				if (c.equals(p)) {
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
		P;

		public static MODIFIERS getModifier(String s, COMMANDS com, Referenceable reference) {
			if (s == null) {
				if (COMMANDS.isNA(com)) {
					return NA;
				}
				throw new IllegalArgumentException(com.toString() + " command requires an argument!");
			}
			if (s != null && COMMANDS.isNA(com)) {
				throw new IllegalArgumentException(com.toString() + " command does not accept an argument!");
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
				if (argument.endsWith("+")) {
					if (r instanceof Variable && ((Variable) r).getType().getSize() == 2) {
						plusModifier = 1;
					} else {
						throw new IllegalArgumentException("Cannot use the + modifier here!");
					}
				}
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
						|| (literal > 0xff && modifier.equals(MODIFIERS.I) && !this.command.equals(COMMANDS.STX))
						|| literal > 0xffff) {
					throw new IllegalArgumentException("Literal out of range!");
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid literal format or variable not defined!");
			}
		} else {
			modifier = MODIFIERS.getModifier(argument, this.command, reference);
		}
	}
	
	public void setRelativeOffset(int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset cannot be less than zero!");
		}
		rOffset = offset;
	}
	
	public String[] getOutput() {
		if (reference != null && (reference instanceof Label ||
				(reference instanceof Variable && ((Variable) reference).isRelative()))) {
			var out = new String[getLength()];
			out[0] = String.format("%1$02X", COMMANDS.REL.getValue(MODIFIERS.NA));
			out[1] = String.format("%1$02X", command.getValue(modifier));
			out[2] = String.format("%1$02X", ((reference.getLine() - rOffset - 1 + plusModifier) & 0xFF00) >> 8);
			out[3] = String.format("%1$02X", (reference.getLine() - rOffset - 1 + plusModifier) & 0xFF);
			return out;
		}
		var out = new String[getLength()];
		out[0] = String.format("%1$02X", command.getValue(modifier));
		if (out.length == 1) {
			return out;
		}
		if (out.length == 2) {
			out[1] = String.format("%1$02X", literal);
			return out;
		}
		if (reference == null) {
			out[1] = String.format("%1$02X", ((literal + plusModifier) & 0xFF00) >> 8);
			out[2] = String.format("%1$02X", (literal + plusModifier) & 0xFF);
		} else {
			out[1] = String.format("%1$02X", ((reference.getLine() + plusModifier) & 0xFF00) >> 8);
			out[2] = String.format("%1$02X", (reference.getLine() + plusModifier) & 0xFF);
		}
		
		
		return out;
	}
	
	public int getLength() {
		if (reference != null && (reference instanceof Label ||
				(reference instanceof Variable && ((Variable) reference).isRelative()))) {
			return 4;
		}
		if (modifier.equals(MODIFIERS.NA) || modifier.equals(MODIFIERS.X)) {
			return 1;
		} else if (modifier.equals(MODIFIERS.I)) {
			if (command.equals(COMMANDS.LDX)) {
				return 3;
			}
			return 2;
		}
		return 3;
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











