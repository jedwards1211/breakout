package org.andork.semver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Re {
	public static final class Token {
		public final String src;
		public final Pattern re;

		public Token(String src) {
			super();
			this.src = src;
			this.re = Pattern.compile(src);
		}

		@Override
		public String toString() {
			return src;
		}

		public Matcher matcher(String input) {
			return re.matcher(input);
		}
	}

	// ## Numeric Identifier
	// A single "0", or a non-zero digit followed by zero or more digits.

	public static final Token NUMERICIDENTIFIER = new Token("0|[1-9]\\d*");
	public static final Token NUMERICIDENTIFIERLOOSE = new Token("[0-9]+");

	// ## Non-numeric Identifier
	// Zero or more digits, followed by a letter or hyphen, and then zero or
	// more letters, digits, or hyphens.
	public static final Token NONNUMERICIDENTIFIER = new Token("\\d*[a-zA-Z-][a-zA-Z0-9-]*");

	// ## Main Version
	// Three dot-separated numeric identifiers.

	public static final Token MAINVERSION =
		new Token("(" + NUMERICIDENTIFIER + ")\\.(" + NUMERICIDENTIFIER + ")\\.(" + NUMERICIDENTIFIER + ")");

	public static final Token MAINVERSIONLOOSE =
		new Token(
			"(" + NUMERICIDENTIFIERLOOSE + ")\\.(" + NUMERICIDENTIFIERLOOSE + ")\\.(" + NUMERICIDENTIFIERLOOSE + ")");

	// ## Pre-release Version Identifier
	// A numeric identifier, or a non-numeric identifier.

	public static final Token PRERELEASEIDENTIFIER =
		new Token("(?:" + NUMERICIDENTIFIER + "|" + NONNUMERICIDENTIFIER + ")");
	public static final Token PRERELEASEIDENTIFIERLOOSE =
		new Token("(?:" + NUMERICIDENTIFIERLOOSE + "|" + NONNUMERICIDENTIFIER + ")");

	// ## Pre-release Version
	// Hyphen, followed by one or more dot-separated pre-release version
	// identifiers.

	public static final Token PRERELEASE =
		new Token("(?:-(" + PRERELEASEIDENTIFIER + "(?:\\." + PRERELEASEIDENTIFIER + ")*))");
	public static final Token PRERELEASELOOSE =
		new Token("(?:-(" + PRERELEASEIDENTIFIERLOOSE + "(?:\\." + PRERELEASEIDENTIFIERLOOSE + ")*))");

	// ## Build Metadata Identifier
	// Any combination of digits, letters, or hyphens.

	public static final Token BUILDIDENTIFIER = new Token("[0-9A-Za-z-]+");

	// ## Build Metadata
	// Plus sign, followed by one or more period-separated build metadata
	// identifiers.

	public static final Token BUILD = new Token("(?:\\+(" + BUILDIDENTIFIER + "(?:\\." + BUILDIDENTIFIER + ")*))");

	// ## Full Version String
	// A main version, followed optionally by a pre-release version and
	// build metadata.

	// Note that the only major, minor, patch, and pre-release sections of
	// the version string are capturing groups. The build metadata is not a
	// capturing group, because it should not ever be used in version
	// comparison.

	public static final Token FULLPLAIN = new Token("v?" + MAINVERSION + PRERELEASE + "?" + BUILD + "?");

	public static final Token FULL = new Token("^" + FULLPLAIN + "$");

	// like full, but allows v1.2.3 and =1.2.3, which people do sometimes.
	// also, 1.0.0alpha1 (prerelease without the hyphen) which is pretty
	// common in the npm registry.
	public static final Token LOOSEPLAIN =
		new Token("[v=\\s]*" + MAINVERSIONLOOSE + PRERELEASELOOSE + "?" + BUILD + "?");

	public static final Token LOOSE = new Token("^" + LOOSEPLAIN + "$");

	public static final Token GTLT = new Token("((?:<|>)?=?)");

	// Something like "2.*" or "1.2.x".
	// Note that "x.x" is a valid xRange identifer, meaning "any version"
	// Only the first item is strictly required.
	public static final Token XRANGEIDENTIFIERLOOSE = new Token(NUMERICIDENTIFIERLOOSE + "|x|X|\\*");
	public static final Token XRANGEIDENTIFIER = new Token(NUMERICIDENTIFIER + "|x|X|\\*");
}
