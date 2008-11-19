package org.relique.jdbc.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSetInputStream extends InputStream {

	private List fileNames;
	private FileInputStream currentFile;
	private boolean atEndOfLine;
	private boolean readingHeader;
	private String tail;
	private int pos;
	private Pattern fileNameRE;
	private char separator;
	private String dataTail;

	public FileSetInputStream(String dirName, String fileNamePattern,
			String[] fieldsInName, char separator) throws FileNotFoundException {

		// Initialising tail for header...
		this.separator = separator;
		tail = "";
		for (int i = 0; i < fieldsInName.length; i++) {
			tail += separator;
			tail += fieldsInName[i];
		}

		fileNames = new LinkedList();
		File root = new File(dirName);
		String[] candidates = root.list();

		fileNameRE = Pattern.compile(fileNamePattern);

		for (int i = 0; i < candidates.length; i++) {
			Matcher m = fileNameRE.matcher(candidates[i]);
			if (m.matches()) {
				fileNames.add(dirName + candidates[i]);
			}
		}
		fileNameRE = Pattern.compile(".*"+fileNamePattern);
		readingHeader = true;
		atEndOfLine = false;
		String currentName = (String) fileNames.remove(0);
		dataTail = getTailFromName(currentName);
		currentFile = new FileInputStream(currentName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileInputStream#close()
	 */
	public void close() throws IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileInputStream#read()
	 */
	public int read() throws IOException {
		// TODO Auto-generated method stub
		if (atEndOfLine) {
			return readTail();
		}
		if (currentFile == null)
			return -1;
		int ch = currentFile.read();
		if (ch == '\n') {
			atEndOfLine = true;
			return readTail();
		} else if (ch == -1) {
			currentFile.close();
			// open next file and skip header
			atEndOfLine = false;
			pos = 0;
			String currentName;
			try {
				currentName = (String) fileNames.remove(0);
			} catch (IndexOutOfBoundsException e) {
				currentFile = null;
				return -1;
			}
			tail = getTailFromName(currentName);
			currentFile = new FileInputStream(currentName);
			while (currentFile.read() != '\n');
			return read();
		}
		return ch;
	}

	private String getTailFromName(String currentName) {
		Matcher m = fileNameRE.matcher(currentName);
		m.matches();
		String tail = "";
		for (int i = 1; i <= m.groupCount(); i++) {
			tail += separator;
			tail += m.group(i);
		}
		return tail;
	}

	private int readTail() {
		if (pos < tail.length())
			return tail.charAt(pos++);
		atEndOfLine = false;
		pos = 0;
		if (readingHeader) {
			readingHeader = false;
			tail = dataTail;
		}
		return '\n';
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		// TODO Auto-generated method stub
		super.reset();
	}

}
