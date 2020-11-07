package org.andork.semver;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;

import org.andork.util.ArrayUtils;

public class SemVer implements Comparable<SemVer> {
	private final String raw;
	private final int major;
	private final int minor;
	private final int patch;
	private final Object[] prerelease;
	private final String[] build;
	private final String version;

	public static final int LOOSE = 1 << 0;
	public static final int INCLUDE_PRERELEASE = 1 << 1;

	public SemVer(String version) {
		this(version, 0);
	}

	public SemVer(String version, int options) {
		Matcher m = ((options & LOOSE) != 0 ? Re.LOOSE : Re.FULL).matcher(version);
		if (!m.find()) {
			throw new IllegalArgumentException("Invalid Version: " + version);
		}

		raw = version;
		major = Integer.valueOf(m.group(1));
		minor = Integer.valueOf(m.group(2));
		patch = Integer.valueOf(m.group(3));

		if (m.group(4) == null) {
			prerelease = new String[0];
		}
		else {
			String[] basePrerelease = m.group(4).split("\\.");
			prerelease =
				ArrayUtils
					.map(
						basePrerelease,
						new Object[basePrerelease.length],
						id -> id.matches("^[0-9]+$") ? Integer.valueOf(id) : id);
		}

		if (m.group(5) == null) {
			build = new String[0];
		}
		else {
			build = m.group(5).split("\\.");
		}

		String formattedVersion = major + "." + minor + "." + patch;
		if (prerelease.length > 0)
			formattedVersion += "-" + ArrayUtils.join(".", prerelease);
		this.version = formattedVersion;
	}

	public String getRaw() {
		return raw;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	public Object[] getPrerelease() {
		return prerelease;
	}

	public String[] getBuild() {
		return build;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return raw;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(build);
		result = prime * result + Arrays.deepHashCode(prerelease);
		result = prime * result + Objects.hash(major, minor, patch);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SemVer other = (SemVer) obj;
		return Arrays.equals(build, other.build)
			&& major == other.major
			&& minor == other.minor
			&& patch == other.patch
			&& Arrays.deepEquals(prerelease, other.prerelease);
	}

	@Override
	public int compareTo(SemVer o) {
		if (version.equals(o.version))
			return 0;
		int c;
		c = compareMain(o);
		if (c != 0)
			return c;
		return comparePre(o);
	}

	public int compareMain(SemVer o) {
		int c;
		c = major - o.major;
		if (c != 0)
			return sign(c);
		c = minor - o.minor;
		if (c != 0)
			return sign(c);
		return sign(patch - o.patch);
	}

	public int comparePre(SemVer o) {
		if (prerelease.length > 0 && o.prerelease.length == 0) {
			return -1;
		}
		if (prerelease.length == 0 && o.prerelease.length > 0) {
			return 1;
		}
		if (prerelease.length == 0 && o.prerelease.length == 0) {
			return 0;
		}

		int i = 0;
		do {
			Object a = i < prerelease.length ? prerelease[i] : null;
			Object b = i < o.prerelease.length ? o.prerelease[i] : null;
			if (a == null && b == null)
				return 0;
			if (b == null)
				return 1;
			if (a == null)
				return -1;
			if (Objects.equals(a, b))
				continue;
			return compareIdentifiers(a, b);
		} while (++i > 0);

		return 0;
	}

	private static int sign(int x) {
		return x < 0 ? -1 : x > 0 ? 1 : 0;
	}

	private static int compareIdentifiers(Object a, Object b) {
		Integer anum =
			a == null
				? null
				: a instanceof Integer
					? (Integer) a
					: a instanceof String && ((String) a).matches("^[0-9]+$") ? Integer.valueOf((String) a) : null;
		Integer bnum =
			b == null
				? null
				: b instanceof Integer
					? (Integer) b
					: b instanceof String && ((String) b).matches("^[0-9]+$") ? Integer.valueOf((String) b) : null;

		if (anum != null && bnum != null) {
			a = anum;
			b = bnum;
		}

		return Objects.equals(a, b)
			? 0
			: (anum != null && bnum == null)
				? -1
				: (bnum != null && anum == null)
					? 1
					: anum != null && bnum != null
						? sign(anum.compareTo(bnum))
						: sign(String.valueOf(a).compareTo(String.valueOf(b)));
	}
}
