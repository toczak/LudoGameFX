package model;

public class LastField {
    int X;
    int Y;
    int color;
    boolean available;

    public LastField(int x, int y,int color) {
        X = x;
        Y = y;
        this.color = color;
        available = true;
    }

    public int getColor() {
        return color;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }
}
