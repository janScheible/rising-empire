package com.scheible.risingempire.mootheme.lbx;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream to deal with LBX files. LBX files are stored in little endian byte order.
 * The read methods use the next bigger data type to return unsigned values. In addtion to
 * a regular input stream also a read index is maintained.
 *
 * @author sj
 */
public class LbxInputStream implements Closeable {

	private final InputStream in;

	private long readIndex = 0;

	public LbxInputStream(final InputStream in) {
		this.in = in;
	}

	public short readUByte() throws IOException {
		readIndex += 1;

		return (short) (in.read() & 0xFF);
	}

	public int readUShort() throws IOException {
		readIndex += 2;

		final int byte0 = in.read() & 0xFF;
		final int byte1 = in.read() & 0xFF;
		return (byte1 << 8) + byte0;
	}

	public long readUInt() throws IOException {
		readIndex += 4;

		final int byte0 = in.read() & 0xFF;
		final int byte1 = in.read() & 0xFF;
		final int byte2 = in.read() & 0xFF;
		final int byte3 = in.read() & 0xFF;
		return (byte3 << 24) + (byte2 << 16) + (byte1 << 8) + byte0;
	}

	public long skip(final long n) throws IOException {
		readIndex += n;
		return in.skip(n);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public long getReadIndex() {
		return readIndex;
	}

}
