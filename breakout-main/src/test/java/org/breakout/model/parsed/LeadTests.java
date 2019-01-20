package org.breakout.model.parsed;

import org.andork.unit.Length;
import org.junit.Assert;
import org.junit.Test;

public class LeadTests {
	@Test
	public void testDescribeSize() {
		Lead lead = new Lead();
		lead.width = MetacaveLengthParser.parse("22", Length.feet);
		lead.height = MetacaveLengthParser.parse("3.53", Length.feet);
		Assert.assertEquals("22w 3.5h", lead.describeSize(Length.feet));
		Assert.assertEquals("6.7w 1.1h", lead.describeSize(Length.meters));
		
		lead.width = null;
		Assert.assertEquals("1.1h", lead.describeSize(Length.meters));
		
		lead.height = null;
		Assert.assertEquals(null, lead.describeSize(Length.meters));
	}
}
