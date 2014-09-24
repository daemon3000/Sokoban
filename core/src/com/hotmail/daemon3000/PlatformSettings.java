package com.hotmail.daemon3000;

import com.badlogic.gdx.files.FileHandle;

public interface PlatformSettings {
	boolean allowsOptionsScreen();
	boolean allowsAddonLevels();
	FileHandle getAddonLevelPath();
	FileHandle getHighscoresPath();
	int getWidth();
	int getHeight();
	boolean getFullscreen();
	String getTitle();
	String getLanguage();
	void changeResolution(int width, int height, boolean fullscreen);
	void setFullscreen(boolean enabled);
	void saveSettings();
}
