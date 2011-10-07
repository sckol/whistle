package ru.niir.protowhistle.util;


/**
 * This class contains various static utility methods performing operations on
 * arrays, and a method to provide a List "view" of an array to facilitate
 * using arrays with Collection-based APIs. All methods throw a
 * {@link NullPointerException} if the parameter array is null.
 * <p>
 *
 * Implementations may use their own algorithms, but must obey the general
 * properties; for example, the sort must be stable and n*log(n) complexity.
 * Sun's implementation of sort, and therefore ours, is a tuned quicksort,
 * adapted from Jon L. Bentley and M. Douglas McIlroy's "Engineering a Sort
 * Function", Software-Practice and Experience, Vol. 23(11) P. 1249-1265
 * (November 1993). This algorithm offers n*log(n) performance on many data
 * sets that cause other quicksorts to degrade to quadratic performance.
 *
 * @author Original author unknown
 * @author Bryce McKinlay
 * @author Eric Blake (ebb9@email.byu.edu)
 * @see Comparable
 * @see Comparator
 * @since 1.2
 * @status updated to 1.4
 */
public class Arrays
{
  /**
   * This class is non-instantiable.
   */
  private Arrays()
  {
  }


// equals
  /**
   * Compare two boolean arrays for equality.
   *
   * @param a1 the first array to compare
   * @param a2 the second array to compare
   * @return true if a1 and a2 are both null, or if a2 is of the same length
   *         as a1, and for each 0 <= i < a1.length, a1[i] == a2[i]
   */
  public static boolean equals(boolean[] a1, boolean[] a2)
  {
    // Quick test which saves comparing elements of the same array, and also
    // catches the case that both are null.
    if (a1 == a2)
      return true;

    if (null == a1 || null == a2)
      return false;
    
    // If they're the same length, test each element
    if (a1.length == a2.length)
      {
	int i = a1.length;
	while (--i >= 0)
	  if (a1[i] != a2[i])
	    return false;
	return true;
      }
    return false;
  }

  /**
   * Compare two byte arrays for equality.
   *
   * @param a1 the first array to compare
   * @param a2 the second array to compare
   * @return true if a1 and a2 are both null, or if a2 is of the same length
   *         as a1, and for each 0 <= i < a1.length, a1[i] == a2[i]
   */
  public static boolean equals(byte[] a1, byte[] a2)
  {
    // Quick test which saves comparing elements of the same array, and also
    // catches the case that both are null.
    if (a1 == a2)
      return true;

    if (null == a1 || null == a2)
      return false;

    // If they're the same length, test each element
    if (a1.length == a2.length)
      {
	int i = a1.length;
	while (--i >= 0)
	  if (a1[i] != a2[i])
	    return false;
	return true;
      }
    return false;
  }

  /**
   * Compare two char arrays for equality.
   *
   * @param a1 the first array to compare
   * @param a2 the second array to compare
   * @return true if a1 and a2 are both null, or if a2 is of the same length
   *         as a1, and for each 0 <= i < a1.length, a1[i] == a2[i]
   */
  public static boolean equals(char[] a1, char[] a2)
  {
    // Quick test which saves comparing elements of the same array, and also
    // catches the case that both are null.
    if (a1 == a2)
      return true;

    if (null == a1 || null == a2)
      return false;
    
    // If they're the same length, test each element
    if (a1.length == a2.length)
      {
	int i = a1.length;
	while (--i >= 0)
	  if (a1[i] != a2[i])
	    return false;
	return true;
      }
    return false;
  }

  /**
   * Compare two short arrays for equality.
   *
   * @param a1 the first array to compare
   * @param a2 the second array to compare
   * @return true if a1 and a2 are both null, or if a2 is of the same length
   *         as a1, and for each 0 <= i < a1.length, a1[i] == a2[i]
   */
  public static boolean equals(short[] a1, short[] a2)
  {
    // Quick test which saves comparing elements of the same array, and also
    // catches the case that both are null.
    if (a1 == a2)
      return true;

    if (null == a1 || null == a2)
      return false;

    // If they're the same length, test each element
    if (a1.length == a2.length)
      {
	int i = a1.length;
	while (--i >= 0)
	  if (a1[i] != a2[i])
	    return false;
	return true;
      }
    return false;
  }

