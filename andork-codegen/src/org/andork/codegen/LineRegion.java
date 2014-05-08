package org.andork.codegen;

import java.util.Comparator;
import java.util.List;

public class LineRegion {
	public final int	startLine;
	public final int	startColumn;
	public final int	endLine;
	public final int	endColumn;

	public LineRegion(int startLine, int startColumn, int endLine, int endColumn) {
		super();
		if (startLine < 0) {
			throw new IllegalArgumentException("startLine must be >= 0");
		}
		if (startColumn < 0) {
			throw new IllegalArgumentException("startColumn must be >= 0");
		}
		if (endLine < startLine) {
			throw new IllegalArgumentException("endLine must be >= startLine");
		}
		if (startLine == endLine && endColumn < startColumn) {
			throw new IllegalArgumentException("endColumn must be >= startColumn if startLine == endLine");
		}
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}

	/**
	 * Creates a zero-size region.
	 * 
	 * @param line
	 * @param beforeColumn
	 */
	public LineRegion(int line, int beforeColumn) {
		if (line < 0) {
			throw new IllegalArgumentException("line must be >= 0");
		}
		if (beforeColumn < 0) {
			throw new IllegalArgumentException("beforeColumn must be >= 0");
		}
		this.startLine = line;
		this.startColumn = beforeColumn;
		this.endLine = line;
		this.endColumn = beforeColumn - 1;
	}

	public LineRegion(LineRegion other) {
		this(other.startLine, other.startColumn, other.endLine, other.endColumn);
	}

	public LineRegion union(LineRegion other) {
		int nStartLine;
		int nStartColumn;
		int nEndLine;
		int nEndColumn;

		if (startLine == other.startLine) {
			nStartLine = startLine;
			nStartColumn = Math.min(startColumn, other.startColumn);
		} else if (startLine < other.startLine) {
			nStartLine = startLine;
			nStartColumn = startColumn;
		} else {
			nStartLine = other.startLine;
			nStartColumn = other.startColumn;
		}
		if (endLine == other.endLine) {
			nEndLine = endLine;
			nEndColumn = Math.max(endColumn, other.endColumn);
		} else if (endLine > other.endLine) {
			nEndLine = endLine;
			nEndColumn = endColumn;
		} else {
			nEndLine = other.endLine;
			nEndColumn = other.endColumn;
		}

		return new LineRegion(nStartLine, nStartColumn, nEndLine, nEndColumn);
	}

	public boolean isZeroSize() {
		return startLine == endLine && endColumn < startColumn;
	}

	public boolean overlaps(LineRegion other) {
		if (startLine > other.endLine || endLine < other.startLine) {
			return false;
		}

		if (other.isZeroSize()) {
			if (isZeroSize()) {
				return false;
			}
			if (startLine == other.startLine && startColumn >= other.startColumn) {
				return false;
			}
			if (endLine == other.endLine && endColumn <= other.endColumn) {
				return false;
			}
			return true;
		}
		if (startLine == other.startLine && startColumn <= other.endColumn) {
			return true;
		}
		if (endLine == other.endLine && endColumn >= other.startColumn) {
			return true;
		}
		return false;
	}

	public static void replace(List<String> lines, LineRegion region, String replacement, int tabSize) {
		StringBuffer sb = new StringBuffer();
		String startLine = lines.get(region.startLine);
		int startIndex = indexOfColumn(startLine, region.startColumn, tabSize);
		String endLine = lines.get(region.endLine);
		int endIndex = indexOfColumn(endLine, region.endColumn, tabSize);
		sb.append(startLine.substring(0, startIndex));
		sb.append(replacement);
		if (endIndex + 1 < endLine.length()) {
			sb.append(endLine.substring(endIndex + 1));
		}

		for (int line = region.endLine; line >= region.startLine; line--) {
			lines.remove(line);
		}

		String[] newLines = sb.toString().split("(\r\n|\n\r|\n|\r)");
		for (int line = newLines.length - 1; line >= 0; line--) {
			lines.add(region.startLine, newLines[line]);
		}
	}

	private static int indexOfColumn(String s, int column, int tabSize) {
		if (column < 0) {
			return column;
		}
		int curColumn = 0;
		int i;
		for (i = 0; i < s.length() && curColumn < column; i++) {
			if (s.charAt(i) == '\t') {
				curColumn += tabSize;
				curColumn -= curColumn % tabSize;
			} else {
				curColumn++;
			}
			if (curColumn > column) {
				return i;
			}
		}
		return i;
	}

	public boolean equals(Object o) {
		if (o instanceof LineRegion) {
			LineRegion l = (LineRegion) o;
			return startLine == l.startLine && startColumn == l.startColumn &&
					endLine == l.endLine && endColumn == l.endColumn;
		}
		return false;
	}

	public int hashCode() {
		int hashCode = startLine;
		hashCode = (23 * hashCode) ^ startColumn;
		hashCode = (29 * hashCode) ^ endLine;
		hashCode = (31 * hashCode) ^ endColumn;
		return hashCode;
	}

	public static Comparator<LineRegion>	OVERLAP_FORBIDDING_COMPARATOR	= new OverlapForbiddingComparator();

	private static class OverlapForbiddingComparator implements Comparator<LineRegion> {
		@Override
		public int compare(LineRegion o1, LineRegion o2) {
			if (o1.overlaps(o2)) {
				throw new IllegalArgumentException("o1 and o2 must not overlap");
			}
			int result = o1.startLine - o2.startLine;
			if (result != 0) {
				return result;
			}
			if (o1.isZeroSize()) {
				if (o2.isZeroSize()) {
					return o1.startColumn - o2.startColumn;
				} else {
					return o1.startColumn <= o2.startColumn ? -1 : 1;
				}
			} else {
				if (o2.isZeroSize()) {
					return o1.startColumn >= o2.startColumn ? 1 : -1;
				} else {
					return o1.startColumn - o2.startColumn;
				}
			}
		}
	}

	public String toString() {
		return String.format("%d : %d - %d : %d", startLine, startColumn, endLine, endColumn);
	}
}