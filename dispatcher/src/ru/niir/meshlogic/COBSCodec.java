/*
 * Copyright (c) 2003, The Regents of the University of California, through
 * Lawrence Berkeley National Laboratory (subject to receipt of any required
 * approvals from the U.S. Dept. of Energy). All rights reserved.
 */
package ru.niir.meshlogic;

import java.io.IOException;

/**
 * Encoder/Decoder implementing Consistent Overhead Byte Stuffing (COBS) for
 * efficient, reliable, unambigous packet framing regardless of packet content,
 * making it is easy for applications to recover from malformed packet payloads.
 * <p>
 * For details, see the <a
 * href="http://www.stuartcheshire.org/papers/COBSforToN.pdf">paper </a>. In
 * case the link is broken, get it from the <a
 * href="http://www.stuartcheshire.org">paper's author </a>.
 * <p>
 * Quoting from the paper: "When packet data is sent over any serial medium, a
 * protocol is needed by which to demarcate packet boundaries. This is done by
 * using a special bit-sequence or character value to indicate where the
 * boundaries between packets fall. Data stuffing is the process that transforms
 * the packet data before transmission to eliminate any accidental occurrences
 * of that special framing marker, so that when the receiver detects the marker,
 * it knows, without any ambiguity, that it does indeed indicate a boundary
 * between packets.
 * <p>
 * COBS takes an input consisting of bytes in the range [0,255] and produces an
 * output consisting of bytes only in the range [1,255]. Having eliminated all
 * zero bytes from the data, a zero byte can now be used unambiguously to mark
 * boundaries between packets.
 * <p>
 * This allows the receiver to synchronize reliably with the beginning of the
 * next packet, even after an error. It also allows new listeners to join a
 * broadcast stream at any time and without failing to receive and decode the
 * very next error free packet.
 * <p>
 * With COBS all packets up to 254 bytes in length are encoded with an overhead
 * of exactly one byte. For packets over 254 bytes in length the overhead is at
 * most one byte for every 254 bytes of packet data. The maximum overhead is
 * therefore roughly 0.4% of the packet size, rounded up to a whole number of
 * bytes. COBS encoding has low overhead (on average 0.23% of the packet size,
 * rounded up to a whole number of bytes) and furthermore, for packets of any
 * given length, the amount of overhead is virtually constant, regardless of the
 * packet contents."
 * <p>
 * This class implements the original COBS algorithm, not the COBS/ZPE variant.
 * <p>
 * There holds: <code>decode(encode(src)) = src</code>.
 * <p>
 * Performance Note: The JDK 1.5 server VM runs <code>decode(encode(src))</code>
 * at about 125 MB/s throughput on a commodity PC (2 GHz Pentium 4). Encoding is
 * the bottleneck, decoding is extremely cheap. Obviously, this is way more
 * efficient than Base64 encoding or similar application level byte stuffing
 * mechanisms.
 * 
 * @author whoschek@lbl.gov
 * @author $Author: hoschek3 $
 * @version $Revision: 1.3 $, $Date: 2004/08/08 05:10:17 $
 */
public class COBSCodec {

	protected COBSCodec() {
	} // not instantiable

	/**
	 * Returns the encoded representation of the given bytes. Inefficient
	 * method, but easy to use and understand.
	 * 
	 * @param src
	 *            the bytes to encode
	 * @return the encoded bytes.
	 */
	public static byte[] encode(byte[] src) {
		ArrayByteList dest = new ArrayByteList(maxEncodedSize(src.length));
		encode(src, 0, src.length, dest);
		dest.trimToSize();
		return dest.asArray();
	}

	/**
	 * Adds (appends) the encoded representation of the range
	 * <code>src[from..to)</code> to the given destination list.
	 * 
	 * @param src
	 *            the bytes to encode
	 * @param from
	 *            the first byte to encode (inclusive)
	 * @param to
	 *            the last byte to encode (exclusive)
	 * @param dest
	 *            the destination list to append to
	 */
	public static void encode(byte[] src, int from, int to, ArrayByteList dest) {
		checkRange(from, to, src);
		dest.ensureCapacity(dest.size() + maxEncodedSize(to - from)); // for
																		// performance
																		// ensure
																		// add()
																		// will
																		// never
																		// need
																		// to
																		// expand
																		// list
		int code = 1; // can't use unsigned byte arithmetic...
		int blockStart = -1;

		// find zero bytes, then use bulk copy for best Java performance (unlike
		// in C):
		while (from < to) {
			if (src[from] == 0) {
				finishBlock(code, src, blockStart, dest, from - blockStart);
				code = 1;
				blockStart = -1;
			} else {
				if (blockStart < 0)
					blockStart = from;
				code++;
				if (code == 0xFF) {
					finishBlock(code, src, blockStart, dest, from - blockStart
							+ 1);
					code = 1;
					blockStart = -1;
				}
			}
			from++;
		}

		finishBlock(code, src, blockStart, dest, from - blockStart);
	}

	private static void finishBlock(int code, byte[] src, int blockStart,
			ArrayByteList dest, int length) {
		dest.add((byte) code);
		if (blockStart >= 0)
			dest.add(src, blockStart, length);
	}

	/**
	 * Returns the maximum amount of bytes an ecoding of <code>size</code> bytes
	 * takes in the worst case.
	 */
	public static int maxEncodedSize(int size) {
		return size + 1 + size / 254;
	}

	/**
	 * Returns the decoded representation of the given bytes. Inefficient
	 * method, but easy to use and understand.
	 * 
	 * @param src
	 *            the bytes to decode
	 * @return the decoded bytes.
	 */
	public static byte[] decode(byte[] src) throws IOException {
		ArrayByteList dest = new ArrayByteList(src.length);
		decode(src, 0, src.length, dest);
		dest.trimToSize();
		return dest.asArray();
	}

	/**
	 * Adds (appends) the decoded representation of the range
	 * <code>src[from..to)</code> to the given destination list.
	 * 
	 * @param src
	 *            the bytes to decode
	 * @param from
	 *            the first byte to decode (inclusive)
	 * @param to
	 *            the last byte to decode (exclusive)
	 * @param dest
	 *            the destination list to append to
	 * @throws IOException
	 *             if src data is corrupt (encoded erroneously)
	 */
	public static void decode(byte[] src, int from, int to, ArrayByteList dest)
			throws IOException {
		checkRange(from, to, src);
		dest.ensureCapacity(dest.size() + (to - from)); // for performance
														// ensure add() will
														// never need to expand
														// list

		while (from < to) {
			int code = src[from++] & 0xFF;
			int len = code - 1;
			if (code == 0 || from + len > to)
				throw new IOException(
						"Corrupt COBS encoded data - bug in remote encoder?");
			dest.add(src, from, len);
			from += len;
			if (code < 0xFF && from < to) { // unnecessary to write last zero
											// (is implicit anyway)
				dest.add((byte) 0);
			}
		}
	}

	/**
	 * Checks if the given range is within the contained array's bounds.
	 */
	private static void checkRange(int from, int to, byte[] arr) {
		if (from < 0 || from > to || to > arr.length)
			throw new IndexOutOfBoundsException("from: " + from + ", to: " + to
					+ ", size: " + arr.length);
	}

}
