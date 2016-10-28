package Tarot;

public class CardModel implements Comparable<CardModel> {
	public static final int CARD_W = 100;
	public static final int CARD_H = 174;
	
	private String name;
	private String path;
	private int x;
	private int y;
	private int order;
	
	public CardModel(String name, String path, int order){
		this.name = name;
		this.path = path;
		this.order = order;
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
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void moveTo(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int compareTo(CardModel c) {
		return c.order - this.order;
	}
}