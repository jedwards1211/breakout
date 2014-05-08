package org.andork.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple tool for generating Java code. It inserts braces, indentation, javadoc
 * markers, and comment markers for you.
 * 
 * @author james.a.edwards
 */
public class CodeBuilder2 {
	private static interface Element {
		public void append(StringBuffer sb, String tabs);
	}

	private static class Line implements Element {
		StringBuffer	line;

		private Line(StringBuffer line) {
			super();
			this.line = line;
		}

		private Line(String line) {
			this(new StringBuffer(line));
		}

		public void append(StringBuffer sb, String tabs) {
			sb.append(tabs).append(line).append('\n');
		}
	}

	/**
	 * Represents a group of lines of code. This may be the root block, a Java
	 * code block for a class, method, etc., a Javadoc block, or a comment
	 * block. The block may contain sub-blocks, and it may indent/format them
	 * based upon its type. To get the formatted code, just call
	 * {@link #toString()}.
	 * 
	 * @author james.a.edwards
	 */
	public static class Block implements Element {
		Block			parent;
		String			header;
		String			indenter;
		String			footer;
		List<Element>	contents	= new ArrayList<Element>();

		private Block() {
		}

		private Block(String header, String indenter, String footer) {
			this(null, header, indenter, footer);
		}

		private Block(Block parent, String header, String indenter, String footer) {
			this.parent = parent;
			this.header = header;
			this.indenter = indenter;
			this.footer = footer;
		}

		/**
		 * Adds a block to the inside of this block.
		 * 
		 * @param block
		 *            the block to add
		 * @throws IllegalArgumentException
		 *             if {@code block} already has a parent
		 */
		public void addBlock(Block block) {
			if (block.parent != null) {
				throw new IllegalArgumentException("block already has a parent");
			}
			block.parent = this;
			contents.add(block);
		}

		/**
		 * Adds a line of text to this block.
		 * 
		 * @param line
		 *            the text to add.
		 * @return a {@link StringBuffer} representing the new line, which may
		 *         be modified.
		 */
		public StringBuffer addLine(String line) {
			StringBuffer sb = addLine();
			sb.append(line);
			return sb;
		}

		/**
		 * Adds an (initially empty) line of text to this block.
		 * 
		 * @return a {@link StringBuffer} representing the new line, which may
		 *         be modified.
		 */
		public StringBuffer addLine() {
			StringBuffer sb = new StringBuffer();
			contents.add(new Line(sb));
			return sb;
		}

		/**
		 * Appends the contents of this block into a {@link StringBuffer}.
		 * 
		 * @param sb
		 *            the {@code StringBuffer} to append to.
		 * @param tabs
		 *            the text to append to {@code sb} before each line of this
		 *            block.
		 */
		public void append(StringBuffer sb, String tabs) {
			if (header != null) {
				sb.append(tabs).append(header).append("\n");
			}
			String childTabs = tabs;
			if (indenter != null) {
				childTabs += indenter;
			}
			for (Element statement : contents) {
				statement.append(sb, childTabs);
			}
			if (footer != null) {
				sb.append(tabs).append(footer).append("\n");
			}
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			append(sb, "");
			return sb.toString();
		}
	}

	public static class JavaBlock extends Block {
		private JavaBlock() {
			this(null, null, null, null);
		}

		private JavaBlock(JavaBlock parent, String header, String indenter, String footer) {
			this.parent = parent;
			this.header = header;
			this.indenter = indenter;
			this.footer = footer;
		}

		/**
		 * Adds a {@code Block} of java code as a child of this one.
		 * 
		 * @param header
		 *            the code that comes before the start brace.
		 * @param footer
		 *            the code that comes after the end brace.
		 * @return the new child {@code Block}.
		 */
		public JavaBlock newJavaBlock(String header, String footer) {
			if (header == null) {
				header = "{";
			} else {
				header += " {";
			}
			if (footer == null) {
				footer = "}";
			} else {
				footer = "} " + footer;
			}
			JavaBlock block = new JavaBlock(this, header, "\t", footer);
			contents.add(block);
			return block;
		}

