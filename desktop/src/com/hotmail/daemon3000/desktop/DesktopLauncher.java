package com.hotmail.daemon3000.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.hotmail.daemon3000.SokobanGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopSettings settings = new DesktopSettings();
		Vector2 screenSize = settings.getScreenSize();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int)screenSize.x;
		config.height = (int)screenSize.y;
		config.title = settings.getTitle();
		config.resizable = false;
		config.fullscreen = settings.getFullscreen();
		config.useGL30 = false;
		config.vSyncEnabled = false;
		config.addIcon("icon_big.png", FileType.Internal);
		config.addIcon("icon_medium.png", FileType.Internal);
		config.addIcon("icon_small.png", FileType.Internal);
		
		new LwjglApplication(new SokobanGame(settings), config);
	}
}
