package org.andork.awt.anim;

public abstract interface Animation
{
	/**
	 * Advances the animation one frame.
	 * 
	 * @return if a value less than zero is returned, {@link AnimationQueue} will terminate this animation. Otherwise, it will attempt to call
	 *         {@link #animate()} again in that many milliseconds.
	 */
	public abstract long animate( long animTime );
}
