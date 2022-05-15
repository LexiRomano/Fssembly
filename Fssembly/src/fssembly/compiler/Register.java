package fssembly.compiler;

public class Register extends Argument {
	private int register;
	
	public Register(int register) {
		if (register > -2 && register < 8) {
			this.register = register;
		} else {
			throw new IllegalArgumentException("Register index invalid (must be between 0 and 7");
		}
	}
	
	public int getRegister() {
		return register;
	}
}
