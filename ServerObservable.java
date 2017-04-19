package assignment7;

public class ServerObservable {
	private int chatNum = 0;
	public boolean hasChanged = false;
	
	public ServerObservable(int chatNum) {
		this.chatNum = chatNum;
	}
	
	public String toString() {
		return "Chat " + chatNum;
	}
}
