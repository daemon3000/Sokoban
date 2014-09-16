package com.hotmail.daemon3000;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

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
	
	public void saveScores() {
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
}
