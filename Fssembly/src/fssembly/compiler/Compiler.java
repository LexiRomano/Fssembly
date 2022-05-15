package fssembly.compiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Compiler {
	private static ArrayList<Label> labels;
	private static ArrayList<Variable> variables;
	private static ArrayList<Command> commands;
	
	public static void main(String[] not_args) {
		// Reading file
		boolean go = true;
		String ln;
		ArrayList<String> fileIn;
		FileReader fileHandle;
		BufferedReader fileStream;

		fileIn = new ArrayList<String>();

		try {

			fileHandle = new FileReader("data/Program1.txt");

			fileStream = new BufferedReader(fileHandle);

			while ((ln = fileStream.readLine()) != null) {
				fileIn.add(ln);
			}
			fileHandle.close();

		} catch (FileNotFoundException e) {
			go = false;
			System.out.println("File not found.");
		} catch (IOException e) {
			go = false;
			System.out.println("Error:");
			System.out.println(e);
		}
		if (go) {
			ArrayList<String> fileOut = new ArrayList<String>();
			labels = new ArrayList<Label>();
			variables = new ArrayList<Variable>();
			String[] split;
			// Finding labels and variables
			for (String content : fileIn) {
				if (content.charAt(0) == ':') {
					// Duplicate Name Detection
					for (Label lbl : labels) {
						if (content.substring(1).equals(lbl.getName())) {
							throw new IllegalArgumentException("Duplicate label name");
						}
					}
					
					labels.add(new Label(content.substring(1)));
				} else if (content.substring(0, 3).equals("var")) {
					split = content.split(" ");
					// Duplicate Name Detection
					for (Variable var : variables) {
						if (split[1].equals(var.getName())) {
							throw new IllegalArgumentException("Duplicate label name");
						}
					}
					
					variables.add(new Variable(split[1], Integer.parseInt(split[2])));
				}
			}
			// Finding commands
			commands = new ArrayList<Command>();
			Argument[] args;
			boolean needsLabel = false;
			Label newLabel = null;
			for (String content : fileIn) {
				split = content.split(" ");
				// Cheching if there is a label and saving it to associate with the next command
				if (split[0].charAt(0) == ':') {
					needsLabel = true;
					for (Label lbl : labels) {
						if (lbl.getName().equals(split[0].substring(1))) {
							newLabel = lbl;
						}
					}
				}
				// Finding if it matches any known command
				for (String command : DICTIONARY) {
					if (split[0].equals(command)) {
						// Figuring out the arguments
						args = new Argument[split.length - 1];
						// xR xM
						if (command.equals("l")) {
							args[0] = new Register(Integer.parseInt(split[1]));
							if (split[2].charAt(0) == 'r') {
								args[1] = new Memory(-1 * Integer.parseInt(split[2].substring(1)));
							} else {
								args[1] = findReferenceable(split[2]);
							}
						}
						// xM xR
						else if (command.equals("s")) {
							if (split[1].charAt(0) == 'r') {
								args[0] = new Memory(-1 * Integer.parseInt(split[1].substring(1)));
							} else {
								args[0] = findReferenceable(split[1]);
							}
							args[1] = new Register(Integer.parseInt(split[2]));
						}
						// xR xR xR
						else if (command.equals("add")||
								command.equals("sub")||
								command.equals("mul")||
								command.equals("div")||
								command.equals("mod")) {
							// Null register bypass
							if (split[1].equals("n")) {
								args[0] = new Register(-1);

							} else {
								args[0] = new Register(Integer.parseInt(split[1]));
							}
							args[1] = new Register(Integer.parseInt(split[2]));
							args[2] = new Register(Integer.parseInt(split[3]));

						}
						// xR xR
						else if (command.equals("trns")) {
							args[0] = new Register(Integer.parseInt(split[1]));
							args[1] = new Register(Integer.parseInt(split[2]));
						}
						// xL
						else if (command.equals("j")) {
							try {
								args[0] = new Line(Integer.parseInt(split[1]));
							} catch (NumberFormatException e) {
								args[0] = findReferenceable(split[1]);
							}
						}
						// xS xS xL
						else if (command.equals("b")) {
							if (split[1].equals("b") ||
									split[1].equals("s")) {
								args[0] = new Subcommand(split[1]);
							} else {
								throw new IllegalArgumentException(split[1] + " is not a valid argument");
							}
							for (String scmd : SUBCOMMANDS) {
								if (scmd.equals(split[2])) {
									if (!split[2].equals("b") ||
											!split[2].equals("s")) {
										args[1] = new Subcommand(split[2]);
									}
								}
							}
							if (args[1] == null) {
								throw new IllegalArgumentException(split[2] + " is not a valid argument");
							}
							try {
								args[2] = new Line(Integer.parseInt(split[3]));
							} catch (NumberFormatException e) {
								args[2] = findReferenceable(split[3]);
							}
						}
						// xR
						else if (command.equals("ofsb")||
								command.equals("in")||
								command.equals("out")) {
							args[0] = new Register(Integer.parseInt(split[1]));
						}
						
						commands.add(new Command(command, args));
						
						// Associating a label if there was one
						if (needsLabel) {
							commands.get(commands.size()-1).setLabel(newLabel);
							needsLabel = false;
						}
					}
				}
			}
			// Writing commands:
			for (Command cmd : commands) {
				// Determining label location if applicable
				if (cmd.getLabel() != null) {
					cmd.getLabel().setline(fileOut.size());
				}
				// Add the command
				fileOut.add(String.valueOf(cmd.getCommand()));
				
				// Add the arguments
				for (Argument arg : cmd.getArguments()) {
					if (arg instanceof Referenceable) {
						// Placeholder if the argument is referenceable
						((Referenceable) arg).addReference(fileOut.size());
						fileOut.add("X");
					} else if (arg instanceof Register) {
						fileOut.add(String.valueOf(((Register) arg).getRegister()));
						
					} else if (arg instanceof Subcommand) {
						fileOut.add(String.valueOf(((Subcommand) arg).getSubcommand()));
						
					} else if (arg instanceof Line) {
						fileOut.add(String.valueOf(((Line) arg).getLine()));
						
					} else if (arg instanceof Memory) {
						fileOut.add(String.valueOf(((Memory) arg).getNumber()));
					}
				}
			}
			// Just-In-Case terminate
			fileOut.add("17");
			
			// Adding the variables
			for (Variable var : variables) {
				var.setLine(fileOut.size());
				fileOut.add(String.valueOf(var.getValue()));
				for (Integer index : var.getReferences()) {
					fileOut.set(index, String.valueOf(var.getLine()));
				}
			}
			
			// Updating the label references
			for (Label lbl : labels) {
				for (Integer index : lbl.getReferences()) {
					fileOut.set(index, String.valueOf(lbl.getLine()));
				}
			}
			
			// Saving to file
			try {
				FileWriter writer = new FileWriter("data/out.txt");
				PrintWriter printer = new PrintWriter(writer);
				
				for (String content : fileOut) {
					printer.println(content);
				}
				writer.close();
			} catch (IOException e) {
				System.out.println("Error:");
				System.out.println(e);
			}
		}
	}
	
	private static Referenceable findReferenceable(String name) {
		for (Label lbl : labels) {
			if (lbl.getName().equals(name)) {
				return lbl;
			}
		}
		for (Variable var : variables) {
			if (var.getName().equals(name)) {
				return var;
			}
		}
		throw new IllegalArgumentException("Unknown Reference");
	}
	
	public static String[] DICTIONARY = {"l", "s", "add", "sub", "mul", "div", "mod",
			"trns", "j", "b", "rfs", "ofsb", "ofsp", "ofsr", "ofse", "in", "out", "trm"};
	public static String[] SUBCOMMANDS = {"b", "s", "ne", "nezr", "zr", "zrpo", "po"};
}




