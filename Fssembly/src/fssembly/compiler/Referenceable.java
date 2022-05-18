package fssembly.compiler;

import java.util.ArrayList;

abstract class Referenceable extends Argument {
	protected ArrayList<Integer> references = new ArrayList<Integer>();
	protected int line = -1;
	public void addReference(int line) {
		references.add(line);
	}
	
	public ArrayList<Integer> getReferences() {
		return references;
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public int getLine() {
		return line;
	}
}
