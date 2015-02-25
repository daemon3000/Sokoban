package com.hotmail.daemon3000.Sokoban.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hotmail.daemon3000.Sokoban.ActionListener;
import com.hotmail.daemon3000.Sokoban.EventListener;
import com.hotmail.daemon3000.Sokoban.InputAdapter;
import com.hotmail.daemon3000.Sokoban.MoveDirection;

public class AndroidInputAdapter implements InputAdapter {
	private DelayedRemovalArray<EventListener<MoveDirection>> m_moveListeners;
	private DelayedRemovalArray<ActionListener> m_undoListeners;
	private DelayedRemovalArray<ActionListener> m_pauseListeners;
	private Stage m_stage;
	private Skin m_uiSkin;
	private Sound m_click;
	private Button m_pauseButton;
	private Button m_undoButton;
	private InputMultiplexer m_inputMultiplexer;
	private boolean m_enabled = true;
	
	public AndroidInputAdapter(Viewport viewport, Vector2 virtualScreenSize) {
		m_moveListeners = new DelayedRemovalArray<EventListener<MoveDirection>>();
		m_undoListeners = new DelayedRemovalArray<ActionListener>();
		m_pauseListeners = new DelayedRemovalArray<ActionListener>();
		m_stage = new Stage(viewport);
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		
		DirectionListener dirListener = new DirectionListener() {
			@Override
			public void onUp() {
				dispatchMoveEvent(MoveDirection.Up);
			}

			@Override
			public void onDown() {
				dispatchMoveEvent(MoveDirection.Down);
			}

			@Override
			public void onRight() {
				dispatchMoveEvent(MoveDirection.Right);
			}

			@Override
			public void onLeft() {
				dispatchMoveEvent(MoveDirection.Left);
			}
		};
		
		m_inputMultiplexer = new InputMultiplexer();
		m_inputMultiplexer.addProcessor(m_stage);
		m_inputMultiplexer.addProcessor(new DirectionGestureDetector(dirListener));
		
		createWidgets(virtualScreenSize);
	}
	
	private void createWidgets(Vector2 virtualScreenSize) {
		m_undoButton = new ImageButton(m_uiSkin, "default");
		m_undoButton.setPosition(20.0f, 20.0f);
		m_undoButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				m_undoListeners.begin();
				for(ActionListener l : m_undoListeners) {
					l.handle();
				}
				m_undoListeners.end();
		    }
		});
		Image undoIcon = new Image(m_uiSkin, "undo");
		m_undoButton.addActor(undoIcon);
		undoIcon.setOrigin(undoIcon.getWidth() / 2, undoIcon.getHeight() / 2);
		undoIcon.setPosition(m_undoButton.getWidth() / 2 - undoIcon.getWidth() / 2, 
								m_undoButton.getHeight() / 2 - undoIcon.getHeight() / 2);
		
		m_pauseButton = new ImageButton(m_uiSkin, "default");
		m_pauseButton.setPosition(virtualScreenSize.x - (m_pauseButton.getWidth() + 20.0f), 20.0f);
		m_pauseButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				m_pauseListeners.begin();
				for(ActionListener l : m_pauseListeners) {
					l.handle();
				}
				m_pauseListeners.end();
		    }
		});
		Image pauseIcon = new Image(m_uiSkin, "pause");
		m_pauseButton.addActor(pauseIcon);
		pauseIcon.setOrigin(pauseIcon.getWidth() / 2, pauseIcon.getHeight() / 2);
		pauseIcon.setPosition(m_pauseButton.getWidth() / 2 - pauseIcon.getWidth() / 2, 
								m_pauseButton.getHeight() / 2 - pauseIcon.getHeight() / 2);
		
		m_stage.addActor(m_undoButton);
		m_stage.addActor(m_pauseButton);
	}
	
	@Override
	public void update(float deltaTime) {
		if(m_enabled)
			m_stage.act(deltaTime);
	}
	
	@Override
	public void render() {
		if(m_enabled)
			m_stage.draw();
	}

	private void dispatchMoveEvent(MoveDirection dir) {
		m_moveListeners.begin();
		for(EventListener<MoveDirection> l : m_moveListeners) {
			l.handle(dir);
		}
		m_moveListeners.end();
	}

	@Override
	public void addMoveListener(EventListener<MoveDirection> listener) {
		if(!m_moveListeners.contains(listener, true))
			m_moveListeners.add(listener);
	}


	@Override
	public void removeMoveListener(EventListener<MoveDirection> listener) {
		m_moveListeners.removeValue(listener, true);
	}


	@Override
	public void addUndoListener(ActionListener listener) {
		if(!m_undoListeners.contains(listener, true))
			m_undoListeners.add(listener);
	}


	@Override
	public void removeUndoListener(ActionListener listener) {
		m_undoListeners.removeValue(listener, true);
	}


	@Override
	public void addPauseListener(ActionListener listener) {
		if(!m_pauseListeners.contains(listener, true))
			m_pauseListeners.add(listener);
	}


	@Override
	public void removePauseListener(ActionListener listener) {
		m_pauseListeners.removeValue(listener, true);
	}

	@Override
	public void reset() { }

	@Override
	public void enable() {
		m_enabled = true;
		Gdx.input.setInputProcessor(m_inputMultiplexer);
	}

	@Override
	public void disable() {
		m_enabled = false;
		Gdx.input.setInputProcessor(null);
	}
	
	@Override
	public void dispose() {
		m_stage.dispose();
		m_uiSkin.dispose();
		m_click.dispose();
	}

	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		m_stage.getViewport().update((int)screenSize.x, (int)screenSize.y, true);
		m_undoButton.setPosition(20.0f, 20.0f);
		m_pauseButton.setPosition(virtualScreenSize.x - (m_pauseButton.getWidth() + 20.0f), 20.0f);
	}
}

