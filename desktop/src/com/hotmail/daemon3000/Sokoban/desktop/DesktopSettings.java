package com.hotmail.daemon3000.Sokoban.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import com.hotmail.daemon3000.Sokoban.InputAdapter;
import com.hotmail.daemon3000.Sokoban.PlatformAdapter;

public class DesktopSettings implements PlatformAdapter {
	private Vector2 m_virtualScreenSize;
	private FileHandle m_addonPath;
	private FileHandle m_highscorePath;
	private InputAdapter m_inputAdapter;
	
	@Override
	public void initialize() {
		m_virtualScreenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		m_addonPath = Gdx.files.local("addons/levels/index.json");
		m_highscorePath = Gdx.files.local("profile/highscores.hsc");
		m_inputAdapter = new DesktopInputAdapter();
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
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		return viewport;
	}
	
	@Override
	public Vector2 getVirtualScreenSize() {
		m_virtualScreenSize.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		return m_virtualScreenSize;
	}
	
	@Override
	public FileHandle getAddonLevelPath() {
		return m_addonPath;
	}

	@Override
	public FileHandle getHighscoresPath() {
		return m_highscorePath;
	}

	@Override
	public InputAdapter getInputAdapter() {
		return m_inputAdapter;
	}

	@Override
	public void dispose() {
		m_inputAdapter.dispose();
	}
}
