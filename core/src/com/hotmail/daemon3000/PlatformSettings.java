package com.hotmail.daemon3000;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface PlatformSettings {
	boolean allowsOptionsScreen();
	boolean allowsAddonLevels();
	boolean getFullscreen();
	FileHandle getAddonLevelPath();
	FileHandle getHighscoresPath();
	Viewport createViewport();
	Vector2 getScreenSize();
	Vector2 getVirtualScreenSize();
	String getTitle();
	String getLanguage();
	void changeResolution(int width, int height, boolean fullscreen);
	void setFullscreen(boolean enabled);
	void saveSettings();
}
