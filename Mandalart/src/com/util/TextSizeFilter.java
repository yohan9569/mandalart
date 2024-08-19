package com.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TextSizeFilter extends DocumentFilter {
	private final int maxCharacters;

    public TextSizeFilter(int maxChars) {
        this.maxCharacters = maxChars;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException {
        if (str == null) {
            str = "";
        }
        int newLength = fb.getDocument().getLength() + str.length() - length;
        if (newLength <= maxCharacters) {
            super.replace(fb, offset, length, str, attrs);
        } else {
            int allowedLength = maxCharacters - (fb.getDocument().getLength() - length);
            if (allowedLength > 0) {
                str = str.substring(0, allowedLength);
                super.replace(fb, offset, length, str, attrs);
            }
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }
        int newLength = fb.getDocument().getLength() + str.length();
        if (newLength <= maxCharacters) {
            super.insertString(fb, offset, str, attr);
        } else {
            int allowedLength = maxCharacters - fb.getDocument().getLength();
            if (allowedLength > 0) {
                str = str.substring(0, allowedLength);
                super.insertString(fb, offset, str, attr);
            }
        }
    }
}