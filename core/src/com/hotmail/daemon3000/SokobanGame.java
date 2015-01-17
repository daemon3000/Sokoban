package com.hotmail.daemon3000;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;

public class SokobanGame extends Game {
	private PlatformSettings m_platformSettings;
	private I18NBundle m_stringBundle;
	private Music m_gameMusic;
	private HashMap<String, HighScore> m_highScores;
	
	public SokobanGame(PlatformSettings platformSettings) {
		m_platformSettings = platformSettings;
	}
	
	@Override
	public void create() {
		loadScores();
		
		Locale locale = new Locale(m_platformSettings.getLanguage());
		m_stringBundle = I18NBundle.createBundle(Gdx.files.internal("i18n/sokoban"), locale);
		
		m_gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music.ogg"));
		m_gameMusic.setVolume(0.4f);
		m_gameMusic.setLooping(true);
		m_gameMusic.play();
		
		setScene(new StartScene(this));
	}
	
	@Override
	public void dispose() {
		
		m_platformSettings.saveSettings();
		m_gameMusic.dispose();
		super.dispose();
	}
	
	public void exit() {
		saveScores();
		Gdx.app.exit();
	}
	
	public PlatformSettings getPlatformSettings() {
		return m_platformSettings;
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
	
	private void saveScores() {
		FileHandle file = m_platformSettings.getHighscoresPath();
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
		FileHandle file = m_platformSettings.getHighscoresPath();
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
