package org.andork.jogl.neu2;

import org.andork.jogl.Projection;
import org.andork.jogl.neu.JoglDrawContext;
import org.andork.math3d.PickXform;

public interface JoglCamera extends JoglDrawContext
{

	public abstract void update( int width , int height );

	public abstract void getViewXform( float[ ] out );

	public abstract void setViewXform( float[ ] v );

	public abstract PickXform pickXform( );

	public abstract void setProjection( Projection projection );

	public abstract Projection getProjection( );

}