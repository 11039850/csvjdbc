/*
 *  CsvJdbc - a JDBC driver for CSV files
 *  Copyright (C) 2001  Jonathan Ackerman
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.relique.jdbc.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.relique.io.CryptoFilter;

/**
 * This class is a helper class that handles the reading and parsing of data
 * from a .csv file.
 * 
 * @author Jonathan Ackerman
 * @author Sander Brienen
 * @author Stuart Mottram (fritto)
 * @author Jason Bedell
 * @author Tomasz Skutnik
 * @author Christoph Langer
 * @author Chetan Gupta
 * @created 25 November 2001
 * @version $Id: CsvReader.java,v 1.27 2010/05/27 12:51:07 mfrasca Exp $
 */

public class CsvReader {

	protected BufferedReader input;
	protected String[] columnNames;
	protected String[] fieldValues;
	protected java.lang.String buf = null;
	protected char separator = ',';
	protected String headerLine = "";
	protected boolean suppressHeaders = false;
	protected String tableName;
	protected char quoteChar = '"';
	protected String extension = CsvDriver.DEFAULT_EXTENSION;
	protected boolean trimHeaders = true;
	protected char commentChar = 0;
	private boolean ignoreUnparseableLines;
	protected CryptoFilter filter;

	/**
	 *Constructor for the CsvReader object
	 * 
	 * @param fileName
	 *            Description of Parameter
	 * @exception Exception
	 *                Description of Exception
	 * @since
	 */
	public CsvReader(String fileName) throws Exception {
		this(new BufferedReader(new InputStreamReader(new FileInputStream(
				fileName))), ',', false, '"', (char) 0, "",
				CsvDriver.DEFAULT_EXTENSION, true, 0, false, null, false, 0);
	}

	/**
	 * Insert the method's description here.
	 * 
	 * Creation date: (6-11-2001 15:02:42)
	 * 
	 * @param fileName
	 *            java.lang.String
	 * @param separator
	 *            char
	 * @param suppressHeaders
	 *            boolean
	 * @param quoteChar
	 *            char
	 * @param filter the decrypting filter
	 * @param defectiveHeaders 
	 * @param skipLeadingDataLines 
	 * @exception java.lang.Exception
	 *                The exception description.
	 * @throws SQLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @since
	 */
	public CsvReader(BufferedReader in, char separator,
			boolean suppressHeaders, char quoteChar, char commentChar,
			String headerLine, String extension, boolean trimHeaders, 
			int skipLeadingLines, boolean ignoreUnparseableLines, CryptoFilter filter, boolean defectiveHeaders, int skipLeadingDataLines)
			throws IOException, SQLException {
		this.separator = separator;
		this.suppressHeaders = suppressHeaders;
		this.quoteChar = quoteChar;
		this.commentChar = commentChar;
		this.headerLine = headerLine;
		this.extension = extension;
		this.trimHeaders = trimHeaders;
		this.input = in;
		this.ignoreUnparseableLines = ignoreUnparseableLines;
		this.filter = filter;

		for (int i=0; i<skipLeadingLines; i++){
			in.readLine();
		}

		if (this.suppressHeaders) {
			// column names specified by property are available. Read and use.
			if (this.headerLine != null) {
				this.columnNames = parseCsvLine(this.headerLine, trimHeaders);
			} else {
				// No column names available. Read first data line and determine
				// number of columns.
				buf = getNextDataLine();
				String[] data = parseCsvLine(buf, false);
				this.columnNames = new String[data.length];
				for (int i = 0; i < data.length; i++) {
					this.columnNames[i] = "COLUMN" + String.valueOf(i + 1);
				}
				data = null;
				// throw away.
			}
		} else {
			String tmpHeaderLine = getNextDataLine();
			this.columnNames = parseCsvLine(tmpHeaderLine, trimHeaders);
			Set uniqueNames = new HashSet();
			for (int i = 0; i < this.columnNames.length; i++)
				uniqueNames.add(this.columnNames[i]);
			if (uniqueNames.size() != this.columnNames.length)
				throw new SQLException("Table contains duplicated column names");
		}
		// some column names may be missing and should be corrected
		if (defectiveHeaders)
			for (int i = 0; i < this.columnNames.length; i++)
				if (this.columnNames[i].length() == 0)
					this.columnNames[i] = "COLUMN" + String.valueOf(i + 1);

		for (int i=0; i<skipLeadingDataLines; i++){
			in.readLine();
		}
	}

	/**
	 *Description of the Method
	 * 
	 * @return Description of the Returned Value
	 * @exception SQLException
	 *                Description of Exception
	 * @since
	 */
	public boolean next() throws SQLException {
		fieldValues = new String[columnNames.length];
		String dataLine = null;
		try {
			if (suppressHeaders && (buf != null)) {
				// The buffer is not empty yet, so use this first.
				dataLine = buf;
				buf = null;
			} else {
				// read new line of data from input.
				dataLine = getNextDataLine();
			}
			if (dataLine == null) {
				input.close();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e.toString());
		}
		fieldValues = parseCsvLine(dataLine, false);
		return true;
	}

