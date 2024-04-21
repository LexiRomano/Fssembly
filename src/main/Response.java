package main;

public class Response {
	
	private String message;
	private int line;
	
	public Response(String message, int line) {
		this.message = message;
		this.line = line;
	}

	public Response(String message) {
		this.message = message;
		this.line = -1;
	}
	
	public String getMessage() {
		return message;
	}

	public int getLine() {
		return line;
	}
	
}
