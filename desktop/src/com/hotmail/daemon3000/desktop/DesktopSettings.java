package com.hotmail.daemon3000.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import com.hotmail.daemon3000.PlatformSettings;
import com.hotmail.daemon3000.PlayerPrefs;

public class DesktopSettings implements PlatformSettings {
	private static final int VIRTUAL_WIDTH = 800;
	private static final int VIRTUAL_HEIGHT = 600;
	
	private PlayerPrefs m_playerPrefs;
	private int m_width;
	private int m_height;
	private boolean m_fullscreen;
	
	public DesktopSettings() {
		m_playerPrefs = new PlayerPrefs();
		m_playerPrefs.load("settings.json");
		m_width = m_playerPrefs.getInt("window_width", VIRTUAL_WIDTH);
		m_height = m_playerPrefs.getInt("window_height", VIRTUAL_HEIGHT);
		m_fullscreen = m_playerPrefs.getBoolean("fullscreen", false);
	}
	
	@Override
	public boolean allowsOptionsScreen() {
		return true;
	}
	
	@Override
	public boolean allowsAddonLevels() {
		return true;
	}
	
	@Override
	public Viewport createViewport() {
		return new ScreenViewport();
	}
	
	@Override
	public Viewport createViewport(Camera camera) {
		ScreenViewport viewport = new ScreenViewport(camera);
		viewport.update(m_width, m_height, true);
		
		return viewport;
	}
	
	@Override
	public Vector2 getScreenSize() {
		return new Vector2(m_width, m_height);
	}
	
	@Override
	public Vector2 getVirtualScreenSize() {
		return new Vector2(m_width, m_height);
	}
	
	@Override
	public boolean getFullscreen() {
		return m_fullscreen;
	}
	
	@Override
	public void changeResolution(int width, int height, boolean fullscreen) {
		m_width = width;
		m_height = height;
		m_fullscreen = fullscreen;
		m_playerPrefs.setInt("window_width", width);
		m_playerPrefs.setInt("window_height", height);
		m_playerPrefs.setBoolean("fullscreen", fullscreen);
		Gdx.graphics.setDisplayMode(width, height, fullscreen);
	}
	
	@Override
	public void setFullscreen(boolean enabled) {
		m_fullscreen = enabled;
		m_playerPrefs.setBoolean("fullscreen", enabled);
		Gdx.graphics.setDisplayMode(m_width, m_height, enabled);
	}
	
	@Override
	public PlayerPrefs getPlayerPrefs() {
		return m_playerPrefs;
	}
	
	@Override
	public void savePlayerPrefs() {
		m_playerPrefs.save(Gdx.files.local("settings.json"));
	}

	@Override
	public FileHandle getAddonLevelPath() {
		return Gdx.files.local("addons/levels/index.json");
	}

	@Override
	public FileHandle getHighscoresPath() {
		return Gdx.files.local("profile/highscores.hsc");
	}
}
