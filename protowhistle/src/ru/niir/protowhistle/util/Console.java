package ru.niir.protowhistle.util;

public class Console {
    private static Console instance = null;
    private StringBuffer buf = new StringBuffer();

    public static Console getInstance() {
        if (instance == null) {
            instance = new Console();
        }
        return instance;
    }

    public void println(final Object obj) {
        if (obj == null) {
            println("null");
        }
        else {
            buf.append(obj.toString() + '\n');
        }
    }

	public void printThrowable(final Throwable t) {
		println(t.toString());
		println(t.getMessage());
	}

	public void printThrowable(final Throwable t, final String comment) {
		if (comment != null) println(comment);
		printThrowable(t);
	}
	
	public String getText() {
		return buf.toString();
	}
	
	public void clear() {
		buf = new StringBuffer();
	}

	public StringBuffer append(boolean arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(char arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(char[] arg0, int arg1, int arg2) {
		return buf.append(arg0, arg1, arg2);
	}

	public StringBuffer append(char[] arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(double arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(float arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(int arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(long arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(Object arg0) {
		return buf.append(arg0);
	}

	public StringBuffer append(String arg0) {
		return buf.append(arg0);
	}
}
