package org.andork.format;

import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class CollectionFormat<T, C extends Collection<T>> implements Format<C> {
	public static void main(String[] args) throws Exception {
		CollectionFormat<String, Set<String>> format = new CollectionFormat<String, Set<String>>(',',
				StringFormat.instance,
				() -> new HashSet<String>());
		System.out.println(format.parse("  a , b,  \"cbd\"\"\"\"\"abc, e,"));
	}

	private char parseDelimiter;
	private String formatDelimiter;
	private Format<T> itemFormat;

	Supplier<C> collectionSupplier;

	public CollectionFormat(char delimiter, Format<T> itemFormat, Supplier<C> collectionSupplier) {
		this(delimiter, delimiter + " ", itemFormat, collectionSupplier);
	}

	public CollectionFormat(char parseDelimiter, String formatDelimiter, Format<T> itemFormat,
			Supplier<C> collectionSupplier) {
		super();
		this.parseDelimiter = parseDelimiter;
		this.formatDelimiter = formatDelimiter;
		this.itemFormat = itemFormat;
		this.collectionSupplier = collectionSupplier;
	}

	@Override
	public String format(C t) {
		if (t == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (T item : t) {
			String s = itemFormat.format(item);
			if (sb.length() > 0) {
				sb.append(formatDelimiter);
			}
			if (s.indexOf(parseDelimiter) >= 0) {
				sb.append('"').append(s.replaceAll("\"", "\"\"")).append('"');
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	private String nextItem(StringCharacterIterator i) {
		StringBuilder sb = null;

		while (i.current() != '\uffff') {
			if (i.current() == parseDelimiter) {
				i.next();
				break;
			} else {
				if (sb == null) {
					sb = new StringBuilder();
				}
				if (i.current() == '"') {
					readUntilEndQuote(i, sb);
				} else {
					sb.append(i.current());
					i.next();
				}
			}
		}

		return sb == null ? null : sb.toString().trim();
	}

	@Override
	public C parse(String s) throws Exception {
		if (s == null) {
			return collectionSupplier.get();
		}
		if (parseDelimiter == ' ') {
			s = s.replaceAll("\\s+", " ");
		}
		C result = collectionSupplier.get();
		StringCharacterIterator i = new StringCharacterIterator(s);

		String item;
		while ((item = nextItem(i)) != null) {
			result.add(itemFormat.parse(item));
		}
		return result;
	}

	private void readUntilEndQuote(StringCharacterIterator i, StringBuilder sb) {
		while (true) {
			char c = i.next();
			if (c == '"') {
				char c2 = i.next();
				if (c2 != '"') {
					return;
				}
			}
			sb.append(c);
		}
	}
}
