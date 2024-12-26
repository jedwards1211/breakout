package org.breakout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.breakout.FileFinder.FileWalker;
import org.breakout.FileFinder.MatchSet;
import org.junit.Test;

public class FileFinderTests {
	FileWalker makeWalkFiles(
		String... files
	) {
		return (Function<Path, Boolean> iteratee) -> {
			for (String p : files) {
				if (!iteratee.apply(Paths.get(p))) return;
			}
		};
	}
	
	@Test
	public void testExactMatches() throws IOException {
		Map<String, MatchSet> expected = new HashMap<>();
		expected.put(
			"foo.pdf", new MatchSet(Paths.get("/var/data/foo.pdf").toUri())
		);
		expected.put(
			"data/BAR.pdf", new MatchSet(Paths.get("/var/data/bar.pdf").toUri())
		);
		assertEquals(expected, 
			FileFinder.findFiles(
				Arrays.asList("foo.pdf", "data/BAR.pdf"),
				makeWalkFiles(
					"/var/data/foo.pdf",
					"/var/datums/bar.pdf",
					"/var/data/bar.pdf",
					"/var/data/baz.pdf"
				)
			)
		);
	}
	
	@Test
	public void testSimilarMatches() throws IOException {
		Map<String, MatchSet> expected = new HashMap<>();
		expected.put(
			"data/foo_1.pdf", new MatchSet().put(
				Paths.get("/var/data/foo_01.pdf").toUri(),
				1f - 1f / "data/foo_01.pdf".length()
			).put(
				Paths.get("/var/data/bar_1.pdf").toUri(),
				1f - 3f / "data/bar_1.pdf".length()
			)
		);
		assertEquals(expected, 
			FileFinder.findFiles(
				Arrays.asList("data/foo_1.pdf"),
				makeWalkFiles(
					"/var/data/foo_01.pdf",
					"/var/data/bar_1.pdf",
					"/var/data/baz.pdf"
				)
			)
		);
	}
}
