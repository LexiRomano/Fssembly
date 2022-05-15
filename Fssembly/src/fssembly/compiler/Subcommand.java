package fssembly.compiler;

public class Subcommand extends Argument {
	private int subcommand;
	
	public Subcommand(String subcommand) {
		this.subcommand = -1;
		for (int i = 0; i < Compiler.SUBCOMMANDS.length; i++) {
			if (Compiler.SUBCOMMANDS[i].equals(subcommand)) {
				this.subcommand = i;
			}
		}
		if (this.subcommand == -1) {
			throw new IllegalArgumentException("Invalid subcommand");
		}
	}
	
	public int getSubcommand() {
		return subcommand;
	}
}
