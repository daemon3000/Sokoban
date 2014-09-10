package com.hotmail.daemon3000;

import com.badlogic.gdx.Game;

public class SokobanGame extends Game {
	@Override
	public void create() {
		setScreen(new StartScreen(this));
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}
