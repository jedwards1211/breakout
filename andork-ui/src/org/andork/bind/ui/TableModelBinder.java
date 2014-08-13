package org.andork.bind.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.andork.bind2.Binding;
import org.andork.bind2.CachingBinder;
import org.andork.bind2.Link;

public class TableModelBinder<T extends TableModel> extends CachingBinder<T> implements Binding, TableModelListener {
	public final Link<T>	modelLink	= new Link<T>(this);

	public void update(boolean force) {
		T oldModel = get();
		T newModel = modelLink.get();

		if (oldModel != newModel) {
			if (oldModel != null) {
				oldModel.removeTableModelListener(this);
			}
			if (newModel != null) {
				newModel.addTableModelListener(this);
			}
		}
		set(newModel, force);
	}

	public void tableChanged(TableModelEvent e) {
		update(true);
	}
}
