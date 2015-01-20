package com.hotmail.daemon3000;

import com.badlogic.gdx.math.Vector2;

public interface Scene {
	
	public void onLoad();
	
	/** Called when the screen should update itself.
	 * @param delta The time in seconds since the last render. */
	public void update(float delta);
	
	/** Called when the screen should render itself.
	 */
	public void render();

	/** @see ApplicationListener#resize(int, int) */
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize);

	/** @see ApplicationListener#pause() */
	public void pause();

	/** @see ApplicationListener#resume() */
	public void resume();

	/** Called when this screen should release all resources. */
	public void dispose();
}

