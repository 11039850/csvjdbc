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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.sql.Types;

import org.relique.jdbc.csv.CsvResultSet;

import junit.framework.TestCase;

/**
 * This class is used to test the CsvJdbc driver.
 * 
 * @author Jonathan Ackerman
 * @author JD Evora
 * @author Chetan Gupta
 * @author Mario Frasca
 * @version $Id: TestCsvDriver.java,v 1.27 2008/11/25 08:08:55 mfrasca Exp $
 */
public class TestCsvDriver extends TestCase {
	public static final String SAMPLE_FILES_LOCATION_PROPERTY = "sample.files.location";
	private String filePath;

	public TestCsvDriver(String name) {
		super(name);
	}

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

	public void testWithDefaultValues() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT NAME,ID,EXTRA_FIELD FROM sample");

		results.next();
		assertEquals("Incorrect ID Value", "Q123", results.getString("ID"));
		assertEquals("Incorrect NAME Value", "\"S,\"", results
				.getString("NAME"));
		assertEquals("Incorrect EXTRA_FIELD Value", "F", results
				.getString("EXTRA_FIELD"));

		assertEquals("Incorrect Column 1 Value", "\"S,\"", results.getString(1));
		assertEquals("Incorrect Column 2 Value", "Q123", results.getString(2));
		assertEquals("Incorrect Column 3 Value", "F", results.getString(3));

		results.next();
		assertEquals("Incorrect ID Value", "A123", results.getString("ID"));
		assertEquals("Incorrect NAME Value", "Jonathan Ackerman", results
				.getString("NAME"));
		assertEquals("Incorrect EXTRA_FIELD Value", "A", results
				.getString("EXTRA_FIELD"));

		results.next();
		assertEquals("Incorrect ID Value", "B234", results.getString("ID"));
		assertEquals("Incorrect NAME Value", "Grady O'Neil", results
				.getString("NAME"));
		assertEquals("Incorrect EXTRA_FIELD Value", "B", results
				.getString("EXTRA_FIELD"));

		results.next();
		assertEquals("Incorrect ID Value", "C456", results.getString("ID"));
		assertEquals("Incorrect NAME Value", "Susan, Peter and Dave", results
				.getString("NAME"));
		assertEquals("Incorrect EXTRA_FIELD Value", "C", results
				.getString("EXTRA_FIELD"));

		results.next();
		assertEquals("Incorrect ID Value", "D789", results.getString("ID"));
		assertEquals("Incorrect NAME Value", "Amelia \"meals\" Maurice",
				results.getString("NAME"));
		assertEquals("Incorrect EXTRA_FIELD Value", "E", results
				.getString("EXTRA_FIELD"));

		results.next();
		assertEquals("Incorrect ID Value", "X234", results.getString("ID"));
		assertEquals("Incorrect NAME Value",
				"Peter \"peg leg\", Jimmy & Samantha \"Sam\"", results
						.getString("NAME"));
		assertEquals("Incorrect EXTRA_FIELD Value", "G", results
				.getString("EXTRA_FIELD"));

