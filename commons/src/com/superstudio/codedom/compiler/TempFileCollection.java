package com.superstudio.codedom.compiler;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.FileAccess;
import com.superstudio.commons.io.FileMode;
import com.superstudio.commons.io.FileStream;
import com.superstudio.commons.io.Path;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class TempFileCollection implements List, java.io.Closeable, Serializable {
	private String basePath;

	private String tempDir;

	private boolean keepFiles;

	private Hashtable<String, Boolean> files;

	public final int size() {
		return this.files.size();
	}

	/*
	 * private int size() { return this.files.size(); }
	 */

	private Object getSyncRoot() {
		return null;
	}

	private boolean getIsSynchronized() {
		return false;
	}

	public final String getTempDir() {
		if (this.tempDir != null) {
			return this.tempDir;
		}
		return "";
	}

	public final String getBasePath() throws Exception {
		this.ensureTempNameCreated();
		return this.basePath;
	}

	public final boolean getKeepFiles() {
		return this.keepFiles;
	}

	public final void setKeepFiles(boolean value) {
		this.keepFiles = value;
	}

	public TempFileCollection() {
		this(null, false);
	}

	public TempFileCollection(String tempDir) {
		this(tempDir, false);
	}

	public TempFileCollection(String tempDir, boolean keepFiles) {
		this.keepFiles = keepFiles;
		this.tempDir = tempDir;
		this.files = new Hashtable<String, Boolean>();
	}

	public final void close() throws java.io.IOException {
		this.dispose(true);
		System.gc();
		// GC.SuppressFinalize(this);
	}

	protected void dispose(boolean disposing) {
		this.delete();
	}

	protected void finalize() throws Throwable {
		this.dispose(false);
	}

	public final String addExtension(String fileExtension) throws Exception {
		return this.addExtension(fileExtension, this.keepFiles);
	}

	public final String addExtension(String fileExtension, boolean keepFile) throws Exception {
		if (fileExtension == null || fileExtension.length() == 0) {
			// throw new
			// IllegalArgumentException(SR.GetString("InvalidNullEmptyArgument",
			// new Object[] {"fileExtension"}), "fileExtension");
		}
		String text = this.getBasePath() + "." + fileExtension;
		this.addFile(text, keepFile);
		return text;
	}

	public final void addFile(String fileName, boolean keepFile) {
		if (fileName == null || fileName.length() == 0) {
			// throw new
			// IllegalArgumentException(SR.GetString("InvalidNullEmptyArgument",
			// new Object[] {"fileName"}), "fileName");
		}
		if (this.files.get(fileName) != null) {
			// throw new
			// IllegalArgumentException(SR.GetString("DuplicateFileName", new
			// Object[] {fileName}), "fileName");
		}
		this.files.put(fileName, keepFile);
	}

	public final Iterator iterator() {
		return this.files.keySet().iterator();
	}

	/*
	 * public final Iterator iterator() { return this.files.keySet().iterator();
	 * }
	 */

	/*
	 * public final void copyTo(Arrays array, int start) {
	 * this.files.keySet().toArray(array, start); }
	 */

	public final void copyTo(String[] fileNames, int start) {
		for (String str : this.files.keySet()) {
			fileNames[start] = str;
			start++;
		}
		// fileNames=
		// Arrays.copyOf(this.files.keySet().toArray(),start,size()-start);
		// this.files.keySet().toArray(fileNames,start);//(fileNames, start);
	}

	private void ensureTempNameCreated() throws Exception {
		if (this.basePath == null) {
			String text = null;
			boolean flag = false;
			int num = 5000;
			do {
				try {
					this.basePath = TempFileCollection.getTempFileName(this.getTempDir());
					String fullPath = (new java.io.File(this.basePath)).getAbsolutePath();
					// (new FileIOPermission(FileIOPermissionAccess.AllAccess,
					// fullPath)).Demand();
					text = this.basePath + ".tmp";
					try (FileStream tempVar = new FileStream(text, FileMode.CreateNew, FileAccess.Write)) {
					}
					flag = true;
				} catch (IOException e) {
					num--;
				

					long num2 = 2147942480L;
					
					// ORIGINAL LINE: if (num == 0 ||
					// (long)Marshal.GetHRForException(e) !=
					// (long)((ulong)num2))
					/*
					 * if (num == 0 || (long)Marshal.GetHRForException(e) !=
					 * (long)((long)num2)) { throw e; }
					 */
					flag = false;
				}
			} while (!flag);
			this.files.put(text, this.keepFiles);
		}
	}

	private boolean keepFile(String fileName) {
		Object obj = this.files.get(fileName);
		return obj != null && (Boolean) obj;
	}

	public final void delete() {
		if (this.files != null && this.files.size() > 0) {
			String[] array = new String[this.files.size()];
			// CollectionHelper.c
			// this.files.keySet().copyTo(array, 0);
			int len = this.files.size();
			int index = 0;
			for (String str : this.files.keySet()) {
				array[index] = new String(str);
				index++;
			}

			String[] array2 = array;
			for (int i = 0; i < array2.length; i++) {
				String text = array2[i];
				if (!this.keepFile(text)) {
					this.delete(text);
					this.files.remove(text);
				}
			}
		}

	}

	public final void safeDelete() {
		// WindowsImpersonationContext impersonation =
		// Executor.RevertImpersonation();
		try {
			this.delete();
		} finally {
			// Executor.ReImpersonate(impersonation);
		}
	}

	private void delete(String fileName) {
		try {
			(new java.io.File(fileName)).delete();
		} catch (java.lang.Exception e) {
		}
	}

	private static String getTempFileName(String tempDir) {
		if (StringHelper.isNullOrEmpty(tempDir)) {
			tempDir = Path.GetTempPath();
		}
		String fileNameWithoutExtension = Path.GetFileNameWithoutExtension(Path.GetRandomFileName());
		String result;
		if (tempDir.endsWith("\\")) {
			result = tempDir + fileNameWithoutExtension;
		} else {
			result = tempDir + "\\" + fileNameWithoutExtension;
		}
		return result;
	}

	@Override
	public boolean add(Object e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void add(int index, Object element) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object set(int index, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}
}