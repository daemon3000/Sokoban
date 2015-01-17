package com.hotmail.daemon3000;

public interface Scene {
	/** Called when the screen should update itself.
	 * @param delta The time in seconds since the last render. */
	public void update(float delta);
	
	/** Called when the screen should render itself.
	 */
	public void render();

	/** @see ApplicationListener#resize(int, int) */
	public void resize(int width, int height);

	/** @see ApplicationListener#pause() */
	public void pause();

	/** @see ApplicationListener#resume() */
	public void resume();

	/** Called when this screen should release all resources. */
	public void dispose();
}

