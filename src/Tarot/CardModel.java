package Tarot;

public class CardModel {
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

	public boolean isRecto() {
		return recto;
	}

	public void revert() {
		this.recto = !this.recto;
	}
}