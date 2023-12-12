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
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Fssembler {
	private static Frame frame;
	private static Panel panel;
	private static JTextField textField;
	private static JTextArea textArea;

	private static int attempts = 0;
	
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
		    public void actionPerformed(ActionEvent e){fssemble();}
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
		panel.add(textArea, con);
		
		frame = new Frame("Fssembler v0.2");
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
	
	private static ArrayList<Referenceable> references;
	
	private static void fssemble() {
		attempts++;
		
		// Reading file
		String line;
		ArrayList<String> fileIn = new ArrayList<String>();
		FileReader fileHandle;
		BufferedReader fileStream;
		
		try {
			fileHandle = new FileReader(textField.getText());

			fileStream = new BufferedReader(fileHandle);

			while ((line = fileStream.readLine()) != null) {
				fileIn.add(line);
			}
			fileHandle.close();
			fileStream.close();

		} catch (FileNotFoundException e) {
			outText(attempts, "File \"" + textField.getText() + "\" not found!", -1);
			return;
		} catch (IOException e) {
			outText(attempts, "An unexpected error occured :[", -1);
			return;
		}
		int headerVarSpace = -1;
		
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
				outText(attempts, "Header formatted incorrectly!", 1);
				return;
			}
		} catch (Exception e) {
			if (!fileIn.get(0).split(" ")[1].equals("A")) { // Automatic var space
				outText(attempts, "Header formatted incorrectly!", 1);
				return;
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
					outText(attempts, e.getMessage(), i + 1);
					return;
				} catch (ArrayIndexOutOfBoundsException e) {
					outText(attempts, "Variable requires a name!", i + 1);
					return;
				}
			}
			
			if (r != null) {
				for (char c : Command.getReservedChars()) {
					if (r.getName().contains(String.valueOf(c))) {
						outText(attempts, "Variabe/label name contains a reserved character!", i + 1);
						return;
					}
				}
				for (var ref : references) {
					if (ref.getName().equals(r.getName())) {
						outText(attempts, "Duplicate variable/label name \""
								+ r.getName() + "\"!", i + 1);
						return;
					}
				}
				
				references.add(r);
			}
		}
		
		// second pass: finding commands and locating labels
		Command c;
		var commands = new ArrayList<Command>();
		int outLine = 0;
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
					outText(attempts, e.getMessage(), i + 1);
					return;
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
		for (var com : commands) {
			split = com.getOutput();
			for (var s : split) {
				fileOut.add(s);
			}
		}
		
		try {
			FileWriter fileW;
			PrintWriter printW;

			fileW = new FileWriter("data/o.fbn");
			printW = new PrintWriter(fileW);

			for (String s : fileOut) {
				printW.println(s);
			}

			fileW.close();
			printW.close();
		} catch(IOException e) {
			outText(attempts, "Error when saving to \"data/o.fbin\"", -1);
			return;
		}
		
		outText(attempts, "Successfully fssembled \"" + textField.getText() + "\"!", -1);
		
	}
	
	private static void outText(int attempts, String message, int line) {
		if (line == -1) {
			textArea.setText("Attempts: " + attempts + "\n\n" + message);
		} else {
			textArea.setText("Attempts: " + attempts + "\n\n" + message + " (line " + line + ")");
		}
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



