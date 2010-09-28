package test.org.relique.jdbc.csv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

public class TestJoinedTables extends TestCase {

	public static final String SAMPLE_FILES_LOCATION_PROPERTY = "sample.files.location";
	private String filePath;

	public TestJoinedTables(String name) {
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
	
	public void testHeaderIsTransposedFirstTable() throws SQLException {
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("headerline", "L,P,K,U,W,D,T,V");

		// setting both transposedLines and skipTransposedFields informs the
		// driver we are receiving a compacted join
		
		// L,P,K,U,W, leaving D,T as regular fields, V as matrix of joined values 
		props.put("transposedLines", "5"); 
		
		// the first column in the transposed table holds the (ignored) header.
		props.put("transposedFieldsToSkip", "1");
		// the driver must be told that there is no header.
		props.put("suppressHeaders", "true");

		ResultSet results = null;

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();

		results = stmt.executeQuery("SELECT * FROM twojoinedtables01");
		assertTrue("there should be results", results.next());
		assertEquals("l1", results.getObject("L"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("k1", results.getObject("K"));
		assertEquals("u1", results.getObject("U"));
		assertEquals("w1", results.getObject("W"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v11", results.getObject("V"));

		assertTrue("there should be results", results.next());
		assertEquals("l2", results.getObject("L"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("k2", results.getObject("K"));
		assertEquals("u2", results.getObject("U"));
		assertEquals("w2", results.getObject("W"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v12", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l3", results.getObject("L"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("k3", results.getObject("K"));
		assertEquals("u3", results.getObject("U"));
		assertEquals("w3", results.getObject("W"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v13", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l1", results.getObject("L"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("k1", results.getObject("K"));
		assertEquals("u1", results.getObject("U"));
		assertEquals("w1", results.getObject("W"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v21", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l2", results.getObject("L"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("k2", results.getObject("K"));
		assertEquals("u2", results.getObject("U"));
		assertEquals("w2", results.getObject("W"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v22", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l3", results.getObject("L"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("k3", results.getObject("K"));
		assertEquals("u3", results.getObject("U"));
		assertEquals("w3", results.getObject("W"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v23", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l1", results.getObject("L"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("k1", results.getObject("K"));
		assertEquals("u1", results.getObject("U"));
		assertEquals("w1", results.getObject("W"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v31", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l2", results.getObject("L"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("k2", results.getObject("K"));
		assertEquals("u2", results.getObject("U"));
		assertEquals("w2", results.getObject("W"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v32", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l3", results.getObject("L"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("k3", results.getObject("K"));
		assertEquals("u3", results.getObject("U"));
		assertEquals("w3", results.getObject("W"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v33", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l1", results.getObject("L"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("k1", results.getObject("K"));
		assertEquals("u1", results.getObject("U"));
		assertEquals("w1", results.getObject("W"));
		assertEquals("d4", results.getObject("D"));
		assertEquals("t4", results.getObject("T"));
		assertEquals("v41", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l2", results.getObject("L"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("k2", results.getObject("K"));
		assertEquals("u2", results.getObject("U"));
		assertEquals("w2", results.getObject("W"));
		assertEquals("d4", results.getObject("D"));
		assertEquals("t4", results.getObject("T"));
		assertEquals("v42", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l3", results.getObject("L"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("k3", results.getObject("K"));
		assertEquals("u3", results.getObject("U"));
		assertEquals("w3", results.getObject("W"));
		assertEquals("d4", results.getObject("D"));
		assertEquals("t4", results.getObject("T"));
		assertEquals("v43", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l1", results.getObject("L"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("k1", results.getObject("K"));
		assertEquals("u1", results.getObject("U"));
		assertEquals("w1", results.getObject("W"));
		assertEquals("d5", results.getObject("D"));
		assertEquals("t5", results.getObject("T"));
		assertEquals("v51", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l2", results.getObject("L"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("k2", results.getObject("K"));
		assertEquals("u2", results.getObject("U"));
		assertEquals("w2", results.getObject("W"));
		assertEquals("d5", results.getObject("D"));
		assertEquals("t5", results.getObject("T"));
		assertEquals("v52", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("l3", results.getObject("L"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("k3", results.getObject("K"));
		assertEquals("u3", results.getObject("U"));
		assertEquals("w3", results.getObject("W"));
		assertEquals("d5", results.getObject("D"));
		assertEquals("t5", results.getObject("T"));
		assertEquals("v53", results.getObject("V"));

		assertFalse(results.next());
	}

	public void testHeaderLooksLikeHeader() throws SQLException {
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("headerline", "P,D,T,V");
		
		// setting both transposedLines and skipTransposedFields informs the
		// driver we are receiving a compacted join
		
		// transposedLines <- 1; leaving D,T as regular fields, V as matrix of
		// joined values
		props.put("transposedLines", "1");
		
		// transposedFieldsToSkip <- 2; the file looks like a regular CSV (with
		// variable header)
		props.put("transposedFieldsToSkip", "2");
		// the driver must be told that there is no header.
		props.put("suppressHeaders", "true");

		ResultSet results = null;

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();

		results = stmt.executeQuery("SELECT * FROM twojoinedtables02");
		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v11", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v12", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p3", results.getObject("P"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v13", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p4", results.getObject("P"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v14", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p5", results.getObject("P"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v15", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v21", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v22", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p3", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v23", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p4", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v24", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p5", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v25", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v31", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v32", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p3", results.getObject("P"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v33", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p4", results.getObject("P"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v34", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p5", results.getObject("P"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v35", results.getObject("V"));

		assertFalse(results.next());
	}

	public void testHeaderLooksLikeHeaderIndexed() throws SQLException {
		Properties props = new Properties();
		props.put("fileExtension", ".txt");
		props.put("headerline", "P,J,D,T,V");
		
		// setting both transposedLines and skipTransposedFields informs the
		// driver we are receiving a compacted join
		
		// transposedLines <- 1; leaving D,T as regular fields, V as matrix of
		// joined values
		props.put("transposedLines", "1");
		
		// transposedFieldsToSkip <- 2; the file looks like a regular CSV (with
		// variable header)
		props.put("transposedFieldsToSkip", "3");
		// the driver must be told that there is no header.
		props.put("suppressHeaders", "true");
		
		props.put("indexedFiles", "True");
		props.put("fileTailPattern", "_(.*)");
		props.put("fileTailParts", "junk");
		props.put("fileTailPrepend", "true");

		ResultSet results = null;

		Connection conn = DriverManager.getConnection("jdbc:relique:csv:"
				+ filePath, props);
		Statement stmt = conn.createStatement();

		results = stmt.executeQuery("SELECT * FROM twojoinedtablesindex");
		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v11", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v12", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p3", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d1", results.getObject("D"));
		assertEquals("t1", results.getObject("T"));
		assertEquals("v13", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("2", results.getObject("J"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v21", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("2", results.getObject("J"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v22", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("2", results.getObject("J"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v23", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v31", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v32", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p3", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v33", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("1", results.getObject("J"));
		assertEquals("d1", results.getObject("D")); // TODO (here I get a 'D')
		//assertEquals("t1", results.getObject("T"));
		//assertEquals("v11", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("1", results.getObject("J"));
		//assertEquals("d1", results.getObject("D"));
		//assertEquals("t1", results.getObject("T"));
		//assertEquals("v12", results.getObject("V"));

		assertTrue(results.next()); // TODO (here it crashes)
		//assertEquals("p3", results.getObject("P"));
		//assertEquals("1", results.getObject("J"));
		//assertEquals("d1", results.getObject("D"));
		//assertEquals("t1", results.getObject("T"));
		//assertEquals("v13", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("1", results.getObject("J"));
		assertEquals("p1", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v21", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("2", results.getObject("J"));
		assertEquals("p2", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v22", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("2", results.getObject("J"));
		assertEquals("p3", results.getObject("P"));
		assertEquals("d2", results.getObject("D"));
		assertEquals("t2", results.getObject("T"));
		assertEquals("v23", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p1", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v31", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p2", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v32", results.getObject("V"));

		assertTrue(results.next());
		assertEquals("p3", results.getObject("P"));
		assertEquals("2", results.getObject("J"));
		assertEquals("d3", results.getObject("D"));
		assertEquals("t3", results.getObject("T"));
		assertEquals("v33", results.getObject("V"));

		assertFalse(results.next());
	}

}
