package com.hotmail.daemon3000.desktop;

public class DesktopSettings {
	public int width;
	public int height;
	public String title;
	public boolean fullscreen;
	public boolean useGL30;
	public boolean vSyncEnabled;
	
	public DesktopSettings() {
		width = 800;
		height = 480;
		title = "Sokoban";
		fullscreen = false;
		useGL30 = false;
		vSyncEnabled = false;
	}
}
