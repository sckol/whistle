package ru.niir.protowhistle.util;

import java.util.Vector;

public class StringUtils {
	public static String[] split(final String value, final char delimiter) {
		char[] valueChars = value.toCharArray();
		int lastIndex = 0;
		Vector strings = null;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			if (c == delimiter) {
				if (strings == null) {
					strings = new Vector();
				}
				strings.addElement( new String( valueChars, lastIndex, i - lastIndex ) );
				lastIndex = i + 1;
			}
		}
		if (strings == null) {
			return new String[]{ value };
		}
		// add tail:
		strings.addElement( new String( valueChars, lastIndex, valueChars.length - lastIndex ));
		String[] ret = new String[strings.size()];
		strings.copyInto(ret);
		return ret;
	}
}