	/**
	 *Description of the Method
	 * 
	 * @since
	 */
	public void close() {
		try {
			input.close();
			buf = null;
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @return the first data line that contains the correct amount of columns
	 * @throws IOException
	 */
	protected String getNextDataLine() throws IOException {
		String tmp = input.readLine();
		if (commentChar != 0 && tmp != null) {
			while (tmp.length() == 0 || tmp.charAt(0) == commentChar)
				tmp = input.readLine();
			// set it to 0: we don't skip data lines, only pre-header lines...
			commentChar = 0;
		}
		if(ignoreUnparseableLines && tmp!=null){
			try {
				do {
					int fieldsCount = this.parseCsvLine(tmp, true).length;
					if (columnNames != null && columnNames.length == fieldsCount)
						break; // we are satisfied
					if (columnNames == null && fieldsCount != 1)
						break; // also good enough - hopefully
					tmp = input.readLine();
				} while (tmp != null);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tmp;
	}

	/**
	 *Gets the columnNames attribute of the CsvReader object
	 * 
	 * @return The columnNames value
	 * @since
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * Get the value of the column at the specified index, 0 based.
	 * 
	 * @param columnIndex
	 *            Description of Parameter
	 * @return The column value
	 * @since
	 */
	public String getField(int columnIndex) throws SQLException {
		if (columnIndex >= fieldValues.length) {
			return null;
		}
		String result = fieldValues[columnIndex];
		if (result != null)
			result = result.trim();
		return result;
	}

	/**
	 * Get value from column at specified name. If the column name is not found,
	 * throw an error.
	 * 
	 * @param columnName
	 *            Description of Parameter
	 * @return The column value
	 * @exception SQLException
	 *                Description of Exception
	 * @since
	 */
	public String getField(String columnName) throws SQLException {
		for (int loop = 0; loop < columnNames.length; loop++) {
			if (columnName.equalsIgnoreCase(columnNames[loop])
					|| columnName.equalsIgnoreCase(getTableName() + "."
							+ columnNames[loop])) {
				return getField(loop);
			}
		}
		throw new SQLException("Column '" + columnName + "' not found.");
	}

	/**
	 * splits <b>line</b> into the String[] it contains.
	 * Stuart Mottram added the code for handling line breaks in fields.
	 * 
	 * @param line the line to parse
	 * @param trimValues tells whether to remove leading and trailing spaces
	 * @return
	 * @throws SQLException
	 */
	protected String[] parseCsvLine(String line, boolean trimValues)
			throws SQLException {
				Vector values = new Vector();
				boolean inQuotedString = false;
				String value = "";
				String orgLine = line;
				int currentPos = 0;
				int fullLine = 0;
			
				while (fullLine == 0) {
					currentPos = 0;
					line += separator;
					while (currentPos < line.length()) {
						char currentChar = line.charAt(currentPos);
						if (value.length() == 0 && currentChar == quoteChar
								&& !inQuotedString) {
							currentPos++;
							inQuotedString = true;
							continue;
						}
						if (currentChar == quoteChar) {
							char nextChar = line.charAt(currentPos + 1);
							if (nextChar == quoteChar) {
								value += currentChar;
								currentPos++;
							} else {
								if (!inQuotedString) {
									throw new SQLException("Unexpected '" + quoteChar
											+ "' in position " + currentPos + ". Line="
											+ orgLine);
								}
								if (inQuotedString && nextChar != separator) {
									throw new SQLException("Expecting " + separator
											+ " in position " + (currentPos + 1)
											+ ". Line=" + orgLine);
								}
								if (trimValues) {
									values.add(value.trim());
								} else {
									values.add(value);
								}
								value = "";
								inQuotedString = false;
								currentPos++;
							}
						} else {
							if (currentChar == separator) {
								if (inQuotedString) {
									value += currentChar;
								} else {
									if (trimValues) {
										values.add(value.trim());
									} else {
										values.add(value);
									}
									value = "";
								}
							} else {
								value += currentChar;
							}
						}
						currentPos++;
					}
					if (inQuotedString) {
						// Remove extra , added at start
						value = value.substring(0, value.length() - 1);
						try {
							line = "\n" + input.readLine();
						} catch (IOException e) {
							throw new SQLException(e.toString());
						}
					} else {
						fullLine = 1;
					}
			
				}
				String[] retVal = new String[values.size()];
				values.copyInto(retVal);
				return retVal;
			}

	/**
	 * Retrieves whether the cursor is before the first row in this
	 * <code>ResultSet</code> object.
	 * 
	 * @return <code>true</code> if the cursor is before the first row;
	 *         <code>false</code> if the cursor is at any other position or the
	 *         result set contains no rows
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isBeforeFirst() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.isBeforeFirst() unsupported");
	}

	/**
	 * Retrieves whether the cursor is after the last row in this
	 * <code>ResultSet</code> object.
	 * 
	 * @return <code>true</code> if the cursor is after the last row;
	 *         <code>false</code> if the cursor is at any other position or the
	 *         result set contains no rows
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isAfterLast() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.isAfterLast() unsupported");
	}

	/**
	 * Retrieves whether the cursor is on the first row of this
	 * <code>ResultSet</code> object.
	 * 
	 * @return <code>true</code> if the cursor is on the first row;
	 *         <code>false</code> otherwise
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isFirst() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.isFirst() unsupported");
	}

	/**
	 * Retrieves whether the cursor is on the last row of this
	 * <code>ResultSet</code> object. Note: Calling the method
	 * <code>isLast</code> may be expensive because the JDBC driver might need
	 * to fetch ahead one row in order to determine whether the current row is
	 * the last row in the result set.
	 * 
	 * @return <code>true</code> if the cursor is on the last row;
	 *         <code>false</code> otherwise
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isLast() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.isLast() unsupported");
	}

	/**
	 * Moves the cursor to the front of this <code>ResultSet</code> object, just
	 * before the first row. This method has no effect if the result set
	 * contains no rows.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is <code>TYPE_FORWARD_ONLY</code>
	 */
	public void beforeFirst() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.beforeFirst() unsupported");
	}

	/**
	 * Moves the cursor to the end of this <code>ResultSet</code> object, just
	 * after the last row. This method has no effect if the result set contains
	 * no rows.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is <code>TYPE_FORWARD_ONLY</code>
	 */
	public void afterLast() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.afterLast() unsupported");
	}

	/**
	 * Moves the cursor to the first row in this <code>ResultSet</code> object.
	 * 
	 * @return <code>true</code> if the cursor is on a valid row;
	 *         <code>false</code> if there are no rows in the result set
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean first() throws SQLException {
		throw new UnsupportedOperationException("ResultSet.first() unsupported");
	}

	/**
	 * Moves the cursor to the last row in this <code>ResultSet</code> object.
	 * 
	 * @return <code>true</code> if the cursor is on a valid row;
	 *         <code>false</code> if there are no rows in the result set
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean last() throws SQLException {
		throw new UnsupportedOperationException("ResultSet.last() unsupported");
	}

	/**
	 * Retrieves the current row number. The first row is number 1, the second
	 * number 2, and so on.
	 * 
	 * @return the current row number; <code>0</code> if there is no current row
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getRow() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.getRow() unsupported");
	}

	/**
	 * Moves the cursor to the given row number in this <code>ResultSet</code>
	 * object.
	 * 
	 * <p>
	 * If the row number is positive, the cursor moves to the given row number
	 * with respect to the beginning of the result set. The first row is row 1,
	 * the second is row 2, and so on.
	 * 
	 * <p>
	 * If the given row number is negative, the cursor moves to an absolute row
	 * position with respect to the end of the result set. For example, calling
	 * the method <code>absolute(-1)</code> positions the cursor on the last
	 * row; calling the method <code>absolute(-2)</code> moves the cursor to the
	 * next-to-last row, and so on.
	 * 
	 * <p>
	 * An attempt to position the cursor beyond the first/last row in the result
	 * set leaves the cursor before the first row or after the last row.
	 * 
	 * <p>
	 * <B>Note:</B> Calling <code>absolute(1)</code> is the same as calling
	 * <code>first()</code>. Calling <code>absolute(-1)</code> is the same as
	 * calling <code>last()</code>.
	 * 
	 * @param row
	 *            the number of the row to which the cursor should move. A
	 *            positive number indicates the row number counting from the
	 *            beginning of the result set; a negative number indicates the
	 *            row number counting from the end of the result set
	 * @return <code>true</code> if the cursor is on the result set;
	 *         <code>false</code> otherwise
	 * @exception SQLException
	 *                if a database access error occurs, or the result set type
	 *                is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean absolute(int row) throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.absolute() unsupported");
	}

	/**
	 * Moves the cursor a relative number of rows, either positive or negative.
	 * Attempting to move beyond the first/last row in the result set positions
	 * the cursor before/after the the first/last row. Calling
	 * <code>relative(0)</code> is valid, but does not change the cursor
	 * position.
	 * 
	 * <p>
	 * Note: Calling the method <code>relative(1)</code> is identical to calling
	 * the method <code>next()</code> and calling the method
	 * <code>relative(-1)</code> is identical to calling the method
	 * <code>previous()</code>.
	 * 
	 * @param rows
	 *            an <code>int</code> specifying the number of rows to move from
	 *            the current row; a positive number moves the cursor forward; a
	 *            negative number moves the cursor backward
	 * @return <code>true</code> if the cursor is on a row; <code>false</code>
	 *         otherwise
	 * @exception SQLException
	 *                if a database access error occurs, there is no current
	 *                row, or the result set type is
	 *                <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean relative(int rows) throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.relative() unsupported");
	}

	/**
	 * Moves the cursor to the previous row in this <code>ResultSet</code>
	 * object.
	 * 
	 * @return <code>true</code> if the cursor is on a valid row;
	 *         <code>false</code> if it is off the result set
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean previous() throws SQLException {
		throw new UnsupportedOperationException(
				"ResultSet.previous() unsupported");
	}
}
