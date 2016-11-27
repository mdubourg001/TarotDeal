package Tarot;

public class CardModel implements Comparable<CardModel> {
    public static final int CARD_W = (int)(Model.SCREEN_W/12.7) ;
    public static final int CARD_H = (int)(CARD_W*1.74);
    public static final double CARD_DIAG = Math.sqrt(CARD_W*CARD_W + CARD_H*CARD_H);

    private String name;
    private double x;
    private double y;
    private double z;
    private boolean onFront = false;

    private int order;
    private static int orderCpt = 0;
    
    public static void reinitOrder(){
    	orderCpt = 0;
    }

    public CardModel(String name) {
        this.name = name;
        this.order = orderCpt++;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
    
    public int getOrder() {
        return order;
    }
    
    public boolean isOnFront() {
        return onFront;
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
    
    public void setOnFront(boolean b) {
        this.onFront = b;
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