  /**
   * Compare two int arrays for equality.
   *
   * @param a1 the first array to compare
   * @param a2 the second array to compare
   * @return true if a1 and a2 are both null, or if a2 is of the same length
   *         as a1, and for each 0 <= i < a1.length, a1[i] == a2[i]
   */
  public static boolean equals(int[] a1, int[] a2)
  {
    // Quick test which saves comparing elements of the same array, and also
    // catches the case that both are null.
    if (a1 == a2)
      return true;

    if (null == a1 || null == a2)
      return false;

    // If they're the same length, test each element
    if (a1.length == a2.length)
      {
	int i = a1.length;
	while (--i >= 0)
	  if (a1[i] != a2[i])
	    return false;
	return true;
      }
    return false;
  }

  /**
   * Compare two long arrays for equality.
   *
   * @param a1 the first array to compare
   * @param a2 the second array to compare
   * @return true if a1 and a2 are both null, or if a2 is of the same length
   *         as a1, and for each 0 <= i < a1.length, a1[i] == a2[i]
   */
  public static boolean equals(long[] a1, long[] a2)
  {
    // Quick test which saves comparing elements of the same array, and also
    // catches the case that both are null.
    if (a1 == a2)
      return true;

    if (null == a1 || null == a2)
      return false;

    // If they're the same length, test each element
    if (a1.length == a2.length)
      {
	int i = a1.length;
	while (--i >= 0)
	  if (a1[i] != a2[i])
	    return false;
	return true;
      }
    return false;
  }

