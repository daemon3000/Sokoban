package com.hotmail.daemon3000.Sokoban.android;

import com.badlogic.gdx.input.GestureDetector;

public class DirectionGestureDetector extends GestureDetector {
	public DirectionGestureDetector(DirectionListener listener) {
		super(new DirectionGestureAdapter(listener));
	}
}
