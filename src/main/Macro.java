package main;

public class Macro {
	
	private String name;
	private String expansion;
	
	public Macro(String name, String expansion) {
		this.name = name;
		this.expansion = expansion;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExpansion() {
		return expansion;
	}
}
