package fssembly.compiler;

public class Register extends Argument {
	private int register;
	
	public Register(int register) {
		if (register > -1 && register < 8) {
			this.register = register;
		} else {
			throw new IllegalArgumentException("Register index invalid (must be between 0 and 7)");
		}
	}
	
	public Register(String register) {
		if (register.equals("n")) {
			this.register = -1;
		} else {
			throw new IllegalArgumentException("Null register is designated with \"n,\" not \"" + register + "\"");
		}
	}
	
	public int getRegister() {
		return register;
	}
}
