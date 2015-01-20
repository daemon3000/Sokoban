package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class StartScene implements Scene {
	private ScreenManager m_screenManager;
	private Skin m_uiSkin;
	private Sound m_click;
	private SokobanGame m_game;
	
	public StartScene(SokobanGame game) {
		m_game = game;
	}
	
	@Override
	public void onLoad() {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		
		m_screenManager = new ScreenManager();
		m_screenManager.addScreen(new StartScreen(m_screenManager, m_game, m_uiSkin, m_click));
		m_screenManager.addScreen(new LevelSelectScreen(m_screenManager, m_game, m_uiSkin, m_click));
		m_screenManager.addScreen(new HelpScreen(m_screenManager, m_game, m_uiSkin, m_click));
		m_screenManager.addScreen(new CreditsScreen(m_screenManager, m_game, m_uiSkin, m_click));
		m_screenManager.addScreen(new OptionsScreen(m_screenManager, m_game, m_uiSkin, m_click));
		
		m_screenManager.changeScreen(ScreenID.Start);
	}
	
	@Override
	public void update(float delta) {
		m_screenManager.update(delta);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.45f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		m_screenManager.render();
	}

	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		m_screenManager.resize(screenSize, virtualScreenSize);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		m_screenManager.dispose();
		m_uiSkin.dispose();
		m_click.dispose();
	}
}
