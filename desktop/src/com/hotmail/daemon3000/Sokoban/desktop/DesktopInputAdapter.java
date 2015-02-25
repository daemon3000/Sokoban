package com.hotmail.daemon3000.Sokoban.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.hotmail.daemon3000.Sokoban.ActionListener;
import com.hotmail.daemon3000.Sokoban.EventListener;
import com.hotmail.daemon3000.Sokoban.InputAdapter;
import com.hotmail.daemon3000.Sokoban.MoveDirection;

public class DesktopInputAdapter implements InputAdapter {
	private DelayedRemovalArray<EventListener<MoveDirection>> m_moveListeners;
	private DelayedRemovalArray<ActionListener> m_undoListeners;
	private DelayedRemovalArray<ActionListener> m_pauseListeners;
	private boolean m_enabled = true;
	
	public DesktopInputAdapter() {
		m_moveListeners = new DelayedRemovalArray<EventListener<MoveDirection>>();
		m_undoListeners = new DelayedRemovalArray<ActionListener>();
		m_pauseListeners = new DelayedRemovalArray<ActionListener>();
	}
	
	@Override
	public void update(float deltaTime) {
		if(!m_enabled)
			return;
		
		if(Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
			dispatchMoveEvent(MoveDirection.Up);
		else if(Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S))
			dispatchMoveEvent(MoveDirection.Down);
		else if(Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A))
			dispatchMoveEvent(MoveDirection.Left);
		else if(Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D))
			dispatchMoveEvent(MoveDirection.Right);
		if(Gdx.input.isKeyJustPressed(Keys.Z)) {
			m_undoListeners.begin();
			for(ActionListener l : m_undoListeners) {
				l.handle();
			}
			m_undoListeners.end();
		}
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			m_pauseListeners.begin();
			for(ActionListener l : m_pauseListeners) {
				l.handle();
			}
			m_pauseListeners.end();
		}
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
	public void dispose() { }

	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) { }

	@Override
	public void enable() { 
		m_enabled = true;
	}

	@Override
	public void disable() { 
		m_enabled = false;
	}

	@Override
	public void render() { }
}
