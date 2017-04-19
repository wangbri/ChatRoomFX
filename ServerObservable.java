package assignment7;

public class ServerObservable {
	private int chatNum = 0;
	public boolean hasChanged = false;
	
	public ServerObservable(int chatNum) {
		this.chatNum = chatNum;
	}
	
//	//TODO: changed
//	public void setChange(){
//		hasChanged = true;
//	}
//	
//	public void clearChange(){
//		hasChanged = false;
//	}
//	////////////////
	
	public String toString() {
		return "Chat " + chatNum;
	}
}
