package com.hotmail.daemon3000;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public abstract class Game implements ApplicationListener {
	private Scene m_currentScene;
	
	@Override
	public void pause() {
		if(m_currentScene != null)
			m_currentScene.pause();
	}

	@Override
	public void resume() {
		if(m_currentScene != null)
			m_currentScene.resume();
	}

	@Override
	public void render() {
		if(m_currentScene != null) {
			float delta = Gdx.graphics.getDeltaTime();
			m_currentScene.update(delta);
			m_currentScene.render();
		}
	}

	@Override
	public void resize(int width, int height) {
		if(m_currentScene != null)
			m_currentScene.resize(width, height);
	}
	
	@Override
	public void dispose() {
		if(m_currentScene != null) {
			m_currentScene.dispose();
		}
	}
	
	/** Sets the current screen. {@link Scene#hide()} is called on any old screen, and {@link Scene#show()} is called on the new
	 * screen, if any.
	 * @param screen may be {@code null} */
	public void setScene(Scene screen) {
		if(m_currentScene != null) { 
			m_currentScene.dispose();
		}
		m_currentScene = screen;
		if(m_currentScene != null) {
			m_currentScene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	/** @return the currently active {@link Scene}. */
	public Scene getScene() {
		return m_currentScene;
	}
}

