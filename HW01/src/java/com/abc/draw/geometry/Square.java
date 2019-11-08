package com.abc.draw.geometry;

import java.awt.*;

import com.abc.draw.*;

public class Square implements Drawable{

    private Point upperLeft;
    private double width;

    public Square(Point upperLeft, double width) {
        this.upperLeft = upperLeft;
        this.width = width;
    }

    public Point getUpperLeft() {
        return upperLeft;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double d) {
        this.width = d;
    }

    @Override
    public void draw(Graphics2D g2) {
        Point upperRight = new Point(upperLeft.getX()+width, upperLeft.getY());
        Point bottomLeft = new Point(upperLeft.getX(), upperLeft.getY()+width);
        Point bottomRight = new Point(upperLeft.getX()+width, upperLeft.getY()+width);

        Line left = new Line(upperLeft, bottomLeft);
        Line top = new Line(upperLeft, upperRight);
        Line right = new Line(upperRight, bottomRight);
        Line bottom = new Line(bottomLeft, bottomRight);

        left.draw(g2);
        top.draw(g2);
        right.draw(g2);
        bottom.draw(g2);

    }
}
