package com.hotmail.daemon3000.Sokoban.android;

import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

public class DirectionGestureAdapter extends GestureAdapter {
	
	private DirectionListener m_listener;
	
	public DirectionGestureAdapter(DirectionListener listener) {
		m_listener = listener;
	}
	
	@Override
    public boolean fling(float velocityX, float velocityY, int button) {
		if(Math.abs(velocityX)>Math.abs(velocityY)){
			if(velocityX > 0.0f) {
				m_listener.onRight();
			}
			else if(velocityX < 0.0f) {
				m_listener.onLeft();
			}
		}
		else {
			if(velocityY > 0.0f) {
				m_listener.onDown();
			}
			else if(velocityY < 0.0f) {                                  
				m_listener.onUp();
			}
		}
		
		return super.fling(velocityX, velocityY, button);
    }
}

