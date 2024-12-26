package org.breakout;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.andork.util.Levenshtein;

public class FileFinder {
	public interface FileWalker {
		public void walkFiles(Function<Path, Boolean> iteratee) throws IOException;
	}
	
	/**
	 * 
	 * @param files strings that may be URLs, absolute file paths, or partial file paths
	 * (e.g. "foo.pdf", "somedir/foo.pdf")
	 * @param walkFiles a function to walk whatever files you want to search.  It will be
	 * called with an iteratee argument; it should call the iteratee on each walked file.
	 * If the iteratee returns false, it should stop walking.
	 * @return Match results, a map from an input strings to 
	 * a {@link MatchSet} of matches 
	 */
	public static Map<String, MatchSet> findFiles(
		Iterable<String> files,
		FileWalker fileWalker
	) throws IOException {
		Map<String, MatchSet> result = new HashMap<>();
		Map<String, String> remaining = new HashMap<>();
		
		for (String s : files) {
			try {
				URI uri = new URL(s).toURI();
				result.put(s, new MatchSet(uri));
				continue;
			} catch (Exception e) {
				// ignore
			}
			File file = new File(s);
			if (file.isAbsolute()) {
				result.put(s, new MatchSet(file.toURI()));
				continue;
			}
			remaining.put(s.replace('\\', '/').toLowerCase(), s);
		}
	
		if (remaining.isEmpty()) {
			return result;
		}
		
		fileWalker.walkFiles((Path path) -> {
			String pathStr = path.toString().replace('\\', '/').toLowerCase();
			while (!pathStr.isEmpty()) {
				String matchedInput = remaining.remove(pathStr);
				if (matchedInput != null) {
					result.put(matchedInput, new MatchSet(path.toUri()));
					return !remaining.isEmpty();
				}
				pathStr = pathStr.replaceFirst("^[^\\/]*(\\/|$)", "");
			}
			return !remaining.isEmpty();
		});
		
		if (remaining.isEmpty()) {
			return result;
		}
	
		fileWalker.walkFiles((Path path) -> {
			for (Map.Entry<String, String> entry : remaining.entrySet()) {
				String needle = entry.getKey();
				String pathSuffix = pathSuffix(path, charCount(needle, '/')).toString().replace('\\', '/').toLowerCase();;
				int distance = Levenshtein.distance(needle, pathSuffix.toString());
				float similarity = 1f - (float) distance / Math.max(needle.length(), pathSuffix.length());
				if (similarity < 0.7f) {
					continue;
				}
				String matchedInput = entry.getValue();
				MatchSet matches = result.get(matchedInput);
				if (matches == null) {
					result.put(matchedInput, matches = new MatchSet());
				}
				matches.put(path.toUri(), similarity);
			}
			return !remaining.isEmpty();
		});
		
		
		return result;
	}

	public static class MatchSet {
		final Map<URI, Float> matches = new HashMap<>();
		
		MatchSet() { }

		MatchSet(URI uri) {
			this.matches.put(uri, 1.0f);
		}
		
		public URI exactMatch() {
			if (this.matches.size() != 1) {
				return null;
			}
			Map.Entry<URI, Float> entry = matches.entrySet().iterator().next();
			return entry.getValue() == 1.0f ? entry.getKey() : null;
		}
		
		public MatchSet put(URI uri, float similarity) {
			this.matches.put(uri, similarity);
			return this;
		}
		
		public List<URI> bestMatches(float minSimilarity, int maxCount) {
			List<URI> sorted = new ArrayList<>(this.matches.keySet());
			Collections.sort(sorted, (a, b) -> {
				float diff = this.matches.get(b) - this.matches.get(a);
				return diff > 0 ? 1 : diff < 0 ? -1 : 0;
			});
			return sorted.subList(0, Math.min(sorted.size(), maxCount));
		}

		@Override
		public int hashCode() {
			return Objects.hash(matches);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MatchSet other = (MatchSet) obj;
			return Objects.equals(matches, other.matches);
		}

		@Override
		public String toString() {
			return "MatchSet [matches=" + matches + "]";
		}
		
		
	}

	static int charCount(String s, char c) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) count++;
		}
		return count;
	}
	
	static Path pathSuffix(Path path, int numParentDirs) {
		Path parent = path.getParent();
		for (int i = 0; parent != null && i < numParentDirs; i++) {
			parent = parent.getParent();
		}
		return parent != null ? parent.relativize(path) : null;
	}
}
