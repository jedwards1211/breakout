package org.andork.swing.text;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PatternDocumentFilter extends DocumentFilter {
	Pattern	pattern;
	boolean	enabled	= true;

	public PatternDocumentFilter(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		String newText = getNewText(fb, "", offset, length);
		if (!enabled || pattern == null || pattern.matcher(newText).matches()) {
			fb.remove(offset, length);
		}
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		String newText = getNewText(fb, string, offset, 0);
		if (!enabled || pattern == null || pattern.matcher(newText).matches()) {
			fb.insertString(offset, string, attr);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		String newText = getNewText(fb, text, offset, length);
		if (!enabled || pattern == null || pattern.matcher(newText).matches()) {
			fb.replace(offset, length, text, attrs);
		}
	}

	private String getNewText(FilterBypass fb, String insertText, int offset, int length) {
		try {
			return getNewText(fb.getDocument().getText(0, fb.getDocument().getLength()), insertText, offset, length);
		} catch (BadLocationException e) {
			return null;
		}
	}

	private String getNewText(String inputText, String insertText, int offset, int length) {
		StringBuffer sb = new StringBuffer();
		sb.append(inputText.substring(0, offset));
		if (insertText != null) {
			sb.append(insertText);
		}
		sb.append(inputText.substring(offset + length, inputText.length()));
		return sb.toString();
	}
}
