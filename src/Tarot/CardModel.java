package Tarot;

public class CardModel {
	public static final int CARD_W = 100;
	public static final int CARD_H = 174;
	
	private String name;
	private String path;
	private int x;
	private int y;
	private boolean recto = false;
	
	public CardModel(String name, String path, int x, int y){
		this.name = name;
		this.path = path;
		this.x = x;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void moveTo(int x, int y){
		this.x = x;
		this.y = y;
	}

	public boolean isRecto() {
		return recto;
	}

	public void revert() {
		this.recto = !this.recto;
	}
}