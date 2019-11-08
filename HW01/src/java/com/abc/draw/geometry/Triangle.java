package com.abc.draw.geometry;

import java.awt.*;

import com.abc.draw.*;

public class Triangle implements Drawable{

    private Point p1;
    private Point p2;
    private Point p3;
    private Line l1;
    private Line l2;
    private Line l3;

    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    @Override
    public void draw(Graphics2D g2) {
        l1 = new Line(p1, p2);
        l2 = new Line(p1, p3);
        l3 = new Line(p2, p3);

        l1.draw(g2);
        l2.draw(g2);
        l3.draw(g2);
    }



}
