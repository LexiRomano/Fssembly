package main;

public class Variable extends Referenceable {
	public enum VAR_TYPE {
			VAR,
			DBL,
			PNT,
			PND;
			
			public static VAR_TYPE parseType(String type) {
				if (type.equals("Var")) {
					return VAR;
				} else if (type.equals("Dbl")) {
					return DBL;
				} else if (type.equals("Pnt")) {
					return PNT;
				} else if (type.equals("Pnd")) {
					return PND;
				}
				throw new IllegalArgumentException("Type could not be parsed!");
			}
			
			public static boolean isVar(String s) {
				try {
					parseType(s);
					return true;
				} catch (IllegalArgumentException e) {
					return false;
				}
			}
			
			public int getSize() {
				if (this == VAR) {
					return 1;
				}
				return 2;
			}
	}
	
	private VAR_TYPE varType = null;
	
	public Variable(String type, String name) {
		this.name = name;
		this.varType = VAR_TYPE.parseType(type);
	}
	
	public VAR_TYPE getType() {
		return varType;
	}
}











