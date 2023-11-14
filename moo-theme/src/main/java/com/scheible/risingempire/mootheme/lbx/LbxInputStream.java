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

	public LbxInputStream(InputStream in) {
		this.in = in;
	}

	public short readUByte() throws IOException {
		this.readIndex += 1;

		return (short) (this.in.read() & 0xFF);
	}

	public int readUShort() throws IOException {
		this.readIndex += 2;

		int byte0 = this.in.read() & 0xFF;
		int byte1 = this.in.read() & 0xFF;
		return (byte1 << 8) + byte0;
	}

	public long readUInt() throws IOException {
		this.readIndex += 4;

		int byte0 = this.in.read() & 0xFF;
		int byte1 = this.in.read() & 0xFF;
		int byte2 = this.in.read() & 0xFF;
		int byte3 = this.in.read() & 0xFF;
		return (byte3 << 24) + (byte2 << 16) + (byte1 << 8) + byte0;
	}

	public long skip(long n) throws IOException {
		this.readIndex += n;
		return this.in.skip(n);
	}

	@Override
	public void close() throws IOException {
		this.in.close();
	}

	public long getReadIndex() {
		return this.readIndex;
	}

}
