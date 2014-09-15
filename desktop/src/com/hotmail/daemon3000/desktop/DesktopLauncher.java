package com.hotmail.daemon3000.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hotmail.daemon3000.SokobanGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		new LwjglApplication(new SokobanGame(), getSettings());
	}
	
	public static LwjglApplicationConfiguration getSettings() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		config.title = "Sokoban";
		config.resizable = false;
		config.fullscreen = false;
		
		return config;
	}
}
