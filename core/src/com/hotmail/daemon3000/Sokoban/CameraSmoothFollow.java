package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraSmoothFollow {
	private Camera m_camera;
	private Vector3 m_targetPos;
	private float m_followSpeed;
	private float m_maxDistance;
	private boolean m_isFollowing;
	
	private final float MIN_DISTANCE = 5.0f;
	
	
	public CameraSmoothFollow(Camera camera, float followSpeed, float maxDistance) {
		m_camera = camera;
		m_targetPos = new Vector3();
		m_followSpeed = followSpeed;
		m_maxDistance = maxDistance;
	}
	
	public void update(Vector2 playerPos, float deltaTime) {
		update(playerPos.x, playerPos.y, deltaTime);
	}
	
	public void update(float playerX, float playerY, float deltaTime) {
		m_targetPos.set(playerX, playerY, 0.0f);
		
		if(m_isFollowing) {
			m_camera.position.lerp(m_targetPos, m_followSpeed * deltaTime);
			m_isFollowing = !isCloseEnoughToPlayer(m_targetPos);
		}
		else {
			m_isFollowing = isTooFarFromPlayer(m_targetPos);
		}
		
		m_camera.update();
	}
	
	private boolean isTooFarFromPlayer(Vector3 playerPos) {
		return m_camera.position.dst2(playerPos) > (m_maxDistance * m_maxDistance);
	}
	
	private boolean isCloseEnoughToPlayer(Vector3 playerPos) {
		return m_camera.position.dst2(playerPos) <= (MIN_DISTANCE * MIN_DISTANCE);
	}
	
	public void moveTo(Vector2 playerPos) {
		moveTo(playerPos.x, playerPos.y);
	}
	
	public void moveTo(float playerX, float playerY) {
		m_camera.position.set(playerX, playerY, 0.0f);
	}
	
	public Camera getCamera() {
		return m_camera;
	}
}
