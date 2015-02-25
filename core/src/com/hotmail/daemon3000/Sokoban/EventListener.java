package com.hotmail.daemon3000.Sokoban;

public interface EventListener<T> {
	void handle(T arg);
}
