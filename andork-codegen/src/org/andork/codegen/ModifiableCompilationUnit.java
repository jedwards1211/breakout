package org.andork.codegen;

import static org.andork.japa.JavaParserUtils.getDeclaration;
import static org.andork.japa.JavaParserUtils.getQualifiedName;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.andork.collect.InverseComparator;
import org.andork.japa.JavaParserUtils;

public class ModifiableCompilationUnit {
	private CompilationUnit		unit;

	private List<String>		lines;

	private static final int	TAB_SIZE	= 8;

	public static void main(String[] args) throws Exception {
		List<String> lines = LineUtils.readLines(new FileInputStream("applet/src/gen/" + ModifiableCompilationUnit.class.getName().replace('.', '/') + ".java"));

		ModifiableCompilationUnit compUnit = new ModifiableCompilationUnit(lines);

		System.out.println(compUnit.getNode(ModifiableCompilationUnit.class.getName() + ".main(java.lang.String[])"));
		System.out.println(compUnit.getNode(ModifiableCompilationUnit.class.getName() + ".ModifiableCompilationUnit(List)"));
		System.out.println(compUnit.getNode(ModifiableCompilationUnit.class.getName() + ".parse(java.util.List)"));

		Method m = Modifier.class.getMethod("replace", LineRegion.class, String.class);
		System.out.println(getQualifiedName(m));

		System.out.println(compUnit.getNode(getQualifiedName(m)));

		compUnit.modify()
				.replace(LineUtils.region(compUnit.getNode(ModifiableCompilationUnit.class.getName() + ".main(java.lang.String[])")),
						"public static void main(String[] args) { }")
				.replace(LineUtils.region(compUnit.getNode(ModifiableCompilationUnit.class.getName() + ".lines")),
						"public List<String> blah;")
				.commit();
		System.out.println(compUnit.unit);

		System.out.println(getDeclaration(compUnit.unit, Modifier.class));
		System.out.println(getDeclaration(compUnit.unit, Modifier.class.getMethod("replace", LineRegion.class, String.class)));
		System.out.println(getDeclaration(compUnit.unit, Modifier.class.getDeclaredField("replacements")));
	}

	public ModifiableCompilationUnit(List<String> lines) throws Exception {
		unit = parse(lines);
		this.lines = Collections.unmodifiableList(new ArrayList<String>(lines));
	}

	public Modifier modify() {
		return new Modifier();
	}

	public List<String> getLines() {
		return lines;
	}

	public CompilationUnit getCompilationUnit() {
		return unit;
	}

	private static void write(List<String> lines, Writer writer) throws Exception {
		for (String line : lines) {
			writer.write(line);
			writer.write("\n");
		}
	}

	private static CompilationUnit parse(List<String> lines) throws Exception {
		StringWriter writer = new StringWriter();
		write(lines, writer);
		CompilationUnit result = JavaParser.parse(new ByteArrayInputStream(writer.getBuffer().toString().getBytes()));

		return result;
	}

	public class Modifier {
		private TreeSetMultiMap<LineRegion, String>	replacements	= TreeSetMultiMap.newInstance(new InverseComparator<LineRegion>(LineRegion.OVERLAP_FORBIDDING_COMPARATOR));

		public Modifier replace(LineRegion region, String replacement) {
			replacements.put(region, replacement);
			return this;
		}

		public Modifier delete(LineRegion region) {
			return replace(region, "");
		}

		public Modifier insert(int line, int column, String insertion) {
			return replace(new LineRegion(line, column), insertion);
		}

		public void commit() throws Exception {
			List<String> newLines = new ArrayList<String>(lines);
			for (Map.Entry<LineRegion, String> entry : replacements.entrySet()) {
				LineRegion.replace(newLines, entry.getKey(), entry.getValue(), TAB_SIZE);
			}

			try {
				unit = parse(newLines);
				lines = Collections.unmodifiableList(newLines);

				replacements = null;
			} catch (Exception t) {
				for (String line : newLines) {
					System.out.println(line);
				}
				throw t;
			}
		}
	}

	public Node getNode(String fqName) {
		return JavaParserUtils.getNode(unit, fqName);
	}
}
