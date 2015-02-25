package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.math.Vector2;

public interface InputAdapter {
	public void addMoveListener(EventListener<MoveDirection> listener);
	public void removeMoveListener(EventListener<MoveDirection> listener);
	public void addUndoListener(ActionListener listener);
	public void removeUndoListener(ActionListener listener);
	public void addPauseListener(ActionListener listener);
	public void removePauseListener(ActionListener listener);
	public void update(float deltaTime);
	public void render();
	public void enable();
	public void disable();
	public void reset();
	public void dispose();
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize);
}
