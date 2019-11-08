package com.abc.draw;

import java.awt.*;
import java.util.*;

public class Drawing extends Object {

    ArrayList<Drawable> shapes = new ArrayList<>();

	public Drawing() {
        // FIXME - initialize
	}

	public void drawAll(Graphics2D g2) {
        for(Drawable shape : shapes) {
            shape.draw(g2);
        }
	}

	public void append(Drawable drawable) {
	    shapes.add(drawable);
	}
}
