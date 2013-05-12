
/*
 * Copyright (c) 2003, The Regents of the University of California, through
 * Lawrence Berkeley National Laboratory (subject to receipt of any required
 * approvals from the U.S. Dept. of Energy). All rights reserved.
 */
package ru.niir.meshlogic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * Efficient resizable auto-expanding list holding <code>byte</code> elements;
 * implemented with arrays. The API is intended for easy non-trivial
 * high-throughput processing, and (in an elegant and compact yet different
 * form) provides all list and set functionality of the java.util collections
 * package, as well as a little more. This API fills the gap between raw arrays
 * (non-resizable), nio ByteBuffers (non-resizable) and java.util.List and
 * java.util.Set (resizable but not particularly useful for <i>non-trivial
 * high-throughput</i> processing on primitive types). For example, this class
 * is convenient to parse and/or assemble variable-sized network messages if
 * message lengths are a priori unknown.
 * <p>
 * Indexed element access is zero based: valid indexes range from index
 * <code>0</code> (inclusive) to index <code>list.size()</code> (exclusive).
 * Attempts to access out-of-range indexes will throw an
 * {@link IndexOutOfBoundsException}.
 * <p>
 * <strong>Note that this implementation is not synchronized, hence not
 * inherently thread safe.</strong>
 * <p>
 * Example usage:
 * 
 * <pre>
 * System.out.println(new ArrayByteList(new byte[] { 0, 1, 2 }));
 * 
 * // insert and replace
 * ArrayByteList demo = new ArrayByteList(new byte[] { 0, 1, 2 });
 * demo.replace(0, 0, new byte[] { 4, 5 }); // insert
 * System.out.println(demo); // yields [4,5,0,1,2]
 * demo.replace(0, 2, new byte[] { 6, 7, 8, 9 });
 * System.out.println(demo); // yields [6,7,8,9,0,1,2]
 * 
 * // sort, search and remove
 * System.out.println(demo.subList(1, 3)); // yields [7,8]
 * demo.sort(true);
 * System.out.println(demo);
 * System.out.println(demo.binarySearch((byte) 7));
 * demo.remove(4, 4 + 1); // remove elem at index 4
 * System.out.println(demo);
 * System.out.println(demo.binarySearch((byte) 7));
 * 
 * // efficient file I/O
 * System.out.println(new ArrayByteList(0).add(
 * 		new java.io.FileInputStream(&quot;/etc/passwd&quot;)).toString(null));
 * new java.io.FileOutputStream(&quot;/tmp/test&quot;).write(demo.asArray(), 0, demo.size());
 * System.out.println(new ArrayByteList(0).add(new java.io.FileInputStream(
 * 		&quot;/tmp/test&quot;)));
 * System.out.println(new ArrayByteList(0).add(new java.io.FileInputStream(
 * 		&quot;/tmp/test&quot;).getChannel()));
 * 
 * // network I/O via stream
 * java.nio.charset.Charset charset = java.nio.charset.Charset
 * 		.forName(&quot;ISO-8859-1&quot;);
 * System.out.println(new ArrayByteList(0).add(
 * 		new java.net.URL(&quot;http://www.google.com&quot;).openStream()).toString(
 * 		charset));
 * 
 * // simple HTTP via raw socket channel
 * java.nio.channels.SocketChannel channel = java.nio.channels.SocketChannel
 * 		.open();
 * channel.connect(new java.net.InetSocketAddress(&quot;www.google.com&quot;, 80));
 * channel.write(new ArrayByteList(&quot;GET / HTTP/1.0&quot; + &quot;\r\n\r\n&quot;, charset)
 * 		.asByteBuffer());
 * System.out.println(new ArrayByteList(0).add(channel).toString(charset));
 * </pre>
 * <p>
 * Manipulating primitive values other than bytes is not directly supported.
 * However, this can be done via <code>asByteBuffer()</code> along the following
 * lines:
 * 
 * <pre>
 *     // get and set 4 byte integer value at end of list:
 *     list = ...
 *     int val = list.asByteBuffer().getInt(list.size() - 4);
 *     list.asByteBuffer().setInt(list.size() - 4, val * 10);
 *         
 *     // append 8 byte double value:
 *     list = ...
 *     double elemToAdd = 1234.0;
 *     list.replace(list.size(), list.size(), (byte)0, 8); // add 8 bytes at end
 *     list.asByteBuffer().putDouble(list.size() - 8, elemToAdd);
 *  
 *     // insert 8 byte double value at beginning:
 *     list = ...
 *     double elemToInsert = 1234.0;
 *     list.replace(0, 0, 0, 8); // insert 8 bytes at beginning
 *     list.asByteBuffer().putDouble(0, elemToInsert);
 * </pre>
 * 
 * This class requires JDK 1.4 or higher; otherwise it has zero dependencies.
 * Hence you can simply copy the file into your own project if minimal
 * dependencies are desired.
 * <p>
 * Also note that the compiler can (and will) easily inline all methods, then
 * optimize away. In fact with the Sun jdk-1.4.2 server VM it is hard to measure
 * any difference to raw array manipulations at abstraction level zero.
 * 
 * @author whoschek@lbl.gov
 * @author $Author: hoschek3 $
 * @version $Revision: 1.52 $, $Date: 2004/12/01 21:00:24 $
 */
public final class ArrayByteList implements java.io.Serializable {

	/**
	 * The array into which the elements of the list are stored. The capacity of
	 * the list is the length of this array.
	 */
	private transient byte[] elements;

	/**
	 * The current number of elements contained in this list.
	 */
	private int size;

	/**
	 * For compatibility across versions
	 */
	private static final long serialVersionUID = -6250350905005960078L;

	/**
	 * The default charset is UTF-8, and fixed for interoperability. Note that
	 * the first 128 character codes of UTF-8, ISO-8859-1 and US-ASCII are
	 * identical. Hence these charsets are equivalent for most practical
	 * purposes. This charset is used on most operating systems (e.g. for system
	 * files, config files, log files, scripts, source code, etc.) Note that in
	 * Java UTF-8 and ISO-8859-1 always works since JDK support for it is
	 * required by the JDK spec.
	 */
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * Constructs an empty list.
	 */
	public ArrayByteList() {
		this(64);
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the number of elements the receiver can hold without
	 *            auto-expanding itself by allocating new internal memory.
	 */
	public ArrayByteList(int initialCapacity) {
		elements = new byte[initialCapacity];
		size = 0;
	}

	/**
	 * Constructs a list SHARING the specified elements. The initial size and
	 * capacity of the list is the length of the backing array.
	 * <p>
	 * <b>WARNING: </b> For efficiency reasons and to keep memory usage low,
	 * <b>the array is SHARED, not copied </b>. So if subsequently you modify
	 * the specified array directly via the [] operator, be sure you know what
	 * you're doing.
	 * <p>
	 * If you rather need copying behaviour, use
	 * <code>copy = new ArrayByteList(byte[] elems).copy()</code> or similar.
	 * <p>
	 * If you need a list containing a copy of <code>elems[from..to)</code>, use
	 * <code>list = new ArrayByteList(to-from).add(elems, from, to-from)</code>
	 * or
	 * <code>list = new ArrayByteList(ByteBuffer.wrap(elems, from, to-from))</code>
	 * or similar.
	 * 
	 * @param elems
	 *            the array backing the constructed list
	 */
	public ArrayByteList(byte[] elems) {
		elements = elems;
		size = elems.length;
	}

	/**
	 * Constructs a list containing a copy of the remaining buffer elements. The
	 * initial size and capacity of the list is <code>elems.remaining()</code>.
	 * 
	 * @param elems
	 *            the elements initially to be added to the list
	 */
	public ArrayByteList(ByteBuffer elems) {
		this(elems.remaining());
		add(elems);
	}

	/**
	 * Constructs a list containing a copy of the encoded form of the given char
	 * sequence (String, StringBuffer, CharBuffer, etc).
	 * 
	 * @param str
	 *            the string to convert.
	 * @param charset
	 *            the charset to convert with (e.g.
	 *            <code>Charset.forName("US-ASCII")</code>,
	 *            <code>Charset.forName("ISO-8859-1")</code>). If
	 *            <code>null</code> uses <code>Charset.forName("UTF-8")</code>
	 *            as the default charset.
	 */
	public ArrayByteList(CharSequence str, Charset charset) {
		this(getCharset(charset).encode(CharBuffer.wrap(str)));
	}

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param elem
	 *            element to be appended to this list.
	 * @return <code>this</code> (for chaining convenience only)
	 */
	public ArrayByteList add(byte elem) {
		if (size == elements.length)
			ensureCapacity(size + 1);
		elements[size++] = elem;
		return this;
		// equally correct alternative impl: insert(size, elem);
	}

	/**
	 * Appends the elements in the range <code>[offset..offset+length)</code> to
	 * the end of this list.
	 * 
	 * @param elems
	 *            the elements to be appended
	 * @param offset
	 *            the offset of the first element to add (inclusive)
	 * @param length
	 *            the number of elements to add
	 * @return <code>this</code> (for chaining convenience only)
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 */
	public ArrayByteList add(byte[] elems, int offset, int length) {
		if (offset < 0 || length < 0 || offset + length > elems.length)
			throw new IndexOutOfBoundsException("offset: " + offset
					+ ", length: " + length + ", elems.length: " + elems.length);
		ensureCapacity(size + length);
		System.arraycopy(elems, offset, this.elements, size, length);
		size += length;
		return this;
		// equally correct alternative impl: replace(size, size, elems, offset,
		// length);
	}

	/**
	 * Appends the specified elements to the end of this list.
	 * 
	 * @param elems
	 *            elements to be appended.
	 * @return <code>this</code> (for chaining convenience only)
	 */
	public ArrayByteList add(ArrayByteList elems) {
		replace(size, size, elems);
		return this;
	}

	/**
	 * Appends the remaining buffer elements to the end of this list.
	 * 
	 * @param elems
	 *            elements to be appended.
	 * @return <code>this</code> (for chaining convenience only)
	 */
	public ArrayByteList add(ByteBuffer elems) {
		int length = elems.remaining();
		ensureCapacity(size + length);
		elems.get(this.elements, size, length);
		size += length;
		return this;
		// equally correct alternative impl: replace(size, size, elems,
		// elems.remaining());
	}

	/**
	 * Appends the encoded form of the given char sequence (String,
	 * StringBuffer, CharBuffer, etc).
	 * 
	 * @param str
	 *            the string to convert.
	 * @param charset
	 *            the charset to convert with (e.g.
	 *            <code>Charset.forName("US-ASCII")</code>,
	 *            <code>Charset.forName("ISO-8859-1")</code>). If
	 *            <code>null</code> uses <code>Charset.forName("UTF-8")</code>
	 *            as the default charset.
	 * @return <code>this</code> (for chaining convenience only)
	 */
	public ArrayByteList add(CharSequence str, Charset charset) {
		return add(getCharset(charset).encode(CharBuffer.wrap(str)));
	}

	/**
	 * Appends the remaining elements of the stream to the end of this list,
	 * reading until end-of-stream. Finally closes the stream. Note that the
	 * implementation is efficient even if the input stream is not a buffered
	 * stream.
	 * 
	 * @param elems
	 *            the input stream to read from.
	 * @return <code>this</code> (for chaining convenience only)
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public ArrayByteList add(InputStream elems) throws IOException {
		// Note that our algo is correct and efficient even if
		// the input stream implements available() in weird or buggy ways.
		try {
			ensureCapacity(size + 1 + Math.max(0, elems.available()));
			int n;
			while ((n = elems.read(elements, size, elements.length - size)) >= 0) {
				size += n;
				// increasingly make room for next read (and defensively
				// ensure we don't spin loop, attempting to read zero bytes per
				// iteration)
				ensureCapacity(size + Math.max(1, elems.available()));
			}
		} finally {
			if (elems != null)
				elems.close();
		}
		return this;
	}

	/**
	 * Appends the remaining elements of the channel to the end of this list,
	 * reading until end-of-stream. Finally closes the channel.
	 * 
	 * @param elems
	 *            the channel to read from.
	 * @return <code>this</code> (for chaining convenience only)
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public ArrayByteList add(ReadableByteChannel elems) throws IOException {
		try {
			int remaining = 8192;
			if (elems instanceof FileChannel) { // we can be more efficient
				long rem = ((FileChannel) elems).size()
						- ((FileChannel) elems).position();
				if (size + 1 + rem > Integer.MAX_VALUE)
					throw new IllegalArgumentException(
							"File channel too large (2 GB limit d)");
				remaining = (int) rem;
			}
			ensureCapacity(size + 1 + remaining);
			int n;
			while ((n = elems.read(ByteBuffer.wrap(elements, size,
					elements.length - size))) >= 0) {
				size += n;
				// increasingly make room for next read (and defensively
				// ensure we don't spin loop, attempting to read zero bytes per
				// iteration)
				ensureCapacity(size + 1);
			}
		} finally {
			if (elems != null)
				elems.close();
		}
		return this;
	}

	/**
	 * Returns the elements currently stored, including invalid elements between
	 * size and capacity, if any.
	 * <p>
	 * <b>WARNING: </b> For efficiency reasons and to keep memory usage low,
	 * <b>the array is SHARED, not copied </b>. So if subsequently you modify
	 * the returned array directly via the [] operator, be sure you know what
	 * you're doing.
	 * 
	 * @return the elements currently stored.
	 */
	public byte[] asArray() {
		return elements;
	}

	/**
	 * Returns a buffer SHARING elements with the receiver. The buffer will have
	 * the default NIO byte order, which is ByteOrder.BIG_ENDIAN.
	 * <p>
	 * <b>WARNING: </b> For efficiency reasons and to keep memory usage low,
	 * <b>the array is SHARED, not copied </b>. So if subsequently you modify
	 * the returned buffer, be sure you know what you're doing.
	 */
	public ByteBuffer asByteBuffer() {
		return ByteBuffer.wrap(elements, 0, size);
	}

	/**
	 * Creates and returns an unsynchronized output stream that appends to this
	 * SHARED backing byte list. Useful if legacy code requires adapting to a
	 * stream based interface. Note: This is more efficient and straighforward
	 * than using a {@link java.io.ByteArrayOutputStream}.
	 * <p>
	 * Writing to the stream means adding (appending) elements to the end of the
	 * (auto-expanding) backing list.
	 * <p>
	 * Closing the stream has no effect. The stream's methods can be called
	 * after the stream has been closed without generating an IOException. In
	 * fact the stream implementation never ever throws an IOException.
	 * <p>
	 * If your legacy code requires adapting to an {@link InputStream} instead,
	 * simply use the non-copying {@link java.io.ByteArrayInputStream}, for
	 * example as in
	 * <code>new java.io.ByteArrayInputStream(list.asArray(), 0, list.size())</code>.
	 * 
	 * @return the stream
	 */
	public OutputStream asOutputStream() {
		return new OutputStream() {
			public void write(int b) {
				add((byte) b);
			}

			public void write(byte b[], int off, int len) {
				add(b, off, len);
			}
		};
	}

	/**
	 * Searches the list for the specified value using the binary search
	 * algorithm. The list <strong>must </strong> be sorted (as by the
	 * <code>sort</code> method) prior to making this call. If it is not sorted,
	 * the results are undefined. If the list contains multiple elements with
	 * the specified value, there is no guarantee which one will be found.
	 * 
	 * @param key
	 *            the value to be searched for.
	 * @return index of the search key, if it is contained in the list;
	 *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
	 *         <i>insertion point </i> is defined as the point at which the key
	 *         would be inserted into the list: the index of the first element
	 *         greater than the key, or <code>list.size()</code>, if all
	 *         elements in the list are less than the specified key. Note that
	 *         this guarantees that the return value will be >= 0 if and only if
	 *         the key is found.
	 * @see #sort(boolean)
	 */
	public int binarySearch(byte key) {
		int low = 0;
		int high = size - 1;

		while (low <= high) {
			int mid = (low + high) >> 1; // >> 1 is equivalent to divide by 2
			byte midVal = elements[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	/**
	 * Removes all elements but keeps the current capacity; Afterwards
	 * <code>size()</code> will yield zero.
	 * 
	 * @return <code>this</code> (for chaining convenience only)
	 */
	public ArrayByteList clear() {
		size = 0;
		return this;
		// equally correct alternative impl: remove(0, size);
	}

	/**
	 * Constructs and returns a new list that is a deep copy of the receiver.
	 * 
	 * @return a deep copy of the receiver.
	 */
	public ArrayByteList copy() {
		return new ArrayByteList(toArray());
	}

	/**
	 * Compares the specified Object with the receiver. Returns true if and only
	 * if the specified Object is also a list of the same type, both Lists have
	 * the same size, and all corresponding pairs of elements in the two Lists
	 * are identical. In other words, two Lists are defined to be equal if they
	 * contain the same elements in the same order.
	 * 
	 * @param otherObj
	 *            the Object to be compared for equality with the receiver.
	 * @return true if the specified Object is equal to the receiver.
	 */
	public boolean equals(Object otherObj) {
		if (this == otherObj)
			return true;
		if (!(otherObj instanceof ArrayByteList))
			return false;
		ArrayByteList other = (ArrayByteList) otherObj;
		if (size != other.size)
			return false;
		return indexOf(0, size, other) >= 0;
	}

	/**
	 * Ensures that the receiver can hold at least the specified number of
	 * elements without needing to allocate new internal memory. If necessary,
	 * allocates new internal memory and increases the capacity of the receiver.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity.
	 */
	public void ensureCapacity(int minCapacity) {
		if (minCapacity > elements.length) {
			int newCapacity = Math.max(minCapacity,
					(elements.length * 3) / 2 + 1);
			elements = subArray(0, size, newCapacity);
		}
	}

	/**
	 * Finds all matching sublists in the range <code>[from..to)</code> and
	 * replaces them with the given replacement. A sublist matches if it is
	 * equal to the given pattern. The pattern must have a size greater than
	 * zero. The replacement can have any size. Examples:
	 * 
	 * <pre>
	 * [a,b,c,d,b,c].findReplace(0,6, [b,c], [x,y,z]) --> [a,x,y,z,d,x,y,z]
	 * [a,b,c,d,b,c].findReplace(0,6, [b,c], []) --> [a,d]
	 * </pre>
	 * 
	 * @param from
	 *            the index of the first element to search (inclusive)
	 * @param to
	 *            the index of the last element to search (exclusive).
	 * @param pattern
	 *            the sublist to search for
	 * @param replacement
	 *            the elements to replace the found sublists
	 * @return the number of sublists found matching the pattern
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 * @throws IllegalArgumentException
	 *             if pattern.size() == 0.
	 */
	public int findReplace(int from, int to, ArrayByteList pattern,
			ArrayByteList replacement) {
		checkRange(from, to);
		if (pattern.size == 0)
			throw new IllegalArgumentException("pattern size must be > 0");
		int n = 0;
		while ((from = indexOf(from, to, pattern)) >= 0) {
			if (pattern != replacement) { // do more than just counting matches
				replace(from, from + pattern.size, replacement);
				to += replacement.size - pattern.size;
			}
			from += replacement.size;
			n++;
		}
		return n;
	}

	/**
	 * Returns the element at the specified index.
	 * 
	 * @param index
	 *            index of element to return.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range.
	 */
	public byte get(int index) {
		checkIndex(index);
		return elements[index];
	}

	/**
	 * Returns the hash code value for this list. The algorithm ensures that
	 * <code>list1.equals(list2)</code> implies that
	 * <code>list1.hashCode()==list2.hashCode()</code> for any two lists,
	 * <code>list1</code> and <code>list2</code>, as required by the general
	 * contract of <code>Object.hashCode</code>.
	 * <p>
	 * Warning: run time complexity is O(N)
	 * 
	 * @return the hash code value for this list.
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		int hashCode = 1;
		byte[] elems = elements;
		for (int i = size; --i >= 0;)
			hashCode = 31 * hashCode + elems[i];
		return hashCode;
	}

	/**
	 * Returns the index of the first occurrence of the given sublist within the
	 * range <code>this[from..to)</code>. Returns <code>-1</code> if the
	 * receiver does not contain such a sublist. Examples:
	 * 
	 * <pre>
	 * [a,b,c,d,e,b,c,d].indexOf(0,8, [b,c,d]) --> 1
	 * [a,b,c,d,e].indexOf(0,5, [b,c,d]) --> 1
	 * [a,b,c,d,e].indexOf(1,4, [b,c,d]) --> 1
	 * [a,b,c,d,e].indexOf(0,5, [x,y])   --> -1
	 * [a,b,c,d,e].indexOf(0,3, [b,c,d]) --> -1
	 * [a].indexOf(0,1, [a,b,c]) --> -1
	 * [a,b,c,d,e].indexOf(2,3, []) --> 2 // empty sublist is always found
	 * [].indexOf(0,0, []) --> 0
	 * </pre>
	 * 
	 * @param from
	 *            the leftmost search index within the receiver, inclusive.
	 * @param to
	 *            the rightmost search index within the receiver, exclusive.
	 * @param subList
	 *            the sublist to search for.
	 * @return the index of the first occurrence of the sublist in the receiver;
	 *         returns <code>-1</code> if the sublist is not found.
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range
	 */
	public int indexOf(int from, int to, ArrayByteList subList) {
		// brute-force algorithm, but very efficiently implemented
		checkRange(from, to);
		byte[] elems = elements;
		byte[] subElems = subList.elements;

		int subsize = subList.size;
		to -= subsize;
		while (from <= to) {
			int i = subsize;
			int j = from + subsize;
			while (--i >= 0 && elems[--j] == subElems[i]) { // compare from
															// right to left
				;
			}
			if (i < 0)
				return from; // found
			from++;
		}
		return -1; // not found
	}

	/**
	 * Returns the index of the first occurrence of the specified element within
	 * the range <code>[from..to)</code>. Returns <code>-1</code> if the
	 * receiver does not contain such an element.
	 * 
	 * @param from
	 *            the leftmost search index, inclusive.
	 * @param to
	 *            the rightmost search index, exclusive.
	 * @param elem
	 *            element to search for.
	 * @return the index of the first occurrence of the element in the receiver;
	 *         returns <code>-1</code> if the element is not found.
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range
	 */
	public int indexOf(int from, int to, byte elem) {
		checkRange(from, to);
		byte[] elems = elements;
		for (int i = from; i < to; i++) {
			if (elem == elems[i])
				return i; // found
		}
		return -1; // not found
	}

	/**
	 * Returns the index of the last occurrence of the given sublist within the
	 * range <code>this[from..to)</code>. Returns <code>-1</code> if the
	 * receiver does not contain such a sublist. Examples:
	 * 
	 * <pre>
	 * [a,b,c,d,e,b,c,d].lastIndexOf(0,8, [b,c,d]) --> 5
	 * [a,b,c,d,e].lastIndexOf(0,5, [b,c,d]) --> 1
	 * [a,b,c,d,e].lastIndexOf(1,4, [b,c,d]) --> 1
	 * [a,b,c,d,e].lastIndexOf(0,5, [x,y])   --> -1
	 * [a,b,c,d,e].lastIndexOf(0,3, [b,c,d]) --> -1
	 * [a].lastIndexOf(0,1, [a,b,c]) --> -1
	 * [a,b,c,d,e].lastIndexOf(2,3, []) --> 3 // empty sublist is always found
	 * [].lastIndexOf(0,0, []) --> 0
	 * </pre>
	 * 
	 * @param from
	 *            the leftmost search index within the receiver, inclusive.
	 * @param to
	 *            the rightmost search index within the receiver, exclusive.
	 * @param subList
	 *            the sublist to search for.
	 * @return the index of the last occurrence of the sublist in the receiver;
	 *         returns <code>-1</code> if the sublist is not found.
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range
	 */
	public int lastIndexOf(int from, int to, ArrayByteList subList) {
		// brute-force algorithm, but very efficiently implemented
		checkRange(from, to);
		byte[] elems = elements;
		byte[] subElems = subList.elements;

		int subsize = subList.size;
		from += subsize;
		while (from <= to) {
			int i = subsize;
			int j = to;
			while (--i >= 0 && elems[--j] == subElems[i]) { // compare from
															// right to left
				;
			}
			if (i < 0)
				return to - subsize; // found
			to--;
		}
		return -1; // not found
	}

	/**
	 * Removes the elements in the range <code>[from..to)</code>. Shifts any
	 * subsequent elements to the left. Keeps the current capacity. Note: To
	 * remove a single element use <code>remove(index, index+1)</code>. To
	 * remove all elements use <code>remove(0, list.size())</code>.
	 * 
	 * @param from
	 *            the index of the first element to removed (inclusive).
	 * @param to
	 *            the index of the last element to removed (exclusive).
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range
	 */
	public void remove(int from, int to) {
		shrinkOrExpand(from, to, 0);
		// equally correct alternative impl: replace(from, to, 0, 0);
	}

	/**
	 * Removes from the receiver all elements that are contained in the
	 * specified other list.
	 * <p>
	 * Example: <code>[0,1,2,2,3,3,0].removeAll([2,1]) --> [0,3,3,0]</code>
	 * 
	 * @param other
	 *            the other list to test against (remains unmodified by this
	 *            method).
	 * @return <code>true</code> if the receiver changed as a result of the
	 *         call.
	 */
	public boolean removeAll(ArrayByteList other) {
		// efficient implementation: O(N)
		if (size == 0 || other.size() == 0)
			return false; // nothing to do

		BitSet bitSet = new BitSet(256);
		for (int i = 0; i < other.size; i++) {
			bitSet.set(128 + other.elements[i]);
		}

		int j = 0;
		for (int i = 0; i < size; i++) {
			if (!bitSet.get(128 + elements[i])) {
				elements[j++] = elements[i];
			}
		}

		boolean modified = (j != size);
		size = j;
		return modified;
	}

	/**
	 * The powerful work horse for all add/insert/replace/remove methods. One
	 * powerful efficient method does it all :-)
	 */
	private void shrinkOrExpand(int from, int to, int replacementSize) {
		checkRange(from, to);
		int diff = replacementSize - (to - from);
		if (diff != 0) {
			ensureCapacity(size + diff);
			if (size - to > 0) { // check is for performance only (arraycopy is
									// native method)
				// diff > 0 shifts right, diff < 0 shifts left
				System.arraycopy(elements, to, elements, to + diff, size - to);
			}
			size += diff;
		}
	}

	/**
	 * Replaces all elements in the range <code>[from..to)</code> with the
	 * elements <code>replacement[offset..offset+length)</code>. The replacement
	 * can have any length. Increases (or decreases) the receiver's size by
	 * <code>length - (to - from)</code>. Use <code>from==to</code> to perform
	 * pure insertion.
	 * 
	 * @param from
	 *            the index of the first element to replace (inclusive)
	 * @param to
	 *            the index of the last element to replace (exclusive).
	 * @param replacement
	 *            the elements to replace the replaced elements
	 * @param offset
	 *            the offset of the first replacing element (inclusive)
	 * @param length
	 *            the number of replacing elements
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 */
	public void replace(int from, int to, byte[] replacement, int offset,
			int length) {
		if (offset < 0 || length < 0 || offset + length > replacement.length)
			throw new IndexOutOfBoundsException("offset: " + offset
					+ ", length: " + length + ", replacement.length: "
					+ replacement.length);
		shrinkOrExpand(from, to, length);
		System.arraycopy(replacement, offset, this.elements, from, length);
	}

	/**
	 * Replaces all elements in the range <code>[from..to)</code> with the given
	 * replacement. The replacement can have any length. Increases (or
	 * decreases) the receiver's size by
	 * <code>replacement.size - (to - from)</code>. Use <code>from==to</code> to
	 * perform pure insertion. Examples:
	 * 
	 * <pre>
	 * [a,b,c,d,e].replace(1,4, [x,y]) --> [a,x,y,e]
	 * [a,b].replace(1,1, [w,x,y,z]) --> [a,w,x,y,z,b]
	 * </pre>
	 * 
	 * @param from
	 *            the index of the first element to replace (inclusive)
	 * @param to
	 *            the index of the last element to replace (exclusive).
	 * @param replacement
	 *            the elements to replace the replaced elements
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 */
	public void replace(int from, int to, ArrayByteList replacement) {
		shrinkOrExpand(from, to, replacement.size);
		System.arraycopy(replacement.elements, 0, this.elements, from,
				replacement.size);
	}

	/**
	 * Replaces all elements in the range <code>[from..to)</code> with the given
	 * replacement. The replacement consists of
	 * <code>replacement[replacement.position() .. replacement.position() + replacementSize)</code>
	 * . Increases (or decreases) the receiver's size by
	 * <code>replacementSize - (to - from)</code>. Use <code>from==to</code> to
	 * perform pure insertion. Examples:
	 * 
	 * <pre>
	 * [a,b,c,d,e].replace(1,4, [x,y], 2) --> [a,x,y,e]
	 * [a,b].replace(1,1, [w,x,y,z], 4) --> [a,w,x,y,z,b]
	 * </pre>
	 * 
	 * There must hold:
	 * <code>0 < replacementSize <= replacement.remaining()</code>.
	 * 
	 * @param from
	 *            the index of the first element to replace (inclusive)
	 * @param to
	 *            the index of the last element to replace (exclusive).
	 * @param replacement
	 *            the elements to replace the replaced elements
	 * @param replacementSize
	 *            the number of replacing elements
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 */
	public void replace(int from, int to, ByteBuffer replacement,
			int replacementSize) {
		if (replacementSize < 0 || replacementSize > replacement.remaining())
			throw new IndexOutOfBoundsException("replacementSize: "
					+ replacementSize);
		shrinkOrExpand(from, to, replacementSize);
		replacement.get(this.elements, from, replacementSize);
	}

	/**
	 * Replaces all elements in the range <code>[from..to)</code> with the given
	 * replacement. The replacement can have any length >= 0. Increases (or
	 * decreases) the receiver's size by
	 * <code>replacementSize - (to - from)</code>. Use <code>from==to</code> to
	 * perform pure insertion. Examples:
	 * 
	 * <pre>
	 * [a,b,c,d,e].replace(1,4,x,4) --> [a,x,x,x,x,e]
	 * [a,b,c,d,e].replace(0,0,x,4) --> [x,x,x,x,a,b,c,d,e]
	 * </pre>
	 * 
	 * @param from
	 *            the index of the first element to replace (inclusive)
	 * @param to
	 *            the index of the last element to replace (exclusive).
	 * @param replacement
	 *            the elements to replace the replaced elements
	 * @param replacementSize
	 *            the number of times <code>replacement</code> is to replace the
	 *            replaced elements
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 */
	public void replace(int from, int to, byte replacement, int replacementSize) {
		checkSize(replacementSize);
		shrinkOrExpand(from, to, replacementSize);
		while (replacementSize-- > 0)
			elements[from++] = replacement;
		// java.util.Arrays.fill(this.elements, from, from + replacementSize,
		// replacement);
	}

	/**
	 * Retains (keeps) only the elements in the receiver that are contained in
	 * the specified other list. In other words, removes from the receiver all
	 * of its elements that are not contained in the specified other list.
	 * <p>
	 * Example: <code>[0,1,2,2,3,1].retainAll([2,1]) --> [1,2,2,1]</code>
	 * <p>
	 * An efficient <i>set intersection</i> can be computed along the following
	 * lines:
	 * 
	 * <pre>
	 * list1.sort(true);
	 * list2.sort(true);
	 * list1.retainAll(list2);
	 * System.out.println(&quot;list1.retainAll(list2) = &quot; + list1);
	 * // as a convenient byproduct we now know if list2 is a SUBSET of list1:
	 * System.out.println(&quot;list1.containsAll(list2) = &quot;
	 * 		+ (list1.size() == list2.size()));
	 * </pre>
	 * 
	 * @param other
	 *            the other list to test against (remains unmodified by this
	 *            method).
	 * @return <code>true</code> if the receiver changed as a result of the
	 *         call.
	 */
	public boolean retainAll(ArrayByteList other) {
		// efficient implementation: O(N)
		if (size == 0)
			return false;
		if (other.size() == 0) {
			size = 0;
			return true;
		}

		BitSet bitSet = new BitSet(256);
		for (int i = 0; i < other.size; i++) {
			bitSet.set(128 + other.elements[i]);
		}

		int j = 0;
		for (int i = 0; i < size; i++) {
			if (bitSet.get(128 + elements[i]))
				elements[j++] = elements[i];
		}

		boolean modified = (j != size);
		size = j;
		return modified;
	}

	/**
	 * Rotates (shifts) the elements in the range <code>[from..to)</code> by the
	 * specified distance. After calling this method, the element at index
	 * <code>i</code> will be the element previously at index
	 * <code>(i - distance)</code> mod <code>to-from</code>, for all values of
	 * <code>i</code> between <code>from</code> (inclusive) and <code>to</code>,
	 * exclusive. (This method has no effect on the size of the list.) Examples:
	 * 
	 * <pre>
	 *   [a, b, c, d, e].rotate(0, 5, 1)  --> [e, a, b, c, d]
	 *   [a, b, c, d, e].rotate(0, 5, 2)  --> [d, e, a, b, c]
	 *   [a, b, c, d, e].rotate(1, 4, -1) --> [a, c, d, b, e]
	 * </pre>
	 * 
	 * <p>
	 * To move elements rightwards, use a positive shift distance. To move
	 * elements leftwards, use a negative shift distance.
	 * <p>
	 * Note that this method can usefully be applied to sublists to move one or
	 * more elements within a list while preserving the order of the remaining
	 * elements.
	 * <p>
	 * This implementation exchanges the first element into the location it
	 * should go, and then repeatedly exchanges the displaced element into the
	 * location it should go until a displaced element is swapped into the first
	 * element. If necessary, the process is repeated on the second and
	 * successive elements, until the rotation is complete. This algorithm is
	 * efficient: Time complexity is linear O(to-from), space complexity is
	 * constant O(1).
	 * 
	 * @param from
	 *            the index of the first element to rotate (inclusive)
	 * @param to
	 *            the index of the last element to rotate (exclusive).
	 * @param distance
	 *            the distance to rotate the list. There are no constraints on
	 *            this value; for example it may be zero, negative, or greater
	 *            than <code>to-from</code>.
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range.
	 * @see java.util.Collections#rotate(java.util.List, int)
	 */
	public void rotate(int from, int to, int distance) {
		checkRange(from, to);
		int length = to - from;
		if (length == 0)
			return;
		distance = distance % length;
		if (distance < 0)
			distance += length;
		if (distance == 0)
			return;

		byte[] elems = elements;
		for (int nMoved = 0; nMoved != length; from++) {
			byte displaced = elems[from];
			int i = from;
			do {
				i += distance;
				if (i >= to)
					i -= length;

				byte tmp = elems[i];
				elems[i] = displaced;
				displaced = tmp;

				nMoved++;
			} while (i != from);
		}
	}

	/**
	 * Replaces the element at the specified index with the specified element.
	 * 
	 * @param index
	 *            index of element to replace.
	 * @param element
	 *            element to be stored at the specified index.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range.
	 */
	public void set(int index, byte element) {
		checkIndex(index);
		elements[index] = element;
		// equally correct alternative impl: replace(index, index+1, element,
		// 1);
	}

	/**
	 * Returns the number of contained elements.
	 * 
	 * @return the number of elements contained in the receiver.
	 */
	public int size() {
		return size;
	}

	/**
	 * Sorts the elements into ascending numerical order. For mathematical set
	 * operations, optionally removes duplicate elements before returning.
	 * Examples:
	 * 
	 * <pre>
	 * [3,2,2,1].sort(false) --> [1,2,2,3]
	 * [3,2,2,1].sort(true)  --> [1,2,3]
	 * </pre>
	 * 
	 * @param removeDuplicates
	 *            remove duplicate elements or keep them?
	 */
	public void sort(boolean removeDuplicates) {
		if (size <= 60) { // heuristic threshold according to benchmarks
							// (theory: N*logN = 2*256)
			java.util.Arrays.sort(elements, 0, size);
			if (removeDuplicates)
				removeDuplicates();
		} else {
			countSort(removeDuplicates);
		}
	}

	/** efficient sort implementation: O(N) via frequency counts */
	private void countSort(boolean removeDuplicates) {
		final int min = Byte.MIN_VALUE;
		final int max = Byte.MAX_VALUE;
		int[] counts = new int[max - min + 1]; // could use BitSet if
												// removeDuplicates
		for (int i = size; --i >= 0;) {
			counts[elements[i] - min]++;
		}

		int j = 0;
		for (int i = min; i <= max; i++) {
			int k = counts[i - min];
			if (removeDuplicates && k > 1)
				k = 1;
			while (--k >= 0) {
				elements[j++] = (byte) i;
			}
		}
		size = j;
	}

	/**
	 * [1,2,2,3,3,3] --> [1,2,3] Assertion: list must be sorted prior to calling
	 * this method
	 */
	private void removeDuplicates() {
		int i = 0;
		int j = 0;
		while (j < size) {
			byte elem = elements[j++];
			elements[i++] = elem;
			while (j < size && elements[j] == elem)
				j++; // skip duplicates
		}
		size = i;
	}

	/** Small helper method eliminating redundancy. */
	private byte[] subArray(int from, int length, int capacity) {
		byte[] subArray = new byte[capacity];
		System.arraycopy(elements, from, subArray, 0, length);
		return subArray;
	}

	/**
	 * Constructs and returns a new list containing a copy of the elements in
	 * the range <code>[from..to)</code>.
	 * 
	 * @param from
	 *            the index of the first element (inclusive).
	 * @param to
	 *            the index of the last element (exclusive).
	 * @return a new list
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range
	 */
	public ArrayByteList subList(int from, int to) {
		checkRange(from, to);
		return new ArrayByteList(subArray(from, to - from, to - from));
	}

	/**
	 * Returns a copied array of bytes containing all elements; the returned
	 * array has length = this.size().
	 */
	public byte[] toArray() {
		return subArray(0, size, size);
	}

	/**
	 * Returns a string representation, containing the numeric String
	 * representation of each element.
	 */
	public String toString() {
		// return toList().toString();
		StringBuffer buf = new StringBuffer(4 * size);
		buf.append("[");
		for (int i = 0; i < size; i++) {
			buf.append(elements[i]);
			if (i < size - 1)
				buf.append(", ");
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * Returns a decoded string representation of all elements.
	 * 
	 * @param charset
	 *            the charset to convert with (e.g.
	 *            <code>Charset.forName("US-ASCII")</code>,
	 *            <code>Charset.forName("ISO-8859-1")</code>). If
	 *            <code>null</code> uses <code>Charset.forName("UTF-8")</code>
	 *            as the default charset.
	 */
	public String toString(Charset charset) {
		return toString(0, size, charset);
	}

	/**
	 * Returns a decoded string representation of the bytes in the given range
	 * <code>[from..to)</code>.
	 * 
	 * @param from
	 *            the index of the first element (inclusive).
	 * @param to
	 *            the index of the last element (exclusive).
	 * @param charset
	 *            the charset to convert with (e.g.
	 *            <code>Charset.forName("US-ASCII")</code>,
	 *            <code>Charset.forName("ISO-8859-1")</code>). If
	 *            <code>null</code> uses <code>Charset.forName("UTF-8")</code>
	 *            as the default charset.
	 * @throws IndexOutOfBoundsException
	 *             if indexes are out of range
	 */
	public String toString(int from, int to, Charset charset) {
		checkRange(from, to);
		return getCharset(charset).decode(
				ByteBuffer.wrap(this.elements, from, to - from)).toString();
	}

	/**
	 * Trims the capacity of the receiver to be the receiver's current size;
	 * Releases any superfluos internal memory. An application can use this
	 * operation to minimize the storage of the receiver.
	 */
	public void trimToSize() {
		if (elements.length > size) {
			elements = subArray(0, size, size);
		}
	}

	/**
	 * Checks if the given index is in range.
	 */
	private void checkIndex(int index) {
		if (index >= size || index < 0)
			throw new IndexOutOfBoundsException("index: " + index + ", size: "
					+ size);
	}

	/**
	 * Checks if the given range is within the contained array's bounds.
	 */
	private void checkRange(int from, int to) {
		if (from < 0 || from > to || to > size)
			throw new IndexOutOfBoundsException("from: " + from + ", to: " + to
					+ ", size: " + size);
	}

	/**
	 * Checks if the given size is within bounds.
	 */
	private void checkSize(int newSize) {
		if (newSize < 0)
			throw new IndexOutOfBoundsException("newSize: " + newSize);
	}

	/**
	 * Returns the default charset if no charset is specified.
	 */
	private static Charset getCharset(Charset charset) {
		return charset == null ? DEFAULT_CHARSET : charset;
	}

	/**
	 * efficient Serializable support. Example: ObjectOutputStream out = new
	 * ObjectOutputStream(new FileOutputStream("/tmp/test"));
	 * out.writeObject(new ArrayByteList("hello world", null)); out.close();
	 * 
	 * ObjectInputStream in = new ObjectInputStream(new
	 * FileInputStream("/tmp/test")); ArrayByteList list = (ArrayByteList)
	 * in.readObject(); in.close(); System.out.println(list.toString(null));
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(elements.length);
		out.write(elements, 0, size);
	}

	/** efficient Serializable support */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		elements = new byte[in.readInt()];
		in.readFully(elements, 0, size);
	}

	// ****************************************************************************
	// following are some methods not deemed important enough or complex enough
	// to warrant interface bloat:
	// ****************************************************************************

	// /**
	// * Appends the specified elements to the end of this list.
	// * <p>
	// * If you need to append <code>elems[from..to)</code>, use
	// * <code>list.add(new ArrayByteList(elems).subList(from, to))</code>
	// * or <code>list.add(ByteBuffer.wrap(elems, from, to-from))</code>
	// * or similar.
	// *
	// * @param elems
	// * elements to be appended.
	// * @return <code>this</code> (for chaining convenience only)
	// */
	// public ArrayByteList add(byte[] elems) {
	// replace(size, size, elems);
	// return this;
	// }

	// /**
	// * Lexicographically compares this object with the specified other object
	// for order. Returns a
	// * negative integer, zero, or a positive integer as this object is less
	// * than, equal to, or greater than the specified other object.
	// * <p>
	// * If a negative integer <code>i</code> is returned, the index of the
	// * first differing element is <code>-(i+1)</code>. If a positive integer
	// * <code>i</code> is returned, the index of the first differing element is
	// * <code>i-1</code>.
	// *
	// * @param otherObj
	// * the Object to be compared.
	// * @return a negative integer, zero, or a positive integer as this object
	// is
	// * less than, equal to, or greater than the specified other object.
	// *
	// * @throws ClassCastException
	// * if the specified object's type prevents it from being
	// * compared to this Object.
	// * @see Comparable
	// * @see String#compareTo(String)
	// */
	// public int compareTo(Object otherObj) {
	// if (this == otherObj) return 0;
	// if (otherObj == null) throw new NullPointerException();
	// if (!(otherObj instanceof ArrayByteList)) throw new ClassCastException();
	// ArrayByteList other = (ArrayByteList) otherObj;
	//
	// int minSize = Math.min(size, other.size);
	// byte[] elems = elements;
	// byte[] otherElems = other.elements;
	// int i = 0;
	// for (; i < minSize; i++) {
	// if (elems[i] < otherElems[i]) return (-i) - 1; // this < other
	// else if (elems[i] > otherElems[i]) return i + 1; // this > other
	// }
	// if (other.size > minSize) return (-i) - 1; // this < other
	// if (size > minSize) return i + 1; // this > other
	// return 0; // equal
	// }

	// /**
	// * Inserts the specified element before the specified index. Shifts the
	// * element currently at that index (if any) and any subsequent elements
	// * to the right. This is equivalent to <code>replace(index, index, elem,
	// 1)</code>.
	// *
	// * @param index
	// * index before which the specified element is to be inserted
	// * @param elem
	// * element to insert.
	// * @throws IndexOutOfBoundsException if index is out of range.
	// */
	// private void insert(int index, byte elem) {
	// replace(index, index, elem, 1);
	// }
	//
	// /**
	// * Inserts the specified elements before the specified index. Shifts the
	// * element currently at that index (if any) and any subsequent elements
	// * to the right.
	// *
	// * @param index
	// * index before which the specified elements are to be inserted
	// * @param elems
	// * elements to insert.
	// * @throws IndexOutOfBoundsException if index is out of range.
	// */
	// private void insert(int index, byte[] elems) {
	// replace(index, index, elems);
	// }
	//
	// /**
	// * Inserts the specified elements before the specified index. Shifts the
	// * element currently at that index (if any) and any subsequent elements
	// * to the right.
	// *
	// * @param index
	// * index before which the specified elements are to be inserted
	// * @param elems
	// * elements to insert.
	// * @throws IndexOutOfBoundsException if index is out of range.
	// */
	// private void insert(int index, ArrayByteList elems) {
	// replace(index, index, elems);
	// }
	//
	// /**
	// * Inserts the remaining buffer elements before the specified index.
	// * Shifts the element currently at that index (if any) and any subsequent
	// * elements to the right.
	// *
	// * @param index
	// * index before which the specified elements are to be inserted
	// * @param elems
	// * elements to insert.
	// * @throws IndexOutOfBoundsException if index is out of range.
	// */
	// private void insert(int index, ByteBuffer elems) {
	// replace(index, index, elems);
	// }

	// /**
	// * Returns the index of the last occurrence of the specified element
	// within
	// * the range <code>[from..to)</code>. Returns <code>-1</code> if the
	// * receiver does not contain such an element.
	// *
	// * @param from
	// * the leftmost search index, inclusive.
	// * @param to
	// * the rightmost search index, exclusive.
	// * @param elem
	// * element to search for.
	// * @return the index of the last occurrence of the element in the
	// receiver;
	// * returns <code>-1</code> if the element is not found.
	// * @throws IndexOutOfBoundsException
	// * if indexes are out of range.
	// */
	// public int lastIndexOf(int from, int to, byte elem) {
	// checkRange(from, to);
	// byte[] elems = elements;
	// for (int i = to; --i >= from; ) {
	// if (elem == elems[i]) return i; //found
	// }
	// return -1; //not found
	// }

	// /**
	// * Removes the element at the specified index. Shifts any subsequent
	// * elements to the left. Keeps the current capacity.
	// *
	// * @param index
	// * the index of the element to removed.
	// * @throws IndexOutOfBoundsException
	// * if index is out of range
	// */
	// public void remove(int index) {
	// remove(index, index+1);
	// }

	// /**
	// * Replaces all elements in the range <code>[from..to)</code> with the
	// * elements <code>replacement[offset..offset+length)</code>. The
	// replacement can have any length.
	// * Increases (or decreases) the receiver's size by <code>length - (to -
	// from)</code>.
	// * Use <code>from==to</code> to perform pure insertion.
	// *
	// * @param from the index of the first element to replace (inclusive)
	// * @param to the index of the last element to replace (exclusive).
	// * @param replacement the elements to replace the replaced elements
	// * @param offset the offset of the first replacing element (inclusive)
	// * @param length the number of replacing elements
	// * @throws IndexOutOfBoundsException if indexes are out of range.
	// */
	// public void replace(int from, int to, byte[] replacement, int offset, int
	// length) {
	// if (offset < 0 || length < 0 || offset + length > replacement.length)
	// throw new IndexOutOfBoundsException("offset: " + offset + ", length: " +
	// length + ", replacement.length: " + ment.length);
	// //if (elements == replacement && length - (to - from) != 0) replacement =
	// (byte[]) replacement.clone();
	// shrinkOrExpand(from, to, length);
	// System.arraycopy(replacement, offset, this.elements, from, length);
	// }

	// /**
	// * Reverses the order of elements in the range <code>[from..to)</code>.
	// * Last becomes first, second last becomes second first, and so on.
	// * <p>
	// * Example: <code>[a,b,c,d].reverse(0,4) --> [d,c,b,a]</code>
	// *
	// * @param from the index of the first element (inclusive)
	// * @param to the index of the last element (exclusive).
	// * @throws IndexOutOfBoundsException if indexes are out of range.
	// */
	// public void reverse(int from, int to) {
	// checkRange(from, to);
	// byte[] elems = elements;
	// int middle = from + (to - from) / 2;
	// to--;
	// for ( ; from < middle; from++, to--) { // swap
	// byte tmp = elems[from];
	// elems[from] = elems[to];
	// elems[to] = tmp;
	// }
	// }

	// /**
	// * Sets the size to the given new size, expanding the list capacity if
	// * necessary.
	// * <p>
	// * Capacity expansion introduces a new backing array. If the new
	// * size is greater than the current size the elements between the current
	// * size and the new size will become legally accessible but have undefined
	// * values. An application will typically want to set these elements to
	// defined
	// * values immediately after this method returns. Note that
	// * <code>setSize(0)</code> effectively clears the list (as does
	// * <code>remove(0, list.size()</code>).
	// *
	// * @param newSize the new size.
	// * @return <code>this</code> (for chaining convenience only)
	// * @throws IndexOutOfBoundsException if new size is less than zero.
	// */
	// public ArrayByteList setSize(int newSize) {
	// checkSize(newSize);
	// ensureCapacity(newSize);
	// // equivalent impl: shrinkOrExpand(0, size, newSize);
	// size = newSize;
	// return this;
	// }

	// /**
	// * Returns a <code>java.util.ArrayList</code> containing a copy of all
	// elements.
	// */
	// public java.util.ArrayList toList() {
	// java.util.ArrayList list = new java.util.ArrayList(size);
	// for (int i = 0; i < size; i++)
	// list.add(new Byte(elements[i]));
	// return list;
	// }

	// /**
	// * Creates and returns an unsynchronized input stream that reads from this
	// * SHARED backing byte list. Useful if legacy code requires adapting to a
	// * stream based interface. Note: This is much more efficient and
	// * straighforward than using a {@link java.io.ByteArrayInputStream}.
	// * <p>
	// * Reading from the stream increments an internal counter that keeps track
	// * of the next byte to be supplied by the stream's read method. Reading
	// * starts at list index 0; end-of-stream is reached on list index size().
	// * Reading from the stream leaves the list unmodified (it does not remove
	// * elements).
	// * <p>
	// * Closing the stream has no effect. The stream's methods can be called
	// * after the stream has been closed without generating an IOException. In
	// * fact the stream implementation never ever throws an IOException.
	// *
	// * @return the stream
	// */
	// public InputStream asInputStream() {
	// return new ListInputStream();
	// }
	//
	// /**
	// * private class; implements/overrides methods of base stream class;
	// * well behaved even under weird interleaved list add() and/or remove(),
	// * but not under concurrent access, since unsynchronized.
	// */
	// private class ListInputStream extends InputStream {
	//
	// private int pos = 0;
	// private int mark = 0;
	//
	// public int read() {
	// return pos < size ? (elements[pos++] & 0xff) : -1;
	// }
	//
	// public int read(byte b[], int off, int len) {
	// if (off < 0 || len < 0 || off + len > b.length) throw new
	// IndexOutOfBoundsException();
	// if (pos >= size) return -1;
	// if (pos + len > size) {
	// len = size - pos;
	// }
	// if (len <= 0) return 0;
	// System.arraycopy(elements, pos, b, off, len);
	// pos += len;
	// return len;
	// }
	//
	// public long skip(long n) {
	// if (pos + n > size) {
	// n = size - pos;
	// }
	// if (n < 0) return 0;
	// pos += n;
	// return n;
	// }
	//
	// public int available() {
	// return Math.max(0, size - pos); // safe even with list add() and/or
	// remove()
	// }
	//
	// public boolean markSupported() {
	// return true;
	// }
	//
	// public void mark(int readlimit) {
	// mark = pos;
	// }
	//
	// public void reset() {
	// pos = mark;
	// }
	//
	// }

}
