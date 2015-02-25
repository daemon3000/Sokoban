package com.hotmail.daemon3000.Sokoban.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import com.hotmail.daemon3000.Sokoban.InputAdapter;
import com.hotmail.daemon3000.Sokoban.PlatformAdapter;

public class AndroidSettings implements PlatformAdapter {
	private Vector2 m_virtualScreenSize;
	private FileHandle m_highscorePath;
	private AndroidInputAdapter m_inputAdapter;
	
	@Override
	public void initialize() {
		m_virtualScreenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		m_highscorePath = Gdx.files.local("profile/highscores.hsc");
		m_inputAdapter = new AndroidInputAdapter(createViewport(), m_virtualScreenSize);
	}
	
	@Override
	public boolean allowsAddonLevels() {
		return false;
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
		return null;
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

