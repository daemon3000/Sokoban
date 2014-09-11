package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class StatusPanel {
	private Texture m_panelTexture;
	private BitmapFont m_font;
	private Stage m_stage;
	private Label m_levelIndexLabel;
	private Label m_moveCountLabel;
	private Label m_undoCountLabel;
	private Label m_elapsedTimeLabel;
	private float m_elapsedTime;
	
	public StatusPanel() {
		m_panelTexture = new Texture(Gdx.files.internal("ui/img/panel.png"));
		m_font = new BitmapFont();
		m_stage = new Stage();
		m_elapsedTime = 0.0f;
		
		createTopPanel();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createTopPanel() {
		int screenHeight = Gdx.graphics.getHeight();
		int screenWidth = Gdx.graphics.getWidth();
		
		NinePatch panelPatch = new NinePatch(m_panelTexture, 20, 20, 20, 20);
		Image topPanelBg = new Image(panelPatch);
		topPanelBg.setWidth(screenWidth);
		topPanelBg.setHeight(30);
		topPanelBg.setPosition(0.0f, screenHeight - 30);
		
		LabelStyle style = new LabelStyle(m_font, Color.WHITE);
		m_levelIndexLabel = new Label("Level: 0/0", style);
		m_levelIndexLabel.setPosition(10.0f, screenHeight - 25.0f);
		
		m_elapsedTimeLabel = new Label("Time: 0.00", style);
		m_elapsedTimeLabel.setPosition(210.0f, screenHeight - 25.0f);
		
		m_moveCountLabel = new Label("Move Count: 0", style);
		m_moveCountLabel.setPosition(410.0f, screenHeight - 25.0f);
		
		m_undoCountLabel = new Label(String.format("Undo Count: 0/%d", Level.MAX_UNDO), style);
		m_undoCountLabel.setPosition(610.0f, screenHeight - 25.0f);
		
		Group topPanel = new Group();
		topPanel.addActor(topPanelBg);
		topPanel.addActor(m_levelIndexLabel);
		topPanel.addActor(m_elapsedTimeLabel);
		topPanel.addActor(m_moveCountLabel);
		topPanel.addActor(m_undoCountLabel);
		
		m_stage.addActor(topPanel);
	}
	
	public void act(float delta) {
		m_elapsedTime += delta;
		m_elapsedTimeLabel.setText(String.format("Time: %.2f", m_elapsedTime));
	}
	
	public void render() {
		m_stage.draw();
	}
	
	public void setLevelIndex(int levelIndex, int levelCount) {
		m_levelIndexLabel.setText(String.format("Level: %d/%d", levelIndex, levelCount));
	}
	
	public void setMoveCount(int moveCount) {
		m_moveCountLabel.setText(String.format("Move Count: %d", moveCount));
	}
	
	public void setUndoCount(int undoCount) {
		m_undoCountLabel.setText(String.format("Undo Count: %d/%d", undoCount, Level.MAX_UNDO));
	}
	
	public void reset() {
		m_levelIndexLabel.setText("Level: 0/0");
		m_moveCountLabel.setText("Move Count: 0");
		m_undoCountLabel.setText(String.format("Undo Count: 0/%d", Level.MAX_UNDO));
		
		m_elapsedTime = 0.0f;
		m_elapsedTimeLabel.setText(String.format("", m_elapsedTime));
	}
	
	public void dispose() {
		m_moveCountLabel = null;
		m_undoCountLabel = null;
		m_elapsedTimeLabel = null;
		m_stage.dispose();
		m_panelTexture.dispose();
		m_font.dispose();
	}
}
