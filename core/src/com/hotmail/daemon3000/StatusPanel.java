package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class StatusPanel {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Label m_levelIndexLabel;
	private Label m_moveCountLabel;
	private Label m_bestMoveCountLabel;
	private Label m_elapsedTimeLabel;
	private Label m_bestTimeLabel;
	private int m_lastElapsedTime = 0;

	public StatusPanel(Skin uiSkin) {
		m_uiSkin = uiSkin;
		m_stage = new Stage();

		createWidgets();
	}

	private void createWidgets() {
		m_window = new Window("", m_uiSkin, "panel");
		m_window.setMovable(false);
		m_window.setWidth(Gdx.graphics.getWidth());
		m_window.setHeight(50);
		m_window.setPosition(0.0f, Gdx.graphics.getHeight() - 30);

		m_levelIndexLabel = new Label("Level: 0/0", m_uiSkin, "default");
		m_window.addActor(m_levelIndexLabel);
		m_levelIndexLabel.setPosition(10.0f, m_window.getHeight() - m_levelIndexLabel.getHeight());

		m_moveCountLabel = new Label("Moves: 0", m_uiSkin, "default");
		m_window.addActor(m_moveCountLabel);
		m_moveCountLabel.setPosition(
				m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2,
				m_window.getHeight() - m_moveCountLabel.getHeight());
		
		m_bestMoveCountLabel = new Label("Best Moves: 0", m_uiSkin, "default");
		m_window.addActor(m_bestMoveCountLabel);
		m_bestMoveCountLabel.setPosition(m_window.getWidth() / 2 - m_bestMoveCountLabel.getWidth() / 2, 5.0f);

		m_elapsedTimeLabel = new Label("Time: 0", m_uiSkin, "default");
		m_window.addActor(m_elapsedTimeLabel);
		m_elapsedTimeLabel.setPosition(
				m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f,
				m_window.getHeight() - m_elapsedTimeLabel.getHeight());
		
		m_bestTimeLabel = new Label("Best Time: 0", m_uiSkin, "default");
		m_window.addActor(m_bestTimeLabel);
		m_bestTimeLabel.setPosition(m_window.getWidth() - m_bestTimeLabel.getWidth() - 10.0f, 5.0f);

		m_stage.addActor(m_window);
	}

	public void render() {
		m_stage.draw();
	}

	public void setLevelIndex(int levelIndex, int levelCount) {
		m_levelIndexLabel.setText("Level: " + levelIndex + "/" + levelCount);
	}

	public void setMoveCount(int moveCount) {
		m_moveCountLabel.setText("Moves: " + moveCount);
		m_moveCountLabel.pack();
		m_moveCountLabel.setX(m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2);
	}
	
	public void setBestMoves(int moveCount) {
		m_bestMoveCountLabel.setText("Best Moves: " + moveCount);
		m_bestMoveCountLabel.pack();
		m_bestMoveCountLabel.setX(m_window.getWidth() / 2 - m_bestMoveCountLabel.getWidth() / 2);
	}

	public void setElapsedTime(float elapsedTime) {
		if ((int) elapsedTime > m_lastElapsedTime) {
			m_lastElapsedTime = (int) elapsedTime;
			m_elapsedTimeLabel.setText("Time: " + m_lastElapsedTime);
			m_elapsedTimeLabel.pack();
			m_elapsedTimeLabel.setX(m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f);
		}
	}
	
	public void setBestTime(float time) {
		m_bestTimeLabel.setText("Best Time: " + (int)time);
		m_bestTimeLabel.pack();
		m_bestTimeLabel.setX(m_window.getWidth() - m_bestTimeLabel.getWidth() - 10.0f);
	}

	public void reset() {
		m_levelIndexLabel.setText("Level: 0/0");
		
		m_moveCountLabel.setText("Moves: 0");
		m_moveCountLabel.pack();
		m_moveCountLabel.setX(m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2);
		
		m_bestMoveCountLabel.setText("Best Moves: 0");
		m_bestMoveCountLabel.pack();
		m_bestMoveCountLabel.setX(m_window.getWidth() / 2 - m_bestMoveCountLabel.getWidth() / 2);
		
		m_elapsedTimeLabel.setText("Time: 0");
		m_elapsedTimeLabel.pack();
		m_elapsedTimeLabel.setX(m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f);
		
		m_bestTimeLabel.setText("Best Time: 0");
		m_bestTimeLabel.pack();
		m_bestTimeLabel.setX(m_window.getWidth() - m_bestTimeLabel.getWidth() - 10.0f);
	}

	public void dispose() {
		m_window = null;
		m_moveCountLabel = null;
		m_elapsedTimeLabel = null;
		m_levelIndexLabel = null;
		m_stage.dispose();
	}
}
