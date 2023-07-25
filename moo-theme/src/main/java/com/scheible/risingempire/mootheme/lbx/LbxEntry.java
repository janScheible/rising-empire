package com.scheible.risingempire.mootheme.lbx;

/**
 *
 * @author sj
 */
public class LbxEntry {

	public enum Type {

		GFX(0), SOUND(1), FONTS_OR_PALLETS(2), HELP_DATA_TABLE(3), DATA_TABLE(5);

		private final int id;

		Type(final int id) {
			this.id = id;
		}

		public static Type valueOf(final int id) {
			for (final Type type : Type.values()) {
				if (type.id == id) {
					return type;
				}
			}

			throw new IllegalArgumentException("The id '" + id + "' is not a known type!");
		}

		public int getId() {
			return id;
		}
	}

	private final LbxInputStream input;
	private final long entryStart;
	private final long entryEnd;
	private final Type type;

	public LbxEntry(final LbxInputStream input, final long entryStart, final long entryEnd, final Type type) {
		this.input = input;
		this.entryStart = entryStart;
		this.entryEnd = entryEnd;
		this.type = type;
	}

	public LbxInputStream getInput() {
		return input;
	}

	public long getEntryStart() {
		return entryStart;
	}

	public long getEntryEnd() {
		return entryEnd;
	}

	public Type getType() {
		return type;
	}
}
