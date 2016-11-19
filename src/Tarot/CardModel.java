package Tarot;

import javafx.stage.Screen;

public class CardModel implements Comparable<CardModel> {
    public static final int CARD_W = (int)(Model.SCREEN_W/12.7) ;
    public static final int CARD_H = (int)(CARD_W*1.74);
    public static final double CARD_DIAG = Math.sqrt(CARD_W*CARD_W + CARD_H*CARD_H);

    private String name;
    private String path;
    private double x;
    private double y;
    private double z;
    public boolean onFront = false;

    private int order;

    public CardModel(String name, String path, int order) {
        this.name = name;
        this.path = path;
        this.order = order;
    }

    public CardModel(double x, double y, double z, boolean onFront) {
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void moveTo(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int compareTo(CardModel c) {
        return c.order - this.order;
    }
}