  // fill
  /**
   * Fill an array with a boolean value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(boolean[] a, boolean val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a boolean value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(boolean[] a, int fromIndex, int toIndex, boolean val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with a byte value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(byte[] a, byte val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a byte value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(byte[] a, int fromIndex, int toIndex, byte val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with a char value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(char[] a, char val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a char value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(char[] a, int fromIndex, int toIndex, char val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with a short value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(short[] a, short val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a short value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(short[] a, int fromIndex, int toIndex, short val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with an int value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(int[] a, int val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with an int value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(int[] a, int fromIndex, int toIndex, int val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with a long value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(long[] a, long val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a long value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(long[] a, int fromIndex, int toIndex, long val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with a float value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(float[] a, float val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a float value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(float[] a, int fromIndex, int toIndex, float val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with a double value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   */
  public static void fill(double[] a, double val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with a double value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(double[] a, int fromIndex, int toIndex, double val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }

  /**
   * Fill an array with an Object value.
   *
   * @param a the array to fill
   * @param val the value to fill it with
   * @throws ClassCastException if val is not an instance of the element
   *         type of a.
   */
  public static void fill(Object[] a, Object val)
  {
    fill(a, 0, a.length, val);
  }

  /**
   * Fill a range of an array with an Object value.
   *
   * @param a the array to fill
   * @param fromIndex the index to fill from, inclusive
   * @param toIndex the index to fill to, exclusive
   * @param val the value to fill with
   * @throws ClassCastException if val is not an instance of the element
   *         type of a.
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0
   *         || toIndex &gt; a.length
   */
  public static void fill(Object[] a, int fromIndex, int toIndex, Object val)
  {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException();
    for (int i = fromIndex; i < toIndex; i++)
      a[i] = val;
  }


// sort
  // Thanks to Paul Fisher (rao@gnu.org) for finding this quicksort algorithm
  // as specified by Sun and porting it to Java. The algorithm is an optimised
  // quicksort, as described in Jon L. Bentley and M. Douglas McIlroy's
  // "Engineering a Sort Function", Software-Practice and Experience, Vol.
  // 23(11) P. 1249-1265 (November 1993). This algorithm gives n*log(n)
  // performance on many arrays that would take quadratic time with a standard
  // quicksort.

  /** 
   * Returns the hashcode of an array of long numbers.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents longs in their wrapper class, <code>Long</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of long numbers for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(long[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      {
	int elt = (int) (v[i] ^ (v[i] >>> 32));
	result = 31 * result + elt;
      }
    return result;
  }

  /** 
   * Returns the hashcode of an array of integer numbers.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents ints in their wrapper class, <code>Integer</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of integer numbers for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(int[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      result = 31 * result + v[i];
    return result;
  }

  /** 
   * Returns the hashcode of an array of short numbers.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents shorts in their wrapper class, <code>Short</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of short numbers for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(short[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      result = 31 * result + v[i];
    return result;
  }

  /** 
   * Returns the hashcode of an array of characters.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents chars in their wrapper class, <code>Character</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of characters for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(char[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      result = 31 * result + v[i];
    return result;
  }

  /** 
   * Returns the hashcode of an array of bytes.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents bytes in their wrapper class, <code>Byte</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of bytes for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(byte[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      result = 31 * result + v[i];
    return result;
  }

  /** 
   * Returns the hashcode of an array of booleans.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents booleans in their wrapper class,
   * <code>Boolean</code>.  For <code>null</code>, 0 is returned.
   *
   * @param v an array of booleans for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(boolean[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      result = 31 * result + (v[i] ? 1231 : 1237);
    return result;
  }

  /** 
   * Returns the hashcode of an array of floats.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents floats in their wrapper class, <code>Float</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of floats for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(float[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      result = 31 * result + Float.floatToIntBits(v[i]);
    return result;
  }

  /** 
   * Returns the hashcode of an array of doubles.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  This has the same
   * data, but represents doubles in their wrapper class, <code>Double</code>.
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of doubles for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(double[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      {
	long l = Double.doubleToLongBits(v[i]);
	int elt = (int) (l ^ (l >>> 32));
	result = 31 * result + elt;
      }
    return result;
  }

  /** 
   * Returns the hashcode of an array of objects.  If two arrays
   * are equal, according to <code>equals()</code>, they should have the
   * same hashcode.  The hashcode returned by the method is equal to that
   * obtained by the corresponding <code>List</code> object.  
   * For <code>null</code>, 0 is returned.
   *
   * @param v an array of integer numbers for which the hash code should be
   *          computed.
   * @return the hash code of the array, or 0 if null was given.
   * @since 1.5 
   */
  public static int hashCode(Object[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      {
	int elt = v[i] == null ? 0 : v[i].hashCode();
	result = 31 * result + elt;
      }
    return result;
  }

  public static int deepHashCode(Object[] v)
  {
    if (v == null)
      return 0;
    int result = 1;
    for (int i = 0; i < v.length; ++i)
      {
	int elt;
	if (v[i] == null)
	  elt = 0;
	else if (v[i] instanceof boolean[])
	  elt = hashCode((boolean[]) v[i]);
	else if (v[i] instanceof byte[])
	  elt = hashCode((byte[]) v[i]);
	else if (v[i] instanceof char[])
	  elt = hashCode((char[]) v[i]);
	else if (v[i] instanceof short[])
	  elt = hashCode((short[]) v[i]);
	else if (v[i] instanceof int[])
	  elt = hashCode((int[]) v[i]);
	else if (v[i] instanceof long[])
	  elt = hashCode((long[]) v[i]);
	else if (v[i] instanceof float[])
	  elt = hashCode((float[]) v[i]);
	else if (v[i] instanceof double[])
	  elt = hashCode((double[]) v[i]);
	else if (v[i] instanceof Object[])
	  elt = hashCode((Object[]) v[i]);
	else
	  elt = v[i].hashCode();
	result = 31 * result + elt;
      }
    return result;
  }
}
