package com.hotmail.daemon3000.desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.hotmail.daemon3000.SokobanGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopSettings settings = loadSettings();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = settings.width;
		config.height = settings.height;
		config.title = settings.title;
		config.resizable = false;
		config.fullscreen = settings.fullscreen;
		config.useGL30 = settings.useGL30;
		config.vSyncEnabled = settings.vSyncEnabled;
		config.addIcon("icon_big.png", FileType.Internal);
		config.addIcon("icon_medium.png", FileType.Internal);
		config.addIcon("icon_small.png", FileType.Internal);
		
		new LwjglApplication(new SokobanGame(settings.language), config);
	}
	
	private static DesktopSettings loadSettings() {
		DesktopSettings settings = null;
		try {
			FileInputStream stream = new FileInputStream("settings.json");
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(stream);
			settings = new DesktopSettings();
			settings.width = root.getInt("width", 800);
			settings.height = root.getInt("height", 600);
			settings.title = root.getString("title", "Sokoban");
			settings.fullscreen = root.getBoolean("fullscreen", false);
			settings.language = root.getString("language", "en");
			settings.useGL30 = root.getBoolean("useGL30", false);
			settings.vSyncEnabled = root.getBoolean("vSyncEnabled", false);
			
			stream.close();
		}
		catch(FileNotFoundException e) {
		} 
		catch(IOException e) {
		}
		finally {
			if(settings == null)
				settings = new DesktopSettings();
		}
		
		return settings;
	}
}
