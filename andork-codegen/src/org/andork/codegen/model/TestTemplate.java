package org.andork.codegen.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.schwab.att.core.event.HierarchicalBasicPropertyChangeListener;
import com.schwab.att.core.event.HierarchicalBasicPropertyChangePropagator;
import com.schwab.att.core.event.HierarchicalBasicPropertyChangeSupport;
import com.schwab.att.core.model.gen.ModelTemplate;
import com.schwab.att.core.util.Java16PlusMethods;
import com.schwab.codegen.Generated;
import com.schwab.codegen.builder.BuilderElementName;
import com.schwab.codegen.builder.BuilderIgnore;

@ModelTemplate
public class TestTemplate {
	public static void main(String[] args) {
		TestTemplate template = new TestTemplate();

		template.getPropertyChangeSupport().addPropertyChangeListener(new HierarchicalBasicPropertyChangeListener() {
			@Override
			public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
				System.out.println("propertyChange(" + source + ", " + property + ", " + oldValue + ", " + newValue + ")");
			}

			@Override
			public void childrenChanged(Object source, ChangeType changeType, Object child) {
				System.out.println("childrenChanged(" + source + ", " + changeType + ", " + child + ")");
			}
		});

		ATemplate a = new ATemplate();
		template.addTemplate(a);
		a.setX(5);

		template.removeTemplate(a);
		a.setX(10);
	}

	@Generated
	public int getHello() {
		return hello;
	}

	public static enum Properties {
		TEMPLATES
	}

	@BuilderElementName(singular = "template", plural = "templates")
	List<TestTemplate.ATemplate>					templates				= new ArrayList<TestTemplate.ATemplate>();

	int												hello;

	@BuilderIgnore
	private HierarchicalBasicPropertyChangeSupport	propertyChangeSupport	= new HierarchicalBasicPropertyChangeSupport();

	@BuilderIgnore
	private HierarchicalBasicPropertyChangeListener	childChangeListener		= new HierarchicalBasicPropertyChangePropagator(this, propertyChangeSupport);

	public HierarchicalBasicPropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public List<TestTemplate.ATemplate> getTemplates() {
		return Collections.unmodifiableList(templates);
	}

	public void setTemplates(Collection<? extends TestTemplate.ATemplate> templates) {
		for (TestTemplate.ATemplate a : this.templates) {
			a.getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);
		}
		this.templates.clear();
		this.templates.addAll(templates);
		for (TestTemplate.ATemplate a : templates) {
			a.getPropertyChangeSupport().addPropertyChangeListener(childChangeListener);
		}
		propertyChangeSupport.fireChildrenChanged(this);
	}

	public void addTemplate(TestTemplate.ATemplate template) {
		if (this.templates.add(template)) {
			template.getPropertyChangeSupport().addPropertyChangeListener(childChangeListener);
			propertyChangeSupport.fireChildAdded(this, template);
		}
	}

	public void removeTemplate(TestTemplate.ATemplate template) {
		if (this.templates.remove(template)) {
			template.getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);
			propertyChangeSupport.fireChildRemoved(this, template);
		}
	}

	@ModelTemplate
	public static class ATemplate {
		public static enum Properties {
			X,
			Y
		}

		int												x;
		int												y;

		@BuilderIgnore
		private HierarchicalBasicPropertyChangeSupport	propertyChangeSupport	= new HierarchicalBasicPropertyChangeSupport();

		@BuilderIgnore
		private HierarchicalBasicPropertyChangeListener	childChangeListener		= new HierarchicalBasicPropertyChangePropagator(this, propertyChangeSupport);

		public HierarchicalBasicPropertyChangeSupport getPropertyChangeSupport() {
			return propertyChangeSupport;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			if (!Java16PlusMethods.equals(this.x, x)) {
				int old = this.x;
				this.x = x;
				propertyChangeSupport.firePropertyChange(this, Properties.X, old, x);
			}
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			if (!Java16PlusMethods.equals(this.y, y)) {
				int old = this.y;
				this.y = y;
				propertyChangeSupport.firePropertyChange(this, Properties.Y, old, y);
			}
		}
	}
}
