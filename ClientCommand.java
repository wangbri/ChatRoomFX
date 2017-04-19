package assignment7;

public class ClientCommand {
	private String command;
	private String message;
	
	public ClientCommand(String command, String message) {
		this.command = command;
		this.message = message;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
