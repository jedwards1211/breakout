package org.andork.swing.async;

public interface SubtaskStreamBimapperFactory<M, S extends SubtaskStreamBimapper<M>>
{
	public S createSubtaskStreamBimapper( Subtask subtask );
}
