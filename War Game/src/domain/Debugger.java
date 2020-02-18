package domain;

public class Debugger {
	
	public boolean verbose;
	private String message;
	
	public Debugger(boolean verbose) {
		this.verbose = verbose;
	}

	public void debug(String message) {
		if(this.verbose) {
			System.out.println("[DEBUG] " + message);
		}
	}
}
