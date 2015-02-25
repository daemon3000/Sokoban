package com.hotmail.daemon3000.Sokoban.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hotmail.daemon3000.Sokoban.SokobanGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopSettings settings = new DesktopSettings();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Sokoban";
		config.resizable = false;
		config.useGL30 = false;
		config.vSyncEnabled = false;
		config.addIcon("icon_big.png", FileType.Internal);
		config.addIcon("icon_medium.png", FileType.Internal);
		config.addIcon("icon_small.png", FileType.Internal);
		
		new LwjglApplication(new SokobanGame(settings), config);
	}
}
