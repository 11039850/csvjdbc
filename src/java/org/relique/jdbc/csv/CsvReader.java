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
 * @version $Id: CsvReader.java,v 1.26 2010/05/04 14:58:34 mfrasca Exp $
 */

public class CsvReader extends CSVReaderAdapter {

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
		super(in, separator, suppressHeaders, quoteChar, commentChar,
				headerLine, extension, trimHeaders, skipLeadingLines,
				ignoreUnparseableLines, filter, defectiveHeaders, skipLeadingDataLines);
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
}
