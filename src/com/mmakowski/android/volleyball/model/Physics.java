package com.mmakowski.android.volleyball.model;

/**
 * Constants and routines for game physics.
 * 
 * @author mmakowski
 */
public final class Physics {
	public static final float GRAVITY = 80f;
	// 1 = ideally elastic
	public static final float BALL_REBOUND_FACTOR = 0.8f;
	private static final float AERODYNAMIC_DRAG_COEFFICIENT = 0.002f;
	
	// aerodynamic drag is proportional to v^2
	public static final float aerodynamicDragDeceleration(float velocity) {
		float aeroDragDec = (velocity > 0 ? -1 : 1) * AERODYNAMIC_DRAG_COEFFICIENT * velocity * velocity;
		return (Math.abs(aeroDragDec) > Math.abs(velocity)) ? -velocity : aeroDragDec;
	}
}
