package com.hotmail.daemon3000;

import com.badlogic.gdx.utils.Pool.Poolable;

public class UndoStep implements Poolable {
	public int deltaX;
	public int deltaY;
	public boolean movedCrate;
	
	public UndoStep() {
		reset();
	}
	
	public void init(int deltaX, int deltaY, boolean moveCrate) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.movedCrate = moveCrate;
	}
	
	@Override
	public void reset() {
		deltaX = 0;
		deltaY = 0;
		movedCrate = false;
	}
}
