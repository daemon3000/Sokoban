package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class StatusPanel {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Label m_levelIndexLabel;
	private Label m_moveCountLabel;
	private Label m_undoCountLabel;
	private Label m_elapsedTimeLabel;
	
	public StatusPanel() {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		
		createWidgets();
	}
	
	private void createWidgets() {
		Window window = new Window("", m_uiSkin, "panel");
		window.setMovable(false);
		window.setWidth(Gdx.graphics.getWidth());
		window.setHeight(30);
		window.setPosition(0.0f, Gdx.graphics.getHeight() - 30);
		
		m_levelIndexLabel = new Label("Level: 0/0", m_uiSkin, "default");
		window.addActor(m_levelIndexLabel);
		m_levelIndexLabel.setPosition(10.0f, window.getHeight() / 2 - m_levelIndexLabel.getHeight() / 2);
		
		m_elapsedTimeLabel = new Label("Time: 0.00", m_uiSkin, "default");
		window.addActor(m_elapsedTimeLabel);
		m_elapsedTimeLabel.setPosition(210.0f, window.getHeight() / 2 - m_elapsedTimeLabel.getHeight() / 2);
		
		m_moveCountLabel = new Label("Move Count: 0", m_uiSkin, "default");
		window.addActor(m_moveCountLabel);
		m_moveCountLabel.setPosition(410.0f, window.getHeight() / 2 - m_moveCountLabel.getHeight() / 2);
		
		m_undoCountLabel = new Label(String.format("Undo Count: 0/%d", Level.MAX_UNDO), m_uiSkin, "default");
		window.addActor(m_undoCountLabel);
		m_undoCountLabel.setPosition(610.0f, window.getHeight() / 2 - m_undoCountLabel.getHeight() / 2);
		
		m_stage.addActor(window);
	}
	
	public void render() {
		m_stage.draw();
	}
	
	public void setLevelIndex(int levelIndex, int levelCount) {
		m_levelIndexLabel.setText(String.format("Level: %d/%d", levelIndex, levelCount));
	}
	
	public void setElapsedTime(float elapsedTime) {
		m_elapsedTimeLabel.setText(String.format("Time: %.2f", elapsedTime));
	}
	
	public void setMoveCount(int moveCount) {
		m_moveCountLabel.setText(String.format("Move Count: %d", moveCount));
	}
	
	public void setUndoCount(int undoCount) {
		m_undoCountLabel.setText(String.format("Undo Count: %d/%d", undoCount, Level.MAX_UNDO));
	}
	
	public void reset() {
		m_levelIndexLabel.setText("Level: 0/0");
		m_elapsedTimeLabel.setText("Time: 0.00");
		m_moveCountLabel.setText("Move Count: 0");
		m_undoCountLabel.setText(String.format("Undo Count: 0/%d", Level.MAX_UNDO));
	}
	
	public void dispose() {
		m_moveCountLabel = null;
		m_undoCountLabel = null;
		m_elapsedTimeLabel = null;
		m_uiSkin.dispose();
		m_stage.dispose();
	}
}
