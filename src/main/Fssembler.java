package main;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Fssembler {
	private static Frame frame;
	private static Panel panel;
	private static JTextField textField;
	private static JTextArea textArea;

	public static void main(String[] args) {
		
		panel = new Panel(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		
		
		textField = new JTextField(50);
		Font font = new Font("Consolas", 0, 16);
		textField.setFont(font);
		textField.setMargin(new Insets(2, 10, 2, 10));
		textField.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = -6622822293547374865L;
			@Override
		    public void actionPerformed(ActionEvent e){handleGo();}
		});
		con.gridy = 0;
		panel.add(textField, con);
		
		textArea = new JTextArea("Please enter the file name above (relative path and file extension included)",
				5, 50);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(font);
		textArea.setMargin(new Insets(2, 10, 2, 10));
		textArea.setEditable(false);
		textArea.setFocusable(false);
		con.gridy = 1;
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		panel.add(scrollPane, con);
		
		frame = new Frame("Fssembler v0.5.0");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
		
		frame.add(panel);
		frame.setLocation(50, 50);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private static void handleGo() {
		attempts++;
		try {
			var file = readFile(textField.getText());
			if (file.size() == 0) {
				outText(new Response("File is empty!", -1));
				return;
			}
			var split = file.get(0).split(" ");
			
			if (split.length == 2 && split[0].equals("#") && split[1].equals("B")) {
				// Batch assembling
				textArea.setText("Batch assembling...");
				
				for (int i = 1; i < file.size(); i++) {
					try {
						attempts++;
						outTextAppend(fssemble(readFile(file.get(i)), file.get(i)));
					} catch (FileNotFoundException e) {
						outTextAppend(new Response("File \"" + file.get(i) + "\" not found!", -1));
					} catch (IOException e) {
						outTextAppend(new Response("An unexpected error occured :[", -1));
					}
					
				}
				
				textArea.setText(textArea.getText() + "\n\nBatch assembling complete!");
				
			} else {
				outText(fssemble(file, textField.getText()));
			}
			
			
			
		} catch (FileNotFoundException e) {
			outText(new Response("File \"" + textField.getText() + "\" not found!", -1));
		} catch (IOException e) {
			outText(new Response("An unexpected error occured :[", -1));
		}
		
	}
	
	private static int attempts = 0;
	private static ArrayList<Referenceable> references;
	
	private static Response fssemble(ArrayList<String> fileIn, String fileName) {
		String line;
		int headerVarSpace = -1;
		
		if (fileIn.size() == 0) {
			return new Response("File is empty!", -1);
		}
		
		// trimming dead space
		for (int i = 0; i < fileIn.size(); i++) {
			fileIn.set(i, fileIn.get(i).trim());
			if (fileIn.get(i).trim().split(" ")[0].equals("")) {
				fileIn.set(i, "");
			}
		}
		
		try {
			if (fileIn.get(0).charAt(0) == '#') {
				headerVarSpace = parseInt(fileIn.get(0).split(" ")[1]);
			} else {
				return new Response("Header formatted incorrectly!", 1);
			}
		} catch (Exception e) {
			if (!fileIn.get(0).split(" ")[1].equals("A")) { // Automatic var space
				return new Response("Header formatted incorrectly!", 1);
			}
		}
		
		String name = null;
		
		// executable file
		if (fileIn.get(0).split(" ").length == 3) {
			name = fileIn.get(0).split(" ")[2];
			if (name.length() > 32) {
				return new Response("Executable name too long!", 1);
			}
		}
		
		
		// first pass: finding labels and variables
		String[] split;
		Referenceable r;
		references = new ArrayList<Referenceable>();
		for (int i = 0; i < fileIn.size(); i++) {
			line = fileIn.get(i);
			if (line.length() == 0) {
				continue;
			}
			split = line.split(" ");
			r = null;
			if (split.length == 1 && line.charAt(0) == ':') {
				// label
				r = new Label(split[0].substring(1));
			}
			if (Variable.VAR_TYPE.isVar(split[0])) {
				// variables
				try {
					r = new Variable(line.split(" ")[0], line.split(" ")[1]);
					if (headerVarSpace != -1) {
						r.setline(headerVarSpace);
						headerVarSpace += ((Variable) r).getType().getSize();
					} else {
						((Variable) r).setIsRelative(true);
					}
					
				} catch (IllegalArgumentException e) {
					return new Response(e.getMessage(), i + 1);
				} catch (ArrayIndexOutOfBoundsException e) {
					return new Response("Variable requires a name!", i + 1);
				}
			}
			
			if (r != null) {
				for (char c : Command.getReservedChars()) {
					if (r.getName().contains(String.valueOf(c))) {
						return new Response("Variabe/label name contains a reserved character!", i + 1);
					}
				}
				for (var ref : references) {
					if (ref.getName().equals(r.getName())) {
						return new Response("Duplicate variable/label name \""
								+ r.getName() + "\"!", i + 1);
					}
				}
				
				references.add(r);
			}
		}
		
		// second pass: finding commands and locating labels
		Command c;
		var commands = new ArrayList<Command>();
		int outLine = 0;
		
		if (name != null) {
			outLine = 33;
		}
		
		for (int i = 0; i < fileIn.size(); i++) {
			line = fileIn.get(i);
			if (line.length() == 0) {
				continue;
			}
			split = line.split(" ");
			if (split.length != 0) {
				if (split[0].charAt(0) == ':') {
					for (var ref : references) {
						if (ref instanceof Label) {
							if (ref.getName().equals(split[0].substring(1))) {
								ref.setline(outLine);
							}
						}
					}
				}
				try {
					for (var com : Command.COMMANDS.values()) {
						if (com.toString().equals(split[0])) {
							c = new Command(com, split.length > 1 ? split[1] : null, references);
							c.setRelativeOffset(outLine);
							outLine += c.getLength();
							commands.add(c);
							break;
						}
					}
				} catch (IllegalArgumentException e) {
					return new Response(e.getMessage(), i + 1);
				}
			}
		}
		
		// Third pass: assigning relative variable locations (if applicable)
		if (headerVarSpace == -1) {
			headerVarSpace = 0;
			for (var ref : references) {
				if (ref instanceof Variable) {
					ref.setline(outLine + headerVarSpace);
					headerVarSpace += ((Variable) ref).getType().getSize();
				}
			}
		}
		
		// outputing
		var fileOut = new ArrayList<String>();
		if (name != null) {
			// name if its an executable file
			for (int i = 0; i < 33; i++) {
				if (i < name.length()) {
					fileOut.add(String.format("%1$02X", (int)name.charAt(i)));
				} else {
					fileOut.add("00");
				}
			}
		}
		for (var com : commands) {
			// commands
			split = com.getOutput();
			for (var s : split) {
				fileOut.add(s);
			}
		}
		if (name != null) {
			// subsector count if its an executable file
			fileOut.set(32, String.format("%1$02X", (int)Math.ceil(fileOut.size() / 256.0)));
		}
		
		String outName = "";
		try {
			FileWriter fileW;
			PrintWriter printW;
			
			int a = 0;
			if (fileName.split("/").length > 1) {
				a = fileName.split("/")[0].length() + 1;
			}
			split = fileName.split("/");
			split = split[split.length - 1].split("\\.");
			int b = split[split.length - 1].length();
			outName += fileName.substring(a, fileName.length() - b - 1);

			fileW = new FileWriter("fbn/" + outName + ".fbn");
			printW = new PrintWriter(fileW);

			for (String s : fileOut) {
				printW.println(s);
			}

			fileW.close();
			printW.close();
		} catch(IOException e) {
			return new Response("Error when saving to \"fbn/" + outName + ".fbn\"", -1);
		}
		
		return new Response("Successfully fssembled \"fbn/" + outName + ".fbn\"!", -1);
		
	}
	
	private static void outText(Response response) {
		if (response.getLine() == -1) {
			textArea.setText("Attempts: " + attempts + "\n\n" + response.getMessage());
		} else {
			textArea.setText("Attempts: " + attempts + "\n\n" + response.getMessage()
					+ " (line " + response.getLine() + ")");
		}
	}
	
	private static void outTextAppend(Response response) {
		if (response.getLine() == -1) {
			textArea.setText(textArea.getText() + "\n\nAttempts: " + attempts + "\n\n" + response.getMessage());
		} else {
			textArea.setText(textArea.getText() + "\n\nAttempts: " + attempts + "\n\n" + response.getMessage()
					+ " (line " + response.getLine() + ")");
		}
	}
	
	private static ArrayList<String> readFile(String fileName) throws IOException {
		String line;
		ArrayList<String> fileIn = new ArrayList<String>();
		FileReader fileHandle;
		BufferedReader fileStream;
		
		fileHandle = new FileReader(fileName);

		fileStream = new BufferedReader(fileHandle);

		while ((line = fileStream.readLine()) != null) {
			fileIn.add(line);
		}
		fileHandle.close();
		fileStream.close();
		
		return fileIn;
	}
	
	private static int parseInt(String s) {
		if (s.startsWith("0x")) {
			return Integer.parseInt(s.substring(2), 16);
		} else if (s.startsWith("0b")) {
			return Integer.parseInt(s.substring(2), 2);
		}
		return Integer.parseInt(s);
	}
	
}



