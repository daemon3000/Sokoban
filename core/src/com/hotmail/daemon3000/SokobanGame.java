package com.hotmail.daemon3000;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class SokobanGame extends Game {
	Music m_gameMusic;
	
	@Override
	public void create() {
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
}