		/**
		 * Adds a {@code Block} of java code as a child of this one.
		 * 
		 * @param header
		 *            the code that comes before the start brace.
		 * @return the new child {@code Block}.
		 */
		public JavaBlock newJavaBlock(String header) {
			return newJavaBlock(header, null);
		}

		/**
		 * Adds a {@code Block} of java code as a child of this one.
		 * 
		 * @param header
		 *            the code that comes before the start brace.
		 * @return the new child {@code Block}.
		 */
		public DeclBlock newDeclBlock(String header) {
			if (header == null) {
				header = "{";
			} else {
				header += " {";
			}
			DeclBlock block = new DeclBlock(this, header, "\t", "}");
			contents.add(block);
			return block;
		}

		/**
		 * Adds a {@code Block} of comments as a child of this one.
		 * 
		 * @return the new child {@code Block}.
		 */
		public Block newCommentBlock() {
			Block block = new Block(this, null, "// ", null);
			contents.add(block);
			return block;
		}
	}

	public static class DeclBlock extends JavaBlock {
		JavadocBlock	javadoc;
		List<Line>		annotations	= new ArrayList<Line>();

		private DeclBlock(JavaBlock parent, String header, String indenter, String footer) {
			super(parent, header, indenter, footer);
		}

		public StringBuffer addAnnotation() {
			return addAnnotation("");
		}

		public StringBuffer addAnnotation(String annotation) {
			Line line = new Line(annotation);
			annotations.add(line);
			return line.line;
		}

		public JavadocBlock javadoc() {
			if (javadoc == null) {
				javadoc = new JavadocBlock(this);
			}
			return javadoc;
		}

		@Override
		public void append(StringBuffer sb, String tabs) {
			if (javadoc != null) {
				javadoc.append(sb, tabs);
			}
			for (Line annotation : annotations) {
				annotation.append(sb, tabs);
			}
			super.append(sb, tabs);
		}
	}

	public static class JavadocBlock extends Block {
		private JavadocBlock() {
			this(null);
		}

		private JavadocBlock(DeclBlock parent) {
			super(parent, "/**", " * ", " */");
		}
	}

	/**
	 * @return a new root {@link Block}. It will not apply any formatting to its
	 *         lines. To add code, use {@link Block#newJavaBlock(String)}.
	 */
	public static JavaBlock newJavaBlock() {
		return new JavaBlock();
	}

	/**
	 * Adds a {@code Block} of java code as a child of this one.
	 * 
	 * @param header
	 *            the code that comes before the start brace.
	 * @param footer
	 *            the code that comes after the end brace.
	 * @return the new child {@code Block}.
	 */
	public static JavaBlock newJavaBlock(String header, String footer) {
		if (header == null) {
			header = "{";
		} else {
			header += " {";
		}
		if (footer == null) {
			footer = "}";
		} else {
			footer = "} " + footer;
		}
		return new JavaBlock(null, header, "\t", footer);
	}

	/**
	 * Adds a {@code Block} of java code as a child of this one.
	 * 
	 * @param header
	 *            the code that comes before the start brace.
	 * @return the new child {@code Block}.
	 */
	public static JavaBlock newJavaBlock(String header) {
		return newJavaBlock(header, null);
	}

	/**
	 * Adds a {@code Block} of java code as a child of this one.
	 * 
	 * @param header
	 *            the code that comes before the start brace.
	 * @param footer
	 *            the code that comes after the end brace.
	 * @return the new child {@code Block}.
	 */
	public static DeclBlock newDeclBlock(String header) {
		if (header == null) {
			header = "{";
		} else {
			header += " {";
		}
		return new DeclBlock(null, header, "\t", "}");
	}

	/**
	 * Adds a {@code Block} of javadoc as a child of this one.
	 * 
	 * @return the new child {@code Block}.
	 */
	public static JavadocBlock newJavadocBlock() {
		return new JavadocBlock();
	}

	/**
	 * Adds a {@code Block} of comments as a child of this one.
	 * 
	 * @return the new child {@code Block}.
	 */
	public static Block newCommentBlock() {
		return new Block(null, "// ", null);
	}
}
