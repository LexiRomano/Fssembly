package fssembly.compiler;

public class Memory extends Argument {
	private int number;
	
	public Memory(int number) {
		if (number < -7) {
			throw new IllegalArgumentException("Cannot index register");
		}
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
}
