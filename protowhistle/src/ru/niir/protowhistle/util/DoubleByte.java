package ru.niir.protowhistle.util;

public class DoubleByte {
	
	private int msb;
	private int lsb;
	
	public DoubleByte() {
		
	}
	
	/**
	 * Decomposes a 16bit int into high and low bytes
	 * 
	 * @param val
	 */
	public DoubleByte(int val) {
		if (val > 0xFFFF || val < 0) {
			throw new IllegalArgumentException("value is out of range");
		}
		
		// split address into high and low bytes
		msb = val >> 8;
		lsb = val & 0xFF;
	}

	/**
	 * Constructs a 16bit value from two bytes (high and low)
	 * 
	 * @param msb
	 * @param lsb
	 */
	public DoubleByte(int msb, int lsb) {
		
		if (msb > 0xFF || lsb > 0xFF) {
			throw new IllegalArgumentException("msb or lsb are out of range");
		}

		this.msb = msb;
		this.lsb = lsb;
	}
	
	public int getMsb() {
		return msb;
	}

	public int getLsb() {
		return lsb;
	}	
	
	public int get16BitValue() {
		return (this.msb << 8) + this.lsb;
	}

	public void setMsb(int msb) {
		this.msb = msb;
	}

	public void setLsb(int lsb) {
		this.lsb = lsb;
	}
	
	public int[] getArray() {
		return new int[] { this.msb, this.lsb };
	}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleByte that = (DoubleByte) o;

        if (lsb != that.lsb) return false;
        if (msb != that.msb) return false;

        return true;
    }

    public int hashCode() {
        int result = msb;
        result = 31 * result + lsb;
        return result;
    }
}
