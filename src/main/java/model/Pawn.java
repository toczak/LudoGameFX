package model;

import java.io.Serializable;

public class Pawn implements Serializable {
    int X;
    int Y;
    int color;
    int position;
    boolean available;

    public Pawn(int x, int y, int color) {
        X = x;
        Y = y;
        this.color = color;
        position = 0;
        available = true;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getColor() {
        return color;
    }
}
