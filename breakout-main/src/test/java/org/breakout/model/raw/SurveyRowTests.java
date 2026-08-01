package org.breakout.model.raw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class SurveyRowTests {
	@Test
	public void testMutable() {
		SurveyRow row = new SurveyRow().setFromStation("A1").setToStation("B1");
		SurveyRow.Mutable mut = row.toMutable();
		assertSame(row, mut.toImmutable());
		assertEquals("A1", mut.getFromStation());
		assertEquals("B1", mut.getToStation());
		mut.setFromStation("A2");
		assertEquals("A2", mut.getFromStation());
		assertEquals("B1", mut.getToStation());
		assertNotSame(row, mut.toImmutable());
		assertEquals("A2", mut.toImmutable().getFromStation());
		assertEquals("B1", mut.toImmutable().getToStation());
		
		SurveyRow immut = mut.toImmutable();
		assertEquals("A2", immut.getFromStation());
		assertEquals("B1", immut.getToStation());
		mut.setFromStation("A3");
		assertEquals("A3", mut.getFromStation());
		assertEquals("A2", immut.getFromStation());
		
		assertSame(row, row.setFromStation("A1"));
		assertNotSame(row, row.setFromStation("A2"));
		assertEquals("A2", row.setFromStation("A2").getFromStation());
		assertEquals("B1", row.setFromStation("A2").getToStation());
	}
}
