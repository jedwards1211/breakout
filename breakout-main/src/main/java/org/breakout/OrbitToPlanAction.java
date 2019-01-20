package org.breakout;

import static org.andork.math3d.Vecmath.newMat4f;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.andork.awt.I18n.Localizer;
import org.andork.jogl.awt.anim.SpringViewOrbitAnimation;
import org.andork.math3d.Vecmath;
import org.andork.swing.OnEDT;

@SuppressWarnings("serial")
public class OrbitToPlanAction extends AbstractAction implements Action {
	BreakoutMainView mainView;

	public OrbitToPlanAction(BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(OrbitToPlanAction.this.getClass());
				localizer.setName(OrbitToPlanAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (mainView.model3d == null) {
			return;
		}

		float[] center = new float[3];
		mainView.orbiter.getCenter(center);

		if (Vecmath.hasNaNsOrInfinites(center)) {
			mainView.model3d.getCenter(center);
		}

		float[] v = newMat4f();
		mainView.renderer.getViewSettings().getViewXform(v);

		mainView.removeUnprotectedCameraAnimations();
		mainView.cameraAnimationQueue.add(new SpringViewOrbitAnimation(mainView.autoDrawable, mainView.renderer.getViewSettings(),
				center, 0f, (float) -Math.PI * .5f, .1f, .05f, 30));
		mainView.cameraAnimationQueue.add(mainView.new AnimationViewSaver());
	}

}