		results.close();
		stmt.close();
		conn.close();
	}

	/**
	 * This creates several sentences with where and tests they work
	 * 
	 * @throws SQLException
	 */
	public void testWhereSimple() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT ID,Name FROM sample4 WHERE ID='03'");
		assertTrue(results.next());
		assertEquals("The name is wrong", "Maria Cristina Lucero", results
				.getString("Name"));
		try {
			assertNull(results.getString("Job"));
			fail("Should not find the column 'Job'");
		} catch (SQLException e) {
		}
		assertTrue(!results.next());
	}

	/**
	 * fields in different order than in source file.
	 * 
	 * @throws SQLException
	 */
	public void testWhereShuffled() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT Job,ID,Name FROM sample4 WHERE ID='02'");
		assertTrue("no results found - should be one", results.next());
		assertEquals("The name is wrong", "Mauricio Hernandez", results
				.getString("Name"));
		assertEquals("The job is wrong", "Project Manager", results
				.getString("Job"));
		assertTrue("more than one matching records", !results.next());
	}

	public void testWithProperties() throws SQLException {
		Properties props = new Properties();
		props.put("fileExtension", "");
		props.put("separator", ";");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT NAME,ID,EXTRA_FIELD FROM sample");

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("Q123"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"\"S;\""));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("F"));

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("A123"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"Jonathan Ackerman"));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("A"));

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("B234"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"Grady O'Neil"));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("B"));

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("C456"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"Susan; Peter and Dave"));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("C"));

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("D789"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"Amelia \"meals\" Maurice"));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("E"));

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("X234"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"Peter \"peg leg\"; Jimmy & Samantha \"Sam\""));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("G"));

		results.close();
		stmt.close();
		conn.close();
	}

	public void testMetadata() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt.executeQuery("SELECT * FROM sample3");

		ResultSetMetaData metadata = results.getMetaData();

		assertEquals("Incorrect Table Name", "sample3", metadata
				.getTableName(0));

		assertEquals("Incorrect Column Name 1", "column 1", metadata
				.getColumnName(1));
		assertEquals("Incorrect Column Name 2", "column \"2\" two", metadata
				.getColumnName(2));
		assertEquals("Incorrect Column Name 3", "Column 3", metadata
				.getColumnName(3));
		assertEquals("Incorrect Column Name 4", "CoLuMn4", metadata
				.getColumnName(4));
		assertEquals("Incorrect Column Name 5", "COLumn5", metadata
				.getColumnName(5));

		results.close();
		stmt.close();
		conn.close();
	}

	public void testMetadataWithSupressedHeaders() throws SQLException {
		Properties props = new Properties();
		props.put("suppressHeaders", "true");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt.executeQuery("SELECT * FROM sample");

		ResultSetMetaData metadata = results.getMetaData();

		assertTrue("Incorrect Table Name", metadata.getTableName(0).equals(
				"sample"));

		assertTrue("Incorrect Column Name 1", metadata.getColumnName(1).equals(
				"COLUMN1"));
		assertTrue("Incorrect Column Name 2", metadata.getColumnName(2).equals(
				"COLUMN2"));
		assertTrue("Incorrect Column Name 3", metadata.getColumnName(3).equals(
				"COLUMN3"));

		results.close();
		stmt.close();
		conn.close();
	}

	public void testMetadataWithColumnType() throws SQLException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Timestamp");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT id, name, job, start FROM sample5");

		ResultSetMetaData metadata = results.getMetaData();

		assertEquals("type of column 1 is incorrect", Types.INTEGER, metadata
				.getColumnType(1));
		assertEquals("type of column 2 is incorrect", Types.VARCHAR, metadata
				.getColumnType(2));
		assertEquals("type of column 3 is incorrect", Types.VARCHAR, metadata
				.getColumnType(3));
		assertEquals("type of column 4 is incorrect", Types.TIMESTAMP, metadata
				.getColumnType(4));

		assertEquals("type of column 1 is incorrect", "Int", metadata
				.getColumnTypeName(1));
		assertEquals("type of column 2 is incorrect", "String", metadata
				.getColumnTypeName(2));
		assertEquals("type of column 3 is incorrect", "String", metadata
				.getColumnTypeName(3));
		assertEquals("type of column 4 is incorrect", "Timestamp", metadata
				.getColumnTypeName(4));

		results.close();
		stmt.close();
		conn.close();
	}


	public void testMetadataWithOperations() throws SQLException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Date,Time");
		props.put("timeFormat", "HHMM");
		props.put("dateFormat", "YYYY-mm-dd");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT id, start, timeoffset, start+timeoffset AS ts FROM sample5");
		ResultSetMetaData metadata = results.getMetaData();

		assertEquals("type of column 1 is incorrect", Types.INTEGER, metadata
				.getColumnType(1));
		assertEquals("type of column 2 is incorrect", Types.DATE, metadata
				.getColumnType(2));
		assertEquals("type of column 3 is incorrect", Types.TIME, metadata
				.getColumnType(3));
		assertEquals("type of column 4 is incorrect", Types.TIMESTAMP, metadata
				.getColumnType(4));
	}

	public void testColumnTypesUserSpecified() throws SQLException,
			ParseException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Date");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT ID, Name, Job, Start "
				+ "FROM sample5 WHERE Job = 'Project Manager'");

		assertTrue(results.next());
		assertEquals("Integer column ID is wrong", new Integer(1), results
				.getObject("id"));
		assertEquals("Integer column 1 is wrong", new Integer(1), results
				.getObject(1));
		java.sql.Date shouldBe = java.sql.Date.valueOf("2001-01-02");
		assertEquals("Date column Start is wrong", shouldBe, results
				.getObject("start"));
		assertEquals("Date column 4 is wrong", shouldBe, results.getObject(4));
		assertEquals("The Name is wrong", "Juan Pablo Morales", results
				.getObject("name"));
	}

	public void testColumnTypesUserSpecifiedShuffled() throws SQLException,
			ParseException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Date");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT ID, Start, Name, Job "
				+ "FROM sample5 WHERE Job = 'Project Manager'");

		assertTrue(results.next());
		assertEquals("Integer column ID is wrong", new Integer(1), results
				.getObject("id"));
		assertEquals("Integer column 1 is wrong", new Integer(1), results
				.getObject(1));
		java.sql.Date shouldBe = java.sql.Date.valueOf("2001-01-02");
		assertEquals("Date column Start is wrong", shouldBe, results
				.getObject("start"));
		assertEquals("Date column 4 is wrong", shouldBe, results.getObject(2));
		assertEquals("The Name is wrong", "Juan Pablo Morales", results
				.getObject("name"));
	}

	public void testColumnTypesUserSpecifiedTS() throws SQLException,
			ParseException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Timestamp");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT ID, Name, Job, Start "
				+ "FROM sample5 WHERE Job = 'Project Manager'");

		assertTrue(results.next());
		DateFormat dfp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertEquals("Integer column ID is wrong", new Integer(1), results
				.getObject("id"));
		assertEquals("Integer column 1 is wrong", new Integer(1), results
				.getObject(1));
		assertEquals("Date column Start is wrong", dfp.parse(results
				.getString("start")), results.getObject("start"));
		assertEquals("Date column 4 is wrong", dfp.parse(results
				.getString("start")), results.getObject(4));
		assertEquals("The Name is wrong", "Juan Pablo Morales", results
				.getObject("name"));
	}

	/**
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void testColumnTypesInferFromData() throws SQLException,
			ParseException {
		Properties props = new Properties();
		props.put("columnTypes", "");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT ID, Name, Job, Start "
				+ "FROM sample5");

		assertTrue(results.next());
		ResultSetMetaData metadata = results.getMetaData();
		assertEquals("type of column 1 is incorrect", Types.INTEGER, metadata
				.getColumnType(1));
		assertEquals("type of column 2 is incorrect", Types.VARCHAR, metadata
				.getColumnType(2));
		assertEquals("type of column 3 is incorrect", Types.VARCHAR, metadata
				.getColumnType(3));
		assertEquals("type of column 4 is incorrect", Types.TIMESTAMP, metadata
				.getColumnType(4));
	}

	public void testColumnTypesDefaultBehaviour() throws SQLException,
			ParseException {
		Properties props = new Properties();

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT ID, Name, Job, Start "
				+ "FROM sample5 WHERE Job = 'Project Manager'");

		assertTrue(results.next());
		assertEquals("the start time is wrong", "2001-01-02 12:30:00", results
				.getObject("start"));
		assertEquals("The ID is wrong", "01", results.getObject("id"));
		assertEquals("The Name is wrong", "Juan Pablo Morales", results
				.getObject("name"));
	}

	public void testColumnTypesWithSelectStar() throws SQLException,
			ParseException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Timestamp");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * "
				+ "FROM sample5 WHERE Job = 'Project Manager'");

		assertTrue(results.next());
		java.sql.Timestamp shouldBe = java.sql.Timestamp
				.valueOf("2001-01-02 12:30:00");
		assertEquals("the start time is wrong", shouldBe, results
				.getObject("start"));
		assertEquals("The ID is wrong", new Integer(1), results.getObject("id"));
		assertEquals("The Name is wrong", "Juan Pablo Morales", results
				.getObject("name"));
	}

	public void testWithSuppressedHeaders() throws SQLException {
		Properties props = new Properties();
		props.put("suppressHeaders", "true");

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt.executeQuery("SELECT * FROM sample");

		// header is now treated as normal data line
		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("ID"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("NAME"));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("EXTRA_FIELD"));

		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("Q123"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("\"S,\""));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("F"));

		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("A123"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("Jonathan Ackerman"));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("A"));

		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("B234"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("Grady O'Neil"));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("B"));

		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("C456"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("Susan, Peter and Dave"));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("C"));

		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("D789"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("Amelia \"meals\" Maurice"));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("E"));

		results.next();
		assertTrue("Incorrect COLUMN1 Value", results.getString("COLUMN1")
				.equals("X234"));
		assertTrue("Incorrect COLUMN2 Value", results.getString("COLUMN2")
				.equals("Peter \"peg leg\", Jimmy & Samantha \"Sam\""));
		assertTrue("Incorrect COLUMN3 Value", results.getString("COLUMN3")
				.equals("G"));

		results.close();
		stmt.close();
		conn.close();
	}

	public void testRelativePath() throws SQLException {
		// break up file path to test relative paths
		String parentPath = new File(filePath).getParent();
		String subPath = new File(filePath).getName();

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ parentPath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT NAME,ID,EXTRA_FIELD FROM ."
						+ File.separator + subPath + File.separator + "sample");

		results.next();
		assertTrue("Incorrect ID Value", results.getString("ID").equals("Q123"));
		assertTrue("Incorrect NAME Value", results.getString("NAME").equals(
				"\"S,\""));
		assertTrue("Incorrect EXTRA_FIELD Value", results.getString(
				"EXTRA_FIELD").equals("F"));

		results.close();
		stmt.close();
		conn.close();
	}

	public void testWhereMultipleResult() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT ID, Name, Job FROM sample4 WHERE Job = 'Project Manager'");
		assertTrue(results.next());
		assertEquals("The ID is wrong", "01", results.getString("ID"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "02", results.getString("ID"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "04", results.getString("ID"));
		assertTrue(!results.next());
	}

	public void testFieldAsAlias() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT ID as i, Name as n, Job as j FROM sample4 WHERE j='Project Manager'");
		assertTrue(results.next());
		assertEquals("The ID is wrong", "01", results.getString("i"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "02", results.getString("i"));
		assertEquals("The name is wrong", "Mauricio Hernandez", results
				.getString("N"));
		assertEquals("The name is wrong", "Mauricio Hernandez", results
				.getString(2));
		assertEquals("The job is wrong", "Project Manager", results
				.getString("J"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "04", results.getString("i"));
		assertTrue(!results.next());
	}

	public void testSelectStar() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		CsvResultSet results = (CsvResultSet) stmt
				.executeQuery("SELECT * FROM sample4");
		assertEquals("ID", results.getMetaData().getColumnName(1).toString());
		assertEquals("[ID]", results.getMetaData().getColumnLabel(1).toString());

		assertTrue(results.next());
		assertEquals("The ID is wrong", "01", results.getString("id"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "02", results.getString("id"));
		assertEquals("The name is wrong", "Mauricio Hernandez", results
				.getString("Name"));
		assertEquals("The name is wrong", "Mauricio Hernandez", results
				.getString(2));
		assertEquals("The job is wrong", "Project Manager", results
				.getString("Job"));
		assertTrue(results.next());
		assertTrue(results.next());
		assertEquals("The ID is wrong", "04", results.getString("id"));
		assertTrue(!results.next());
	}

	public void testLiteralAsAlias() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT Job j,ID i,Name n, 0 c FROM sample4");
		assertTrue("no results found - should be all", results.next());
		assertTrue("no results found - should be all", results.next());
		assertEquals("The literal c is wrong", "0", results.getString("c"));
		assertEquals("The literal c is wrong", "0", results.getString(4));
		assertEquals("The name is wrong", "Mauricio Hernandez", results
				.getString("N"));
		assertEquals("The job is wrong", "Project Manager", results
				.getString("J"));
	}

	/**
	 * This returns no results with where and tests if this still works
	 * 
	 * @throws SQLException
	 */
	public void testWhereNoResults() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);
		Statement stmt = conn.createStatement();
		ResultSet results = stmt
				.executeQuery("SELECT ID,Name FROM sample4 WHERE ID='05'");
		assertFalse(results.next());
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void testSelectStarWhereMultipleResult() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT * FROM sample4 WHERE Job = 'Project Manager'");
		assertTrue(results.next());
		assertEquals("The ID is wrong", "01", results.getString("ID"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "02", results.getString("ID"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", "04", results.getString("ID"));
		assertTrue(!results.next());
	}

	/**
	 * @throws SQLException
	 */
	public void testWhereWithAndOperator() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT * FROM sample4 WHERE Job = 'Project Manager' AND Name = 'Mauricio Hernandez'");
		assertTrue(results.next());
		assertEquals("The ID is wrong", "02", results.getString("ID"));
		assertTrue(!results.next());
	}

	/**
	 * @throws SQLException
	 */
	public void testWhereWithBetweenOperator() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT * FROM sample4 WHERE id BETWEEN '02' AND '03'");

		assertTrue(results.next());
		assertEquals("The ID is wrong", 2, results.getInt("ID"));
		assertTrue(results.next());
		assertEquals("The ID is wrong", 3, results.getInt("ID"));
		assertTrue(!results.next());
	}

	/**
	 * @throws SQLException
	 */
	public void testWhereWithUnselectedColumn() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT Name, Job FROM sample4 WHERE id = '04'");
		assertTrue(results.next());
		try {
			assertNull(results.getString("id"));
			fail("Should not find the column 'id'");
		} catch (SQLException e) {
		}
		assertEquals("The name is wrong", "Felipe Grajales", results
				.getString("name"));
		assertTrue(!results.next());
	}

	/**
	 * @throws SQLException
	 */
	public void test1073375() throws SQLException {
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("separator", "\t");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt.executeQuery("SELECT * FROM jo");
		assertTrue(results.next());
		try {
			assertNull(results.getString("id"));
			fail("Should not find the column 'id'");
		} catch (SQLException e) {
		}
		assertEquals("The name is wrong", "3", results.getString("rc"));
		// would like to test the full_name_nd, but can't insert the Arabic
		// string in the code
		assertTrue(results.next());
		assertEquals("The name is wrong", "3", results.getString("rc"));
		assertTrue(results.next());
		assertEquals("The name is wrong", "3", results.getString("rc"));
		assertEquals("The name is wrong", "Tall Dhayl", results
				.getString("full_name_nd"));
	}

	/**
	 * @throws SQLException
	 */
	public void test0733215() throws SQLException {
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt.executeQuery("SELECT * FROM witheol");
		assertTrue(results.next());
		assertEquals("The name is wrong", "1", results.getString("key"));
		// would like to test the full_name_nd, but can't insert the Arabic
		// string in the code
		assertTrue(results.next());
		assertEquals("The name is wrong", "2", results.getString("key"));
		assertTrue(results.next());
		assertEquals("The name is wrong", "3", results.getString("key"));
		assertEquals("The name is wrong", "123\n456\n789", results
				.getString("value"));
		assertTrue(!results.next());
	}

	public void testColumnWithDot() throws SQLException {

		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT datum, tijd, station, ai007.000 as value FROM test-001-20081112");
		assertTrue(results.next());
		assertEquals("The name is wrong", "20-12-2007", results
				.getString("datum"));
		assertEquals("The name is wrong", "10:59:00", results.getString("tijd"));
		assertEquals("The name is wrong", "007", results.getString("station"));
		assertEquals("The name is wrong", "0.0", results.getString("value"));
	}

	public void testFromIndexedTable() throws SQLException {

		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("fileTailPattern", "-([0-9]{3})-([0-9]{8})");
		props.put("fileTailParts", "location,file_date");
		props.put("indexedFiles", "True");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT location,station,datum,tijd,file_date FROM test");

		ResultSetMetaData metadata = results.getMetaData();

		assertTrue("Incorrect Table Name", metadata.getTableName(0).equals(
				"test"));

		assertEquals("Incorrect Column Name 1", metadata.getColumnName(1),
				"LOCATION");
		assertEquals("Incorrect Column Name 2", metadata.getColumnName(2),
				"STATION");
		assertEquals("Incorrect Column Name 3", metadata.getColumnName(3),
				"DATUM");
		assertEquals("Incorrect Column Name 4", metadata.getColumnName(4),
				"TIJD");

		assertTrue(results.next());
		assertEquals("wrong datum", "20-12-2007", results.getString("datum"));
		assertEquals("wrong tijd", "10:59:00", results.getString("tijd"));
		assertEquals("wrong station", "007", results.getString("station"));
		assertEquals("wrong location #0", "001", results.getString("location"));
		for (int i = 1; i < 12; i++) {
			assertTrue(results.next());
			assertEquals("wrong location #" + i, "001", results
					.getString("location"));
			assertEquals("wrong file_date #" + i, "20081112", results
					.getString("file_date"));
		}
		for (int i = 0; i < 12; i++) {
			assertTrue(results.next());
			assertEquals("wrong location #" + i, "001", results
					.getString("location"));
			assertEquals("wrong file_date #" + i, "20081113", results
					.getString("file_date"));
		}
		for (int i = 0; i < 12; i++) {
			assertTrue(results.next());
			assertEquals("wrong location #" + i, "001", results
					.getString("location"));
			assertEquals("wrong file_date #" + i, "20081114", results
					.getString("file_date"));
		}
		for (int i = 0; i < 36; i++) {
			assertTrue(results.next());
			assertEquals("wrong location #" + i, "002", results
					.getString("location"));
		}
		for (int i = 0; i < 36; i++) {
			assertTrue(results.next());
			assertEquals("wrong location #" + i, "003", results
					.getString("location"));
		}
		for (int i = 0; i < 36; i++) {
			assertTrue(results.next());
			assertEquals("wrong location #" + i, "004", results
					.getString("location"));
		}
		assertFalse(results.next());
	}

	/**
	 * @throws SQLException
	 */
	public void testAddingFields() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath);

		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT id + ' ' + job as mix FROM sample4");
		assertTrue(results.next());
		assertEquals("The mix is wrong", "01 Project Manager", results
				.getString("mix"));
		assertTrue(results.next());
		assertEquals("The mix is wrong", "02 Project Manager", results
				.getString("mix"));
		assertTrue(results.next());
		assertEquals("The mix is wrong", "03 Finance Manager", results
				.getString("mix"));
		assertTrue(results.next());
		assertEquals("The mix is wrong", "04 Project Manager", results
				.getString("mix"));
		assertTrue(!results.next());
	}
	
	public void testAddingDateToTime() throws SQLException {
		Properties props = new Properties();
		props.put("columnTypes", "Int,String,String,Date,Time");
		props.put("timeFormat", "HHMM");
		props.put("dateFormat", "YYYY-mm-dd");
		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);

		Object expect;
		Statement stmt = conn.createStatement();

		ResultSet results = stmt
				.executeQuery("SELECT id, start, timeoffset, start+timeoffset AS ts FROM sample5 WHERE id=41 OR id=4");

		assertTrue(results.next());
		expect = java.sql.Date.valueOf("2001-04-02");
		assertEquals("Date is a Date", expect.getClass(), results.getObject("start").getClass());
		assertEquals("Date is a Date", expect, results.getObject("start"));
		expect = java.sql.Time.valueOf("12:30:00");
		assertEquals("Time is a Time", expect.getClass(), results.getObject("timeoffset").getClass());
		assertEquals("Time is a Time", expect, results.getObject("timeoffset"));
		expect = java.sql.Timestamp.valueOf("2001-04-02 12:30:00");
		assertEquals("adding Date to Time", expect.getClass(), results.getObject("ts").getClass());
		assertEquals("adding Date to Time", expect, results.getObject("ts"));

		assertTrue(results.next());
		expect = java.sql.Date.valueOf("2004-04-02");
		assertEquals("Date is a Date", expect.getClass(), results.getObject("start").getClass());
		assertEquals("Date is a Date", expect, results.getObject("start"));
		expect = java.sql.Time.valueOf("01:00:00");
		assertEquals("Time is a Time", expect.getClass(), results.getObject("timeoffset").getClass());
		assertEquals("Time is a Time", expect, results.getObject("timeoffset"));
		expect = java.sql.Timestamp.valueOf("2004-04-02 01:00:00");
		assertEquals("adding Date to Time", expect.getClass(), results.getObject("ts").getClass());
		assertEquals("adding Date to Time", expect, results.getObject("ts"));

		assertFalse(results.next());
	}

}