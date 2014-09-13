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
	private Label m_elapsedTimeLabel;
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
		m_levelIndexLabel.setPosition(10.0f, m_window.getHeight() / 2
				- m_levelIndexLabel.getHeight() / 2);

		m_moveCountLabel = new Label("Move Count: 0", m_uiSkin, "default");
		m_window.addActor(m_moveCountLabel);
		m_moveCountLabel.setPosition(
				m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2,
				m_window.getHeight() / 2 - m_moveCountLabel.getHeight() / 2);

		m_elapsedTimeLabel = new Label("Time: 0", m_uiSkin, "default");
		m_window.addActor(m_elapsedTimeLabel);
		m_elapsedTimeLabel.setPosition(
				m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f,
				m_window.getHeight() / 2 - m_elapsedTimeLabel.getHeight() / 2);

		m_stage.addActor(m_window);
	}

	public void render() {
		m_stage.draw();
	}

	public void setLevelIndex(int levelIndex, int levelCount) {
		m_levelIndexLabel.setText("Level: " + levelIndex + "/" + levelCount);
	}

	public void setMoveCount(int moveCount) {
		m_moveCountLabel.setText("Move Count: " + moveCount);
		m_moveCountLabel.pack();
		m_moveCountLabel.setX(m_window.getWidth() / 2
				- m_moveCountLabel.getWidth() / 2);
	}

	public void setElapsedTime(float elapsedTime) {
		m_elapsedTimeLabel.setText("Time: " + (int) elapsedTime);
		if ((int) elapsedTime > m_lastElapsedTime) {
			m_lastElapsedTime = (int) elapsedTime;
			m_elapsedTimeLabel.pack();
			m_elapsedTimeLabel.setX(m_window.getWidth()
					- m_elapsedTimeLabel.getWidth() - 10.0f);
		}
	}

	public void reset() {
		m_levelIndexLabel.setText("Level: 0/0");
		m_moveCountLabel.setText("Move Count: 0");
		m_moveCountLabel.pack();
		m_moveCountLabel.setX(m_window.getWidth() / 2
				- m_moveCountLabel.getWidth() / 2);
		m_elapsedTimeLabel.setText("Time: 0");
		m_elapsedTimeLabel.pack();
		m_elapsedTimeLabel.setX(m_window.getWidth()
				- m_elapsedTimeLabel.getWidth() - 10.0f);
	}

	public void dispose() {
		m_window = null;
		m_moveCountLabel = null;
		m_elapsedTimeLabel = null;
		m_levelIndexLabel = null;
		m_stage.dispose();
	}
}
