package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.I18NBundle;

public class StatusPanel {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Label m_levelIndexLabel;
	private Label m_moveCountLabel;
	private Label m_bestMoveCountLabel;
	private Label m_elapsedTimeLabel;
	private Label m_bestTimeLabel;
	private I18NBundle m_stringBundle;
	private int m_lastElapsedTime = 0;

	public StatusPanel(Skin uiSkin, I18NBundle stringBundle) {
		m_uiSkin = uiSkin;
		m_stage = new Stage();
		m_stringBundle = stringBundle;

		createWidgets();
	}

	private void createWidgets() {
		m_window = new Window("", m_uiSkin, "panel");
		m_window.setMovable(false);
		m_window.setWidth(Gdx.graphics.getWidth());
		m_window.setHeight(50);
		m_window.setPosition(0.0f, Gdx.graphics.getHeight() - 30);

		m_levelIndexLabel = new Label(m_stringBundle.format("level_index_label", 0, 0), m_uiSkin, "default");
		m_window.addActor(m_levelIndexLabel);
		m_levelIndexLabel.setPosition(10.0f, m_window.getHeight() - m_levelIndexLabel.getHeight());

		m_moveCountLabel = new Label(m_stringBundle.format("move_count_label", 0), m_uiSkin, "default");
		m_window.addActor(m_moveCountLabel);
		m_moveCountLabel.setPosition(
				m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2,
				m_window.getHeight() - m_moveCountLabel.getHeight());
		
		m_bestMoveCountLabel = new Label(m_stringBundle.format("best_moves_label", 0), m_uiSkin, "default");
		m_window.addActor(m_bestMoveCountLabel);
		m_bestMoveCountLabel.setPosition(m_window.getWidth() / 2 - m_bestMoveCountLabel.getWidth() / 2, 5.0f);

		m_elapsedTimeLabel = new Label(m_stringBundle.format("time_label", 0), m_uiSkin, "default");
		m_window.addActor(m_elapsedTimeLabel);
		m_elapsedTimeLabel.setPosition(
				m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f,
				m_window.getHeight() - m_elapsedTimeLabel.getHeight());
		
		m_bestTimeLabel = new Label(m_stringBundle.format("best_time_label", 0), m_uiSkin, "default");
		m_window.addActor(m_bestTimeLabel);
		m_bestTimeLabel.setPosition(m_window.getWidth() - m_bestTimeLabel.getWidth() - 10.0f, 5.0f);

		m_stage.addActor(m_window);
	}

	public void render() {
		m_stage.draw();
	}

	public void setLevelIndex(int levelIndex, int levelCount) {
		m_levelIndexLabel.setText(m_stringBundle.format("level_index_label", levelIndex, levelCount));
	}

	public void setMoveCount(int moveCount) {
		m_moveCountLabel.setText(m_stringBundle.format("move_count_label", moveCount));
		m_moveCountLabel.pack();
		m_moveCountLabel.setX(m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2);
	}
	
	public void setBestMoves(int moveCount) {
		m_bestMoveCountLabel.setText(m_stringBundle.format("best_moves_label", moveCount));
		m_bestMoveCountLabel.pack();
		m_bestMoveCountLabel.setX(m_window.getWidth() / 2 - m_bestMoveCountLabel.getWidth() / 2);
	}

	public void setElapsedTime(float elapsedTime) {
		if ((int)elapsedTime > m_lastElapsedTime) {
			m_lastElapsedTime = (int)elapsedTime;
			m_elapsedTimeLabel.setText(m_stringBundle.format("time_label", m_lastElapsedTime));
			m_elapsedTimeLabel.pack();
			m_elapsedTimeLabel.setX(m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f);
		}
	}
	
	public void setBestTime(float time) {
		m_bestTimeLabel.setText(m_stringBundle.format("best_time_label", (int)time));
		m_bestTimeLabel.pack();
		m_bestTimeLabel.setX(m_window.getWidth() - m_bestTimeLabel.getWidth() - 10.0f);
	}

	public void reset() {
		m_levelIndexLabel.setText(m_stringBundle.format("level_index_label", 0, 0));
		
		m_moveCountLabel.setText(m_stringBundle.format("move_count_label", 0));
		m_moveCountLabel.pack();
		m_moveCountLabel.setX(m_window.getWidth() / 2 - m_moveCountLabel.getWidth() / 2);
		
		m_bestMoveCountLabel.setText(m_stringBundle.format("best_moves_label", 0));
		m_bestMoveCountLabel.pack();
		m_bestMoveCountLabel.setX(m_window.getWidth() / 2 - m_bestMoveCountLabel.getWidth() / 2);
		
		m_elapsedTimeLabel.setText(m_stringBundle.format("time_label", 0));
		m_elapsedTimeLabel.pack();
		m_elapsedTimeLabel.setX(m_window.getWidth() - m_elapsedTimeLabel.getWidth() - 10.0f);
		
		m_bestTimeLabel.setText(m_stringBundle.format("best_time_label", 0));
		m_bestTimeLabel.pack();
		m_bestTimeLabel.setX(m_window.getWidth() - m_bestTimeLabel.getWidth() - 10.0f);
		
		m_lastElapsedTime = 0;
	}

	public void dispose() {
		m_stringBundle = null;
		m_window = null;
		m_moveCountLabel = null;
		m_elapsedTimeLabel = null;
		m_levelIndexLabel = null;
		m_stage.dispose();
	}
}
