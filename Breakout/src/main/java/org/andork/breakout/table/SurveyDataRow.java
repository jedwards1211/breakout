package org.andork.breakout.table;

import org.andork.util.PowerCloneable;

public abstract class SurveyDataRow implements PowerCloneable
{
	public abstract Object[ ] getCustom( );

	public abstract void setCustom( Object[ ] newCustom );

	public abstract boolean isEmpty( );
}
