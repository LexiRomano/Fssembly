package fssembly.compiler;

public class Command {
	private int command;
	private Argument[] arguments;
	private Label label;
	
	private static int[] VALID_ARGS_LENGTHS = {2, 2, 3, 3, 3, 3, 3, 2, 1, 3, 0, 1, 0, 0, 0, 1, 1, 0};
	
	public Command(String cmd, Argument[] args) {
		int validArgsLength = -1;
		for (int i = 0; i < Compiler.DICTIONARY.length; i++) {
			if (cmd.equals(Compiler.DICTIONARY[i])) {
				command = i;
				validArgsLength = VALID_ARGS_LENGTHS[i];
			}
		}
		if (args.length == validArgsLength) {
			arguments = args;
		} else {
			throw new IllegalArgumentException("Args length does not match the command!"
					+ " Expected: " + validArgsLength + " Actual: " + args.length);
		}
	}
	
	public int getCommand() {
		return command;
	}
	
	public Argument[] getArguments() {
		return arguments;
	}
	
	public void setLabel(Label lbl) {
		label = lbl;
	}
	
	public Label getLabel() {
		return label;
	}
}
