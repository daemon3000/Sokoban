package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.math.Vector2;

public interface Scene {
	public void onLoad();
	public void update(float delta);
	public void render();
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize);
	public void pause();
	public void resume();
	public void dispose();
}

