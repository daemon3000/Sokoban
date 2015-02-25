package com.hotmail.daemon3000.Sokoban;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;

public class SokobanGame extends Game {
	private Music m_gameMusic;
	private HashMap<String, HighScore> m_highScores;
	private I18NBundle m_stringBundle;
	
	public SokobanGame(PlatformAdapter platformSettings) {
		super(platformSettings);
	}
	
	@Override
	public void create() {
		super.create();
		
		Preferences prefs = getPreferences();
		Locale locale = new Locale(prefs.getString("language", "en"));
		m_stringBundle = I18NBundle.createBundle(Gdx.files.internal("i18n/sokoban"), locale);
		
		m_gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music.ogg"));
		m_gameMusic.setVolume(prefs.getFloat("music_volume", 0.4f));
		m_gameMusic.setLooping(true);
		
		boolean playMusic = prefs.getBoolean("music_on", true);
		if(playMusic)
			m_gameMusic.play();
		
		if(Gdx.app.getType() == ApplicationType.Desktop) {
			int screenWidth = prefs.getInteger("window_width", 800);
			int screenHeight = prefs.getInteger("window_height", 600);
			boolean fullscreen = prefs.getBoolean("fullscreen", false);
			Gdx.graphics.setDisplayMode(screenWidth, screenHeight, fullscreen);
		}
		
		loadHighscores();
		setScene(new StartScene(this));
	}
	
	@Override
	public void dispose() {
		m_gameMusic.dispose();
		super.dispose();
	}
	
	@Override
	public void exit() {
		saveHighscores();
		super.exit();
	}
	
	public boolean isMusicPlaying() {
		return m_gameMusic.isPlaying();
	}
	
	public void playMusic() {
		m_gameMusic.play();
	}
	
	public void stopMusic() {
		m_gameMusic.stop();
	}
	
	public I18NBundle getStringBundle() {
		return m_stringBundle;
	}
	
	public HighScore getScore(LevelPack levelPack) {
		return m_highScores.get(levelPack.getID());
	}
	
	public void setScore(LevelPack levelPack, int levelIndex, int moves, float time) {
		HighScore score = m_highScores.get(levelPack.getID());
		if(score != null) {
			if(levelIndex >= 0 && levelIndex < score.bestMoves.length) {
				score.bestMoves[levelIndex] = moves;
				score.bestTime[levelIndex] = time;
			}
		}
		else {
			score = new HighScore(levelPack.getLevelCount());
			score.bestMoves[levelIndex] = moves;
			score.bestTime[levelIndex] = time;
			m_highScores.put(levelPack.getID(), score);
		}
	}
	
	private void saveHighscores() {
		FileHandle file = getPlatformAdapter().getHighscoresPath();
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
	private void loadHighscores() {
		FileHandle file = getPlatformAdapter().getHighscoresPath();
		if(file.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(file.read());
				m_highScores = (HashMap<String, HighScore>)stream.readObject();
			}
			catch(ClassNotFoundException e1) {
				m_highScores = new HashMap<String, HighScore>();
			}
			catch(IOException e2) {
				m_highScores = new HashMap<String, HighScore>();
			}
		}
		else {
			m_highScores = new HashMap<String, HighScore>();
		}
	}
}
