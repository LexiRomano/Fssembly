package fssembly.compiler;

import java.util.ArrayList;

public class Label extends Referenceable {
	private String name;
	
	public Label(String name) {
		this.name = name;
		references = new ArrayList<Integer>();
	}
	
	public String getName() {
		return name;
	}
	
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
		line = newLine;
	}
}




