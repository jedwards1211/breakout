package org.andork.codegen;

import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.expr.AnnotationExpr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LineUtils {
	public static List<String> readLines(InputStream in) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			List<String> lines = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static LineRegion fullRegion(Node node) {
		LineRegion result = region(node);
		if (node instanceof BodyDeclaration) {
			BodyDeclaration bodyDecl = (BodyDeclaration) node;
			if (bodyDecl.getAnnotations() != null) {
				for (AnnotationExpr annotation : bodyDecl.getAnnotations()) {
					result = result.union(region(annotation));
				}
			}
			if (bodyDecl.getJavaDoc() != null) {
				result = result.union(region(bodyDecl.getJavaDoc()));
			}
		}
		return result;
	}

	public static LineRegion region(Node node) {
		return new LineRegion(node.getBeginLine() - 1, node.getBeginColumn() - 1, node.getEndLine() - 1, node.getEndColumn() - 1);
	}
}
