package assignment7;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientCommand implements Serializable {
	private String command;
	private String message;
	private ArrayList<String> list;
	
	public ClientCommand(String command, String message, ArrayList<String> list) {
		this.command = command;
		this.message = message;
		this.list = list;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
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
