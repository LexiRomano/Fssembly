package main;

public abstract class Referenceable {
	private int line = -1;
	protected String name = null;
	
	public int getLine() {
		if (line == -1) {
			throw new IllegalStateException("Line undetermined");
		}
		return line;
	}
	
	public void setline(int newLine) {
		if (newLine < 0) {
			throw new IllegalArgumentException("Line cannot be negative");
		}
		if (newLine > 0xFFFF) {
			throw new IllegalArgumentException("Line cannot be greater than 0xFFFF!");
		}
		line = newLine;
	}
	
	public String getName() {
		return name;
	}
}
