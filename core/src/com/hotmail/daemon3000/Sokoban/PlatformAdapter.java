package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface PlatformAdapter {
	public void initialize();
	public void dispose();
	public boolean allowsAddonLevels();
	public FileHandle getAddonLevelPath();
	public FileHandle getHighscoresPath();
	public Viewport createViewport();
	public Viewport createViewport(Camera camera);
	public Vector2 getVirtualScreenSize();
	public InputAdapter getInputAdapter();
}
