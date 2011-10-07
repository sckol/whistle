package ru.niir.protowhistle.io;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class StorageManager {
	private static StorageManager instance;
	public static final char FIRST_CATEGORY = 'a', DEFAULT_CATEGORY = FIRST_CATEGORY;
	public static final int CATEGORY_NUM = 3;

	private final RecordStore rsUrl, rsCat;

	public static StorageManager getStorageManager() throws RecordStoreException {
		if (instance == null)
			return new StorageManager();
		else
			return instance;
	}

	private StorageManager() throws RecordStoreException {
		rsUrl = RecordStore.openRecordStore("RescueService_URL", true);
		rsCat = RecordStore.openRecordStore("RescueService_Category", true);
	}

	public void saveURL(final String url) throws RecordStoreException {
		final byte[] bytes = (url != null) ? url.getBytes() : null;
		saveSingle(rsUrl, bytes);		
	}
	
	public void saveCategory(final char cat) throws RecordStoreException {
		saveSingle(rsCat, new byte[] {(byte) cat});
	}

	public String loadURL() {
		byte[] bytes = loadSingle(rsUrl);
		if (bytes == null) return null;
		return new String(bytes);
	}
	
	public char loadCategory() {
		byte[] bytes = loadSingle(rsCat);
		if (bytes == null || bytes.length == 0) return DEFAULT_CATEGORY;
		return (char) bytes[0];
	}
	
	private void saveSingle(final RecordStore rs, byte[] bytes) throws RecordStoreException {
		if (rs.getNumRecords() == 0) {
			rs.addRecord(bytes, 0, bytes.length);
		} else {
			rs.setRecord(1, bytes, 0, bytes.length);
		}
	}
	
	private byte[] loadSingle(final RecordStore rs) {
		try {
			if (rs.getNumRecords() == 0)
				return null;
			final byte[] bytes = rs.getRecord(1);
			return bytes;
		} catch (RecordStoreException e) {
			return null;
		}
	}
	
	public static final int categoryToIndex(final char category) {
		return category - 'a';
	}
	
	public static final char indexToCategory(final int index) {
		return (char) (index + 'a');
	}
}
