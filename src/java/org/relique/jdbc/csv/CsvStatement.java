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

import java.sql.*;
import java.io.File;

/**
 * This class implements the Statement interface for the CsvJdbc driver.
 *
 * @author     Jonathan Ackerman
 * @author     Sander Brienen
 * @created    25 November 2001
 * @version    $Id: CsvStatement.java,v 1.2 2001/12/01 22:35:13 jackerm Exp $
 */

public class CsvStatement implements Statement
{
  private CsvConnection connection;


  /**
   *Constructor for the CsvStatement object
   *
   * @param  connection  Description of Parameter
   * @since
   */
  protected CsvStatement(CsvConnection connection)
  {
    DriverManager.println("CsvJdbc - CsvStatement() - connection=" + connection);
    this.connection = connection;
  }


  /**
   *Sets the maxFieldSize attribute of the CsvStatement object
   *
   * @param  p0                The new maxFieldSize value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setMaxFieldSize(int p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Sets the maxRows attribute of the CsvStatement object
   *
   * @param  p0                The new maxRows value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setMaxRows(int p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Sets the escapeProcessing attribute of the CsvStatement object
   *
   * @param  p0                The new escapeProcessing value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setEscapeProcessing(boolean p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Sets the queryTimeout attribute of the CsvStatement object
   *
   * @param  p0                The new queryTimeout value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setQueryTimeout(int p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Sets the cursorName attribute of the CsvStatement object
   *
   * @param  p0                The new cursorName value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setCursorName(String p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Sets the fetchDirection attribute of the CsvStatement object
   *
   * @param  p0                The new fetchDirection value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setFetchDirection(int p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Sets the fetchSize attribute of the CsvStatement object
   *
   * @param  p0                The new fetchSize value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void setFetchSize(int p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the maxFieldSize attribute of the CsvStatement object
   *
   * @return                   The maxFieldSize value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getMaxFieldSize() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the maxRows attribute of the CsvStatement object
   *
   * @return                   The maxRows value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getMaxRows() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the queryTimeout attribute of the CsvStatement object
   *
   * @return                   The queryTimeout value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getQueryTimeout() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the warnings attribute of the CsvStatement object
   *
   * @return                   The warnings value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public SQLWarning getWarnings() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the resultSet attribute of the CsvStatement object
   *
   * @return                   The resultSet value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public ResultSet getResultSet() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the updateCount attribute of the CsvStatement object
   *
   * @return                   The updateCount value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getUpdateCount() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the moreResults attribute of the CsvStatement object
   *
   * @return                   The moreResults value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public boolean getMoreResults() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the fetchDirection attribute of the CsvStatement object
   *
   * @return                   The fetchDirection value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getFetchDirection() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the fetchSize attribute of the CsvStatement object
   *
   * @return                   The fetchSize value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getFetchSize() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the resultSetConcurrency attribute of the CsvStatement object
   *
   * @return                   The resultSetConcurrency value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getResultSetConcurrency() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the resultSetType attribute of the CsvStatement object
   *
   * @return                   The resultSetType value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int getResultSetType() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Gets the connection attribute of the CsvStatement object
   *
   * @return                   The connection value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public Connection getConnection() throws SQLException
  {
    return connection;
  }


  /**
   *Description of the Method
   *
   * @param  sql               Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public ResultSet executeQuery(String sql) throws SQLException
  {
    DriverManager.println("CsvJdbc - CsvStatement:executeQuery() - sql= " + sql);
    SqlParser parser = new SqlParser();
    try
    {
      parser.parse(sql);
    }
    catch (Exception e)
    {
      throw new SQLException("Syntax Error. " + e.getMessage());
    }
    String fileName = connection.getFilePath() + parser.getTableName() + connection.getExtension();
    File checkFile = new File(fileName);
    if (!checkFile.exists())
    {
      throw new SQLException("Cannot open data file '" + fileName + "'  !");
    }
    if (!checkFile.canRead())
    {
      throw new SQLException("Data file '" + fileName + "'  not readable !");
    }
    CsvReader reader;
    try
    {
      reader = new CsvReader(fileName, connection.getSeperator(), connection.isSuppressHeaders());
    }
    catch (Exception e)
    {
      throw new SQLException("Error reading data file. Message was: " + e);
    }
    return new CsvResultSet(this, reader, parser.getColumnNames());
  }


  /**
   *Description of the Method
   *
   * @param  sql               Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int executeUpdate(String sql) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Description of the Method
   *
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void close() throws SQLException { }


  /**
   *Description of the Method
   *
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void cancel() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Description of the Method
   *
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void clearWarnings() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Description of the Method
   *
   * @param  p0                Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public boolean execute(String p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Adds a feature to the Batch attribute of the CsvStatement object
   *
   * @param  p0                The feature to be added to the Batch attribute
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void addBatch(String p0) throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Description of the Method
   *
   * @exception  SQLException  Description of Exception
   * @since
   */
  public void clearBatch() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }


  /**
   *Description of the Method
   *
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public int[] executeBatch() throws SQLException
  {
    throw new SQLException("Not Supported !");
  }
}

