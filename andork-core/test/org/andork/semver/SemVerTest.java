package org.andork.semver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SemVerTest {
	static final String[][] compareFixtures =
		{
			{ "0.0.0", "0.0.0-foo" },
			{ "0.0.1", "0.0.0" },
			{ "1.0.0", "0.9.9" },
			{ "0.10.0", "0.9.0" },
			{ "0.99.0", "0.10.0" },
			{ "2.0.0", "1.2.3" },
			{ "1.2.3", "1.2.3-asdf" },
			{ "1.2.3", "1.2.3-4" },
			{ "1.2.3", "1.2.3-4-foo" },
			{ "1.2.3-5-foo", "1.2.3-5" },
			{ "1.2.3-5", "1.2.3-4" },
			{ "1.2.3-5-foo", "1.2.3-5-Foo" },
			{ "3.0.0", "2.7.2+asdf" },
			{ "1.2.3-a.10", "1.2.3-a.5" },
			{ "1.2.3-a.b", "1.2.3-a.5" },
			{ "1.2.3-a.b", "1.2.3-a" },
			{ "1.2.3-a.b.c.10.d.5", "1.2.3-a.b.c.5.d.100" },
			{ "1.2.3-r2", "1.2.3-r100" },
			{ "1.2.3-r100", "1.2.3-R2" } };

	@Test
	public void compareTest() {
		for (int i = 0; i < compareFixtures.length; i++) {
			SemVer s0 = new SemVer(compareFixtures[i][0]);
			SemVer s1 = new SemVer(compareFixtures[i][1]);
			assertEquals(s0 + " > " + s1, 1, s0.compareTo(s1));
			assertEquals(s1 + " < " + s0, -1, s1.compareTo(s0));
			assertEquals(s0 + " == " + s1, 0, s0.compareTo(s0));
			assertEquals(s1 + " == " + s1, 0, s1.compareTo(s1));
		}
	}

	static final String[][] compareLooseFixtures =
		{
			{ "v0.0.0", "0.0.0-foo" },
			{ "v0.0.1", "0.0.0" },
			{ "v1.0.0", "0.9.9" },
			{ "v0.10.0", "0.9.0" },
			{ "v0.99.0", "0.10.0" },
			{ "v2.0.0", "1.2.3" },
			{ "0.0.0", "v0.0.0-foo" },
			{ "0.0.1", "v0.0.0" },
			{ "1.0.0", "v0.9.9" },
			{ "0.10.0", "v0.9.0" },
			{ "0.99.0", "v0.10.0" },
			{ "2.0.0", "v1.2.3" }, };

	@Test
	public void compareLooseTest() {
		for (int i = 0; i < compareLooseFixtures.length; i++) {
			SemVer s0 = new SemVer(compareLooseFixtures[i][0], SemVer.LOOSE);
			SemVer s1 = new SemVer(compareLooseFixtures[i][1], SemVer.LOOSE);
			assertEquals(s0 + " > " + s1, 1, s0.compareTo(s1));
			assertEquals(s1 + " < " + s0, -1, s1.compareTo(s0));
			assertEquals(s0 + " == " + s1, 0, s0.compareTo(s0));
			assertEquals(s1 + " == " + s1, 0, s1.compareTo(s1));
		}
	}

}
