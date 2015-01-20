package com.hotmail.daemon3000.desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.*;
import com.hotmail.daemon3000.PlatformSettings;

public class DesktopSettings implements PlatformSettings {
	private static final int VIRTUAL_WIDTH = 800;
	private static final int VIRTUAL_HEIGHT = 600;
	
	private int m_width;
	private int m_height;
	private String m_title;
	private String m_language;
	private boolean m_fullscreen;
	private boolean m_isDirty;
	
	public DesktopSettings() {
		if(!tryLoadSettings()) {
			m_width = VIRTUAL_WIDTH;
			m_height = VIRTUAL_HEIGHT;
			m_title = "Sokoban";
			m_fullscreen = false;
			m_language = "en";
			m_isDirty = true;
		}
		m_isDirty = false;
	}
	
	private boolean tryLoadSettings() {
		try {
			FileInputStream stream = new FileInputStream("settings.json");
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(stream);
			m_width = root.getInt("width", VIRTUAL_WIDTH);
			m_height = root.getInt("height", VIRTUAL_HEIGHT);
			m_title = root.getString("title", "Sokoban");
			m_fullscreen = root.getBoolean("fullscreen", false);
			m_language = root.getString("language", "en");
			
			stream.close();
			
			return true;
		}
		catch(FileNotFoundException e) {
		} 
		catch(IOException e) {
		}
		
		return false;
	}
	
	@Override
	public boolean allowsOptionsScreen() {
		return true;
	}
	
	@Override
	public boolean allowsAddonLevels() {
		return true;
	}
	
	@Override
	public Viewport createViewport() {
//		return new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		return new ScreenViewport();
	}
	
	@Override
	public Viewport createViewport(Camera camera) {
//		FitViewport viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		ScreenViewport viewport = new ScreenViewport(camera);
		viewport.update(m_width, m_height, true);
		
		return viewport;
	}
	
	@Override
	public Vector2 getScreenSize() {
		return new Vector2(m_width, m_height);
	}
	
	@Override
	public Vector2 getVirtualScreenSize() {
//		return new Vector2(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		return new Vector2(m_width, m_height);
	}
	
	@Override
	public boolean getFullscreen() {
		return m_fullscreen;
	}
	
	@Override
	public String getTitle() {
		return m_title;
	}
	
	@Override
	public String getLanguage() {
		return m_language;
	}
	
	@Override
	public void changeResolution(int width, int height, boolean fullscreen) {
		m_width = width;
		m_height = height;
		m_fullscreen = fullscreen;
		m_isDirty = true;
		Gdx.graphics.setDisplayMode(width, height, fullscreen);
	}
	
	@Override
	public void setFullscreen(boolean enabled) {
		m_fullscreen = enabled;
		m_isDirty = true;
		Gdx.graphics.setDisplayMode(m_width, m_height, enabled);
	}
	
	@Override
	public void saveSettings() {
		if(!m_isDirty) return;
		
		FileHandle fileHandle = Gdx.files.local("settings.json");
		JsonValue root = null;
		
		if(fileHandle.exists()) {
			JsonReader reader = new JsonReader();
			root = reader.parse(fileHandle);
			
			JsonValue width = root.get("width");
			if(width != null) {
				width.set(Gdx.graphics.getWidth());
			}
			else {
				width = new JsonValue(Gdx.graphics.getWidth());
				width.setName("width");
				if(root.child != null) {
					root.child.setNext(width);
					width.setPrev(root.child);
				}
				else {
					root.child = width;
				}
			}
			
			JsonValue height = root.get("height");
			if(height != null) {
				height.set(Gdx.graphics.getHeight());
			}
			else {
				height = new JsonValue(Gdx.graphics.getHeight());
				height.setName("height");
				width.setNext(height);
				height.setPrev(width);
			}
			
			JsonValue fullscreen = root.get("fullscreen");
			if(fullscreen != null) {
				fullscreen.set(Gdx.graphics.isFullscreen());
			}
			else {
				fullscreen = new JsonValue(Gdx.graphics.isFullscreen());
				fullscreen.setName("fullscreen");
				height.setNext(fullscreen);
				fullscreen.setPrev(height);
			}
		}
		else {
			JsonValue width = new JsonValue(Gdx.graphics.getWidth());
			width.setName("width");
			JsonValue height = new JsonValue(Gdx.graphics.getHeight());
			height.setName("height");
			JsonValue fullscreen = new JsonValue(Gdx.graphics.isFullscreen());
			fullscreen.setName("fullscreen");
			
			root = new JsonValue(ValueType.object);
			root.child = width;
			width.setNext(height);
			height.setPrev(width);
			height.setNext(fullscreen);
			fullscreen.setPrev(height);
		}
		
		fileHandle.writeString(root.prettyPrint(JsonWriter.OutputType.javascript, 0), false);
		m_isDirty = false;
	}

	@Override
	public FileHandle getAddonLevelPath() {
		return Gdx.files.local("addons/levels/index.json");
	}

	@Override
	public FileHandle getHighscoresPath() {
		return Gdx.files.local("profile/highscores.hsc");
	}
}
