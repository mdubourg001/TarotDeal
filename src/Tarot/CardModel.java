package Tarot;

public class CardModel implements Comparable<CardModel> {
    public static final int CARD_W = 100;
    public static final int CARD_H = 174;

    private String name;
    private String path;
    private int x;
    private int y;
    private double z;
    public boolean onFront = false;

    private int order;

    public CardModel(String name, String path, int order) {
        this.name = name;
        this.path = path;
        this.order = order;
    }

    public CardModel(int x, int y, double z, boolean onFront) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.onFront = onFront;
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

    public double getZ() {
        return z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void moveTo(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int compareTo(CardModel c) {
        return c.order - this.order;
    }
}