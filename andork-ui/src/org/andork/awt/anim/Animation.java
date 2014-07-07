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
	
	public default Animation also( Animation simultaneous )
	{
		return animTime -> {
			long myResult = animate( animTime );
			long simultaneousResult = simultaneous.animate( animTime );
			
			if( myResult > 0 )
			{
				if( simultaneousResult > 0 )
				{
					return Math.min( myResult , simultaneousResult );
				}
				return myResult;
			}
			return simultaneousResult;
		};
	}
}
