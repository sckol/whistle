package ru.niir.protowhistle.util;

import java.util.Vector;

/**
 * TODO replace with nio.IntBuffer 
 */
public class IntArrayOutputStream implements IIntArray {

	private Vector intList = new Vector();
	
	public IntArrayOutputStream() {

	}
	
	public void write (int val) {
		intList.addElement(new Integer(val));
	}
	
	public void write(int[] val) {
        for (int i = 0; i < val.length; i++) {
        	final int aVal = val[i];
            this.write(aVal);
        }
	}
	
	public int[] getIntArray() {
		//int[] integer = (int[]) intList.toArray(new int[0]);
		// TODO there has got to be a better way -- how to convert list to int[] array?
		int[] intArr = new int[intList.size()];
		
		int i = 0;
		
		for (int j = 0; j < intList.size(); j++) {
			final Integer integer = (Integer) intList.elementAt(j);
			intArr[i++] = integer.intValue();
		}
		
		return intArr;
	}

	public Vector getInternalList() {
		return intList;
	}	
}
