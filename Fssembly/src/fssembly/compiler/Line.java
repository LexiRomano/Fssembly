package fssembly.compiler;

public class Line extends Argument{
	private int line;
	
	public Line(int line) {
		if (line < 0) {
			throw new IllegalArgumentException("Line number cannot be less than zero!");
		}
		this.line = line;
	}
	
	public int getLine() {
		return line;
	}
}
