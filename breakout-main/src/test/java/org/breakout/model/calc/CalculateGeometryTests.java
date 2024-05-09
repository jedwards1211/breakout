package org.breakout.model.calc;

import org.breakout.model.CrossSectionType;
import org.breakout.model.calc.CalcCrossSection;
import org.breakout.model.calc.CalcShot;
import org.breakout.model.calc.CalcStation;
import org.breakout.model.calc.CalculateGeometry;
import org.junit.Assert;
import org.junit.Test;

public class CalculateGeometryTests {
//	@Test
	public void testLinkCrossSections001() {
		CalcStation A = new CalcStation("A");
		CalcStation B = new CalcStation("B");
		CalcStation C = new CalcStation("C");

		CalcShot AB = new CalcShot(A, B);
		AB.azimuth = Math.toRadians(60);
		AB.toCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 1, 2, 3, 4);
		CalcShot BC = new CalcShot(B, C);
		BC.azimuth = Math.toRadians(120);

		CalculateGeometry.linkCrossSections(AB, B, BC);
		Assert.assertNotSame(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(Math.toRadians(90), AB.toCrossSection.facingAzimuth, 1e-12);
		Assert.assertEquals(Math.toRadians(90), BC.fromCrossSection.facingAzimuth, 1e-12);
	}

//	@Test
	public void testLinkCrossSections002() {
		CalcStation A = new CalcStation("A");
		CalcStation B = new CalcStation("B");
		CalcStation C = new CalcStation("C");

		CalcShot AB = new CalcShot(A, B);
		AB.azimuth = Math.toRadians(60);
		AB.toCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 1, 2, 3, 4);
		AB.toCrossSection.facingAzimuth = Math.toRadians(70);
		CalcShot BC = new CalcShot(B, C);
		BC.azimuth = Math.toRadians(120);

		CalculateGeometry.linkCrossSections(AB, B, BC);
		Assert.assertNotSame(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(Math.toRadians(70), AB.toCrossSection.facingAzimuth, 1e-12);
		Assert.assertEquals(Math.toRadians(70), BC.fromCrossSection.facingAzimuth, 1e-12);
	}

//	@Test
	public void testLinkCrossSections003() {
		CalcStation A = new CalcStation("A");
		CalcStation B = new CalcStation("B");
		CalcStation C = new CalcStation("C");

		CalcShot AB = new CalcShot(A, B);
		AB.azimuth = Math.toRadians(60);
		AB.toCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 1, 2, 3, 4);
		CalcShot CB = new CalcShot(C, B);
		CB.azimuth = Math.toRadians(300);

		CalculateGeometry.linkCrossSections(AB, B, CB);
		Assert.assertArrayEquals(CB.toCrossSection.measurements, new double[] { 2, 1, 3, 4 }, 0.0);
		Assert.assertEquals(Math.toRadians(90), AB.toCrossSection.facingAzimuth, 1e-12);
		Assert.assertEquals(Math.toRadians(270), CB.toCrossSection.facingAzimuth, 1e-12);
	}

//	@Test
	public void testLinkCrossSections004() {
		CalcStation A = new CalcStation("A");
		CalcStation B = new CalcStation("B");
		CalcStation C = new CalcStation("C");

		CalcShot AB = new CalcShot(A, B);
		AB.azimuth = Math.toRadians(60);
		AB.fromCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 1, 2, 3, 4);
		CalcShot BC = new CalcShot(B, C);
		BC.azimuth = Math.toRadians(120);

		CalculateGeometry.linkCrossSections(AB, B, BC);
		Assert.assertNotSame(AB.fromCrossSection, AB.toCrossSection);
		Assert.assertArrayEquals(AB.fromCrossSection.measurements, AB.toCrossSection.measurements, 0.0);
		Assert.assertNotSame(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(Math.toRadians(90), AB.toCrossSection.facingAzimuth, 1e-12);
		Assert.assertEquals(Math.toRadians(90), BC.fromCrossSection.facingAzimuth, 1e-12);
	}

//	@Test
	public void testLinkCrossSections005() {
		CalcStation A = new CalcStation("A");
		CalcStation B = new CalcStation("B");
		CalcStation C = new CalcStation("C");

		CalcShot AB = new CalcShot(A, B);
		AB.azimuth = Math.toRadians(60);
		AB.fromCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 1, 2, 3, 4);
		CalcShot BC = new CalcShot(B, C);
		BC.azimuth = Math.toRadians(120);
		BC.toCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 9, 5, 7, 6);

		CalculateGeometry.linkCrossSections(AB, B, BC);
		Assert.assertNotSame(AB.fromCrossSection, AB.toCrossSection);
		Assert.assertArrayEquals(new double[] {
				(1 + 9) / 2.0,
				(2 + 5) / 2.0,
				(3 + 7) / 2.0,
				(4 + 6) / 2.0 }, AB.toCrossSection.measurements, 0.0);
		Assert.assertNotSame(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(BC.fromCrossSection, AB.toCrossSection);
		Assert.assertEquals(Math.toRadians(90), AB.toCrossSection.facingAzimuth, 1e-12);
		Assert.assertEquals(Math.toRadians(90), BC.fromCrossSection.facingAzimuth, 1e-12);
	}

//	@Test
	public void testLinkCrossSections006() {
		CalcStation A = new CalcStation("A");
		CalcStation B = new CalcStation("B");
		CalcStation C = new CalcStation("C");

		CalcShot AB = new CalcShot(A, B);
		AB.azimuth = Math.toRadians(60);
		AB.fromCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 1, 2, 3, 4);
		AB.fromCrossSection.facingAzimuth = AB.azimuth;
		CalcShot CB = new CalcShot(C, B);
		CB.azimuth = Math.toRadians(300);
		CB.fromCrossSection = new CalcCrossSection(CrossSectionType.LRUD, 9, 5, 7, 6);
		CB.fromCrossSection.facingAzimuth = CB.azimuth;

		CalculateGeometry.linkCrossSections(AB, B, CB);
		Assert.assertNotSame(AB.fromCrossSection, AB.toCrossSection);
		Assert.assertArrayEquals(new double[] {
				(1 + 5) / 2.0,
				(2 + 9) / 2.0,
				(3 + 7) / 2.0,
				(4 + 6) / 2.0 }, AB.toCrossSection.measurements, 0.0);
		Assert.assertArrayEquals(new double[] {
				(2 + 9) / 2.0,
				(1 + 5) / 2.0,
				(3 + 7) / 2.0,
				(4 + 6) / 2.0 }, CB.toCrossSection.measurements, 0.0);
		Assert.assertEquals(Math.toRadians(90), AB.toCrossSection.facingAzimuth, 1e-12);
		Assert.assertEquals(Math.toRadians(270), CB.toCrossSection.facingAzimuth, 1e-12);
	}
}
