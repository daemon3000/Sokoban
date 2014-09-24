package com.hotmail.daemon3000;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.JsonValue.ValueType;

public class SokobanGame extends Game {
	private String m_language;
	private I18NBundle m_stringBundle;
	private Music m_gameMusic;
	private HashMap<String, HashMap<Integer, GameScore>> m_highScores;
	
	public SokobanGame(String language) {
		m_language = language;
	}
	
	@Override
	public void create() {
		loadScores();
		
		Locale locale = new Locale(m_language);
		m_stringBundle = I18NBundle.createBundle(Gdx.files.internal("i18n/sokoban"), locale);
		
		m_gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music.ogg"));
		m_gameMusic.setVolume(0.4f);
		m_gameMusic.setLooping(true);
		m_gameMusic.play();
		
		setScreen(new StartScreen(this));
	}
	
	@Override
	public void dispose() {
		saveScores();
		saveOptions();
		m_gameMusic.dispose();
		super.dispose();
	}
	
	public I18NBundle getStringBundle() {
		return m_stringBundle;
	}
	
	public GameScore getScore(LevelPack levelPack, int levelIndex) {
		HashMap<Integer, GameScore> scores = m_highScores.get(levelPack.getID());
		if(scores != null) {
			return scores.get(levelIndex);
		}
		else {
			return null;
		}
	}
	
	public void setScore(LevelPack levelPack, int levelIndex, int moves, float time) {
		HashMap<Integer, GameScore> scores = m_highScores.get(levelPack.getID());
		if(scores != null) {
			GameScore score = scores.get(levelIndex);
			if(score != null) {
				score.bestMoves = moves;
				score.bestTime = time;
			}
			else {
				score = new GameScore(moves, time);
				scores.put(levelIndex, score);
			}
		}
		else {
			scores = new HashMap<Integer, GameScore>();
			GameScore score = new GameScore(moves, time);
			
			scores.put(levelIndex, score);
			m_highScores.put(levelPack.getID(), scores);
		}
	}
	
	private void saveScores() {
		FileHandle file = Gdx.files.local("profile/highscores.hsc");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objStream = new ObjectOutputStream(stream);
			objStream.writeObject(m_highScores);
			
			file.writeBytes(stream.toByteArray(), false);
			objStream.close();
		}
		catch(IOException ex) {
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadScores() {
		FileHandle file = Gdx.files.local("profile/highscores.hsc");
		if(file.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(file.read());
				m_highScores = (HashMap<String, HashMap<Integer, GameScore>>)stream.readObject();
			}
			catch(ClassNotFoundException e1) {
				m_highScores = new HashMap<String, HashMap<Integer, GameScore>>();
			}
			catch(IOException e2) {
				m_highScores = new HashMap<String, HashMap<Integer, GameScore>>();
			}
		}
		else {
			m_highScores = new HashMap<String, HashMap<Integer, GameScore>>();
		}
	}
	
	private void saveOptions() {
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
	}
}
