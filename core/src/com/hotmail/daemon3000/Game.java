package com.hotmail.daemon3000;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class Game implements ApplicationListener {
	private Scene m_currentScene;
	private PlatformSettings m_platformSettings;
	
	public Game(PlatformSettings platformSettings) {
		m_platformSettings = platformSettings;
	}
	
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
			m_currentScene.resize(new Vector2(width, height), m_platformSettings.getVirtualScreenSize());
	}
	
	@Override
	public void dispose() {
		if(m_currentScene != null) {
			m_currentScene.dispose();
		}
		m_platformSettings.saveSettings();
	}
	
	public void setScene(Scene screen) {
		if(m_currentScene != null) { 
			m_currentScene.dispose();
		}
		m_currentScene = screen;
	}

	public Scene getScene() {
		return m_currentScene;
	}
	
	public PlatformSettings getPlatformSettings() {
		return m_platformSettings;
	}
}

