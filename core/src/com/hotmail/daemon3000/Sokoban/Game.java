package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

public abstract class Game implements ApplicationListener {
	private Scene m_currentScene;
	private Scene m_sceneToLoad;
	private PlatformAdapter m_platformSettings;
	private Preferences m_preferences;
	private Vector2 m_screenSize;
	
	public Game(PlatformAdapter platformSettings) {
		m_platformSettings = platformSettings;
		m_screenSize = new Vector2();
	}
	
	@Override
	public void create() {
		m_preferences = Gdx.app.getPreferences("Sokoban");
		m_platformSettings.initialize();
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
		if(m_sceneToLoad != null) {
			setSceneInternal(m_sceneToLoad);
			m_sceneToLoad = null;
		}
		
		if(m_currentScene != null) {
			float delta = Gdx.graphics.getDeltaTime();
			m_currentScene.update(delta);
			m_currentScene.render();
		}
	}

	@Override
	public void resize(int width, int height) {
		m_screenSize.set(width, height);
		m_platformSettings.getInputAdapter().resize(m_screenSize, m_platformSettings.getVirtualScreenSize());
		if(m_currentScene != null)
			m_currentScene.resize(m_screenSize, m_platformSettings.getVirtualScreenSize());
	}
	
	@Override
	public void dispose() {
		m_preferences.flush();
		m_platformSettings.getInputAdapter().dispose();
		if(m_currentScene != null) {
			m_currentScene.dispose();
		}
	}
	
	public void setScene(Scene scene) {
		m_sceneToLoad = scene;
	}
	
	private void setSceneInternal(Scene scene) {
		if(m_currentScene != null) { 
			m_currentScene.dispose();
		}
		m_currentScene = scene;
		m_currentScene.onLoad();
	}

	public Scene getScene() {
		return m_currentScene;
	}
	
	public PlatformAdapter getPlatformAdapter() {
		return m_platformSettings;
	}
	
	public Preferences getPreferences() {
		return m_preferences;
	}
	
	public void exit() {
		Gdx.app.exit();
	}
}

