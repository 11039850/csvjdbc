/*
CsvJdbc - a JDBC driver for CSV files
Copyright (C) 2001  Jonathan Ackerman

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package test.org.relique.jdbc.csv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

public class TestFixedWidthFiles extends TestCase {

	public static final String SAMPLE_FILES_LOCATION_PROPERTY = "sample.files.location";
	private String filePath;

	protected void setUp() {
		filePath = System.getProperty(SAMPLE_FILES_LOCATION_PROPERTY);
		if (filePath == null)
			filePath = RunTests.DEFAULT_FILEPATH;
		assertNotNull("Sample files location property not set !", filePath);

		// load CSV driver
		try {
			Class.forName("org.relique.jdbc.csv.CsvDriver");
		} catch (ClassNotFoundException e) {
			fail("Driver is not in the CLASSPATH -> " + e);
		}
	}

	public void testFixedWidth() throws SQLException {
		
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("fixedWidths", "1-16,17-24,25-27,35-42,43-50,51-58");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + filePath, props);

		Statement stmt = conn.createStatement();

		// GERMANY         Euro    EUR       0.7616450.7632460.002102
		ResultSet rs1 = stmt.executeQuery("SELECT * FROM currency-exchange-rates-fixed");
		assertTrue(rs1.next());
		assertEquals("Country rate is wrong", "GERMANY", rs1.getString("Country"));
		assertEquals("Currency rate is wrong", "Euro", rs1.getString("Currency"));
		assertEquals("ISO rate is wrong", "EUR", rs1.getString("ISO"));
		assertEquals("YESTERDY rate is wrong", "0.761645", rs1.getString("YESTERDY"));
		assertEquals("TODAY_ rate is wrong", "0.763246", rs1.getString("TODAY_"));
		assertEquals("% Change rate is wrong", "0.002102", rs1.getString("% Change"));

		assertTrue(rs1.next());
		assertEquals("Country rate is wrong", "GREECE", rs1.getString(1));
		assertEquals("Currency rate is wrong", "Euro", rs1.getString(2));
		assertEquals("ISO rate is wrong", "EUR", rs1.getString(3));
		assertEquals("YESTERDY rate is wrong", "0.761645", rs1.getString(4));
		assertEquals("TODAY_ rate is wrong", "0.763246", rs1.getString(5));
		assertEquals("% Change rate is wrong", "0.002102", rs1.getString(6));

		assertTrue(rs1.next());
	}

	public void testHeaderline() throws SQLException {
		
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("headerline", "Country,Currency,ISO,YESTERDAY,TODAY,% Change");
		props.put("suppressHeaders", "true");
		props.put("columnTypes", "String,String,String,Double,Double,Double");
		props.put("fixedWidths", "1-16,17-24,25-27,35-42,43-50,51-58");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + filePath, props);

		Statement stmt = conn.createStatement();

		// GERMANY         Euro    EUR       0.7616450.7632460.002102
		ResultSet rs1 = stmt
				.executeQuery("SELECT * FROM currency-exchange-rates-fixed c WHERE c.Country = 'GERMANY'");
		assertTrue(rs1.next());
		assertEquals("ISO rate is wrong", "EUR", rs1.getString("ISO"));
		assertEquals("YESTERDY rate is wrong", "0.761645", rs1.getString("YESTERDAY"));
		assertEquals("TODAY_ rate is wrong", "0.763246", rs1.getString("TODAY_"));
		assertEquals("% Change rate is wrong", "0.002102", rs1.getString("% Change"));
		
		// HUNGARY         Forint  HUF       226.1222226.67130.002429
		ResultSet rs2 = stmt
				.executeQuery("SELECT * FROM currency-exchange-rates-fixed c WHERE c.Country = 'HUNGARY'");
		assertTrue(rs2.next());
		assertEquals("ISO rate is wrong", "HUF", rs2.getString("ISO"));
		assertEquals("YESTERDY rate is wrong", "226.1222", rs2.getString("YESTERDAY"));
		assertEquals("TODAY_ rate is wrong", "226.6713", rs2.getString("TODAY_"));
		assertEquals("% Change rate is wrong", "0.002429", rs2.getString("% Change"));
		
		// PERU            Sol     PEN       2.6618362.661836       0
		ResultSet rs3 = stmt
				.executeQuery("SELECT * FROM currency-exchange-rates-fixed c WHERE c.Country = 'PERU'");
		assertTrue(rs3.next());
		assertEquals("ISO rate is wrong", "PEN", rs3.getString("ISO"));
		assertEquals("YESTERDY rate is wrong", "2.661836", rs3.getString("YESTERDAY"));
		assertEquals("TODAY_ rate is wrong", "2.661836", rs3.getString("TODAY_"));
		assertEquals("% Change rate is wrong", "0.0", rs3.getString("% Change"));
		
		//SAUDI ARABIA    Riyal   SAR       3.7504133.750361-1.4E-05
		ResultSet rs4 = stmt
				.executeQuery("SELECT * FROM currency-exchange-rates-fixed c WHERE c.Country = 'SAUDI ARABIA'");
		assertTrue(rs4.next());
		assertEquals("ISO rate is wrong", "SAR", rs4.getString("ISO"));
		assertEquals("YESTERDY rate is wrong", "3.750413", rs4.getString("YESTERDAY"));
		assertEquals("TODAY_ rate is wrong", "3.750361", rs4.getString("TODAY_"));
		assertEquals("% Change rate is wrong", "-1.4E-5", rs4.getString("% Change"));
	}

	public void testNumericColumns() throws SQLException {
		
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("columnTypes", "String,String,String,Double,Double,Double");
		props.put("fixedWidths", "1-16,17-24,25-27,35-42,43-50,51-58");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + filePath, props);

		Statement stmt = conn.createStatement();

		//SWEDEN          Krona   SEK       6.76557 6.752711 -0.0019
		//SWITZERLAND     Franc   CHF       0.9153470.9178320.002715
		ResultSet rs1 = stmt.executeQuery("SELECT * FROM currency-exchange-rates-fixed " +
			"WHERE Country='SWEDEN' OR Country='SWITZERLAND'");
		int multiplier = 1000;
		assertTrue(rs1.next());
		assertEquals("TODAY_ is wrong", Math.round(6.76557 * multiplier), Math.round(rs1.getDouble("TODAY_") * multiplier));
		assertEquals("YESTERDY is wrong", Math.round(6.752711 * multiplier), Math.round(rs1.getDouble("YESTERDY") * multiplier));
		assertEquals("% Change is wrong", Math.round(-0.0019 * multiplier), Math.round(rs1.getDouble("% Change") * multiplier));

		assertTrue(rs1.next());
		assertEquals("TODAY_ is wrong", Math.round(0.915347 * multiplier), Math.round(rs1.getDouble("TODAY_") * multiplier));
		assertEquals("YESTERDY is wrong", Math.round(0.917832 * multiplier), Math.round(rs1.getDouble("YESTERDY") * multiplier));
		assertEquals("% Change is wrong", Math.round(0.002715 * multiplier), Math.round(rs1.getDouble("% Change") * multiplier));
	}

	public void testWidthOrder() throws SQLException {
		
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("columnTypes", "String,Integer,String");
		props.put("fixedWidths", "30-32,29,25-28");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet rs1 = stmt.executeQuery("SELECT * FROM flights");
		assertTrue(rs1.next());
		assertEquals("Column 1 is wrong", "A18", rs1.getString(1));
		assertEquals("Column 2 is wrong", "1", rs1.getInt(2));
		assertEquals("Column 3 is wrong", "", rs1.getString(3));
		
		assertTrue(rs1.next());
		assertEquals("Column 1 is wrong", "B2", rs1.getString(1));
		assertEquals("Column 2 is wrong", "1", rs1.getInt(2));
		assertEquals("Column 3 is wrong", "", rs1.getString(3));

		assertTrue(rs1.next());
		assertEquals("Column 1 is wrong", "D4", rs1.getString(1));
		assertEquals("Column 2 is wrong", "2", rs1.getInt(2));
		assertEquals("Column 3 is wrong", "", rs1.getString(3));
		
		assertTrue(rs1.next());
		assertEquals("Column 1 is wrong", "A22", rs1.getString(1));
		assertEquals("Column 2 is wrong", "1", rs1.getInt(2));
		assertEquals("Column 3 is wrong", "1320", rs1.getString(3));
	}
}
