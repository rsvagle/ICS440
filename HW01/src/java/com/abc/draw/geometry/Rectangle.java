package com.abc.draw.geometry;

import java.awt.*;

import com.abc.draw.*;

public class Rectangle implements Drawable{

    private Point upperLeft;
    private double width;
    private double height;

    public Rectangle(Point upperLeft, double width, double height) {
        this.upperLeft = upperLeft;
        this.width = width;
        this.height = height;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double d) {
        this.height = d;
    }

    @Override
    public void draw(Graphics2D g2) {
        Point upperRight = new Point(upperLeft.getX()+width, upperLeft.getY());
        Point bottomLeft = new Point(upperLeft.getX(), upperLeft.getY()+height);
        Point bottomRight = new Point(upperLeft.getX()+width, upperLeft.getY()+height);

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
