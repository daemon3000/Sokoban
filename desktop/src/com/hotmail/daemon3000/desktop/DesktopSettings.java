package com.hotmail.daemon3000.desktop;

public class DesktopSettings {
	public int width;
	public int height;
	public String title;
	public String language;
	public boolean fullscreen;
	public boolean useGL30;
	public boolean vSyncEnabled;
	
	public DesktopSettings() {
		width = 800;
		height = 600;
		title = "Sokoban";
		language = "en";
		fullscreen = false;
		useGL30 = false;
		vSyncEnabled = false;
	}
}
