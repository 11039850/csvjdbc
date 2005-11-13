/*
 *  CsvJdbc - a JDBC driver for CSV files
 *  Copyright (C) 2001  Jonathan Ackerman
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.relique.jdbc.csv;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * This class implements the Connection interface for the CsvJdbc driver.
 *
 * @author     Jonathan Ackerman
 * @author     Sander Brienen
 * @author     Michael Maraya
 * @author     Tomasz Skutnik
 * @author     Christoph Langer
 * @version    $Id: CsvConnection.java,v 1.14 2005/11/13 18:32:58 jackerm Exp $
 */
public class CsvConnection implements Connection {

    /** Directory where the CSV files to use are located */
    private String path;

    /** File extension to use */
    private String extension = CsvDriver.DEFAULT_EXTENSION;

    /** Field separator to use */
    private char separator = CsvDriver.DEFAULT_SEPARATOR;

    /** Field quotechar to use */
    private char quotechar = CsvDriver.DEFAULT_QUOTECHAR;

    /** Field headerline to use */
    private String headerline = CsvDriver.DEFAULT_HEADERLINE; 

    /** Should headers be suppressed */
    private boolean suppressHeaders = CsvDriver.DEFAULT_SUPPRESS;
    
    /** Should headers be trimmed */
    private boolean trimHeaders = CsvDriver.DEFAULT_TRIM_HEADERS;

    /** Collection of all created Statements */
    private Vector statements = new Vector();

    /** Charset that should be used to read the files */
    private String charset = null;

    /** Stores whether this Connection is closed or not */
    private boolean closed;

    /**
     * Creates a new CsvConnection that takes the supplied path
     * @param path directory where the CSV files are located
     */
    protected CsvConnection(String path) {
        // validate argument(s)
        if(path == null || path.length() == 0) {
            throw new IllegalArgumentException(
                    "'path' argument may not be empty or null");
        }
        this.path = path;
    }

    /**
     * Creates a new CsvConnection that takes the supplied path and properties
     * @param path directory where the CSV files are located
     * @param info set of properties containing custom options
     */
    protected CsvConnection(String path, Properties info) {
        this(path);
        // check for properties
        if(info != null) {
            // set the file extension to be used
            if(info.getProperty(CsvDriver.FILE_EXTENSION) != null) {
                extension = info.getProperty(CsvDriver.FILE_EXTENSION);
            }
            // set the separator character to be used
            if(info.getProperty(CsvDriver.SEPARATOR) != null) {
                separator = info.getProperty(CsvDriver.SEPARATOR).charAt(0);
            }
            // set the quotechar character to be used
            if(info.getProperty(CsvDriver.QUOTECHAR) != null) {
                quotechar = info.getProperty(CsvDriver.QUOTECHAR).charAt(0);
            }
            // set the headerline to be used
            if(info.getProperty(CsvDriver.HEADERLINE) != null) {
                 headerline = info.getProperty(CsvDriver.HEADERLINE);
            }
            // set the header suppression flag
            if(info.getProperty(CsvDriver.SUPPRESS_HEADERS) != null) {
                suppressHeaders = Boolean.valueOf(info.getProperty(
                        CsvDriver.SUPPRESS_HEADERS)).booleanValue();
            }
            // default charset
            if (info.getProperty(CsvDriver.CHARSET) != null) {
                charset = info.getProperty(CsvDriver.CHARSET);
            }
        }
    }

    /**
     * Creates a <code>Statement</code> object for sending
     * SQL statements to the database.
     * SQL statements without parameters are normally
     * executed using <code>Statement</code> objects. If the same SQL statement
     * is executed many times, it may be more efficient to use a
     * <code>PreparedStatement</code> object.
     * <P>
     * Result sets created using the returned <code>Statement</code>
     * object will by default be type <code>TYPE_FORWARD_ONLY</code>
     * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
     *
     * @return a new default <code>Statement</code> object
     * @exception SQLException if a database access error occurs
     */
    public Statement createStatement() throws SQLException {
        CsvStatement statement = new CsvStatement(this, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE);
        statements.add(statement);
        return statement;
    }

    /**
     * Creates a <code>PreparedStatement</code> object for sending
     * parameterized SQL statements to the database.
     * <P>
     * A SQL statement with or without IN parameters can be
     * pre-compiled and stored in a <code>PreparedStatement</code> object. This
     * object can then be used to efficiently execute this statement
     * multiple times.
     *
     * <P><B>Note:</B> This method is optimized for handling
     * parametric SQL statements that benefit from precompilation. If
     * the driver supports precompilation,
     * the method <code>prepareStatement</code> will send
     * the statement to the database for precompilation. Some drivers
     * may not support precompilation. In this case, the statement may
     * not be sent to the database until the <code>PreparedStatement</code>
     * object is executed.  This has no direct effect on users; however, it does
     * affect which methods throw certain <code>SQLException</code> objects.
     * <P>
     * Result sets created using the returned <code>PreparedStatement</code>
     * object will by default be type <code>TYPE_FORWARD_ONLY</code>
     * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
     *
     * @param sql an SQL statement that may contain one or more '?' IN
     * parameter placeholders
     * @return a new default <code>PreparedStatement</code> object containing the
     * pre-compiled SQL statement
     * @exception SQLException if a database access error occurs
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
      throw new UnsupportedOperationException(
              "Connection.prepareStatement(String) unsupported");
    }

    /**
     * Creates a <code>CallableStatement</code> object for calling
     * database stored procedures.
     * The <code>CallableStatement</code> object provides
     * methods for setting up its IN and OUT parameters, and
     * methods for executing the call to a stored procedure.
     *
     * <P><B>Note:</B> This method is optimized for handling stored
     * procedure call statements. Some drivers may send the call
     * statement to the database when the method <code>prepareCall</code>
     * is done; others
     * may wait until the <code>CallableStatement</code> object
     * is executed. This has no
     * direct effect on users; however, it does affect which method
     * throws certain SQLExceptions.
     * <P>
     * Result sets created using the returned <code>CallableStatement</code>
     * object will by default be type <code>TYPE_FORWARD_ONLY</code>
     * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
     *
     * @param sql an SQL statement that may contain one or more '?'
     * parameter placeholders. Typically this  statement is a JDBC
     * function call escape string.
     * @return a new default <code>CallableStatement</code> object containing the
     * pre-compiled SQL statement
     * @exception SQLException if a database access error occurs
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.prepareCall(String) unsupported");
    }

    /**
     * Converts the given SQL statement into the system's native SQL grammar.
     * A driver may convert the JDBC SQL grammar into its system's
     * native SQL grammar prior to sending it. This method returns the
     * native form of the statement that the driver would have sent.
     *
     * @param sql an SQL statement that may contain one or more '?'
     * parameter placeholders
     * @return the native form of this statement
     * @exception SQLException if a database access error occurs
     */
    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.nativeSQL(String) unsupported");
    }

    /**
     * Sets this connection's auto-commit mode to the given state.
     * If a connection is in auto-commit mode, then all its SQL
     * statements will be executed and committed as individual
     * transactions.  Otherwise, its SQL statements are grouped into
     * transactions that are terminated by a call to either
     * the method <code>commit</code> or the method <code>rollback</code>.
     * By default, new connections are in auto-commit
     * mode.
     * <P>
     * The commit occurs when the statement completes or the next
     * execute occurs, whichever comes first. In the case of
     * statements returning a <code>ResultSet</code> object,
     * the statement completes when the last row of the
     * <code>ResultSet</code> object has been retrieved or the
     * <code>ResultSet</code> object has been closed. In advanced cases, a
     * single statement may return multiple results as well as output
     * parameter values. In these cases, the commit occurs when all results and
     * output parameter values have been retrieved.
     * <P>
     * <B>NOTE:</B>  If this method is called during a transaction, the
     * transaction is committed.
     *
     * @param autoCommit <code>true</code> to enable auto-commit mode;
     *         <code>false</code> to disable it
     * @exception SQLException if a database access error occurs
     * @see #getAutoCommit
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.setAutoCommit(boolean) unsupported");
    }

    /**
     * Retrieves the current auto-commit mode for this <code>Connection</code>
     * object.
     *
     * @return the current state of this <code>Connection</code> object's
     *         auto-commit mode
     * @exception SQLException if a database access error occurs
     * @see #setAutoCommit
     */
    public boolean getAutoCommit() throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.getAutoCommit() unsupported");
    }

    /**
     * Makes all changes made since the previous
     * commit/rollback permanent and releases any database locks
     * currently held by this <code>Connection</code> object.
     * This method should be
     * used only when auto-commit mode has been disabled.
     *
     * @exception SQLException if a database access error occurs or this
     *            <code>Connection</code> object is in auto-commit mode
     * @see #setAutoCommit
     */
    public void commit() throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.commit() unsupported");
    }

    /**
     * Undoes all changes made in the current transaction
     * and releases any database locks currently held
     * by this <code>Connection</code> object. This method should be
     * used only when auto-commit mode has been disabled.
     *
     * @exception SQLException if a database access error occurs or this
     *            <code>Connection</code> object is in auto-commit mode
     * @see #setAutoCommit
     */
    public void rollback() throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.rollback() unsupported");
    }

    /**
     * Releases this <code>Connection</code> object's database and JDBC
     * resources immediately instead of waiting for them to be automatically
     * released.
     * <P>
     * Calling the method <code>close</code> on a <code>Connection</code>
     * object that is already closed is a no-op.
     * <P>
     * <B>Note:</B> A <code>Connection</code> object is automatically
     * closed when it is garbage collected. Certain fatal errors also
     * close a <code>Connection</code> object.
     *
     * @exception SQLException if a database access error occurs
     */
    public void close() throws SQLException {
        // close all created statements
        for(Enumeration i = statements.elements(); i.hasMoreElements(); ) {
            CsvStatement statement = (CsvStatement)i.nextElement();
            statement.close();
        }
        // set this Connection as closed
        closed = true;
    }

    /**
     * Retrieves whether this <code>Connection</code> object has been
     * closed.  A connection is closed if the method <code>close</code>
     * has been called on it or if certain fatal errors have occurred.
     * This method is guaranteed to return <code>true</code> only when
     * it is called after the method <code>Connection.close</code> has
     * been called.
     * <P>
     * This method generally cannot be called to determine whether a
     * connection to a database is valid or invalid.  A typical client
     * can determine that a connection is invalid by catching any
     * exceptions that might be thrown when an operation is attempted.
     *
     * @return <code>true</code> if this <code>Connection</code> object
     *         is closed; <code>false</code> if it is still open
     * @exception SQLException if a database access error occurs
     */
    public boolean isClosed() throws SQLException {
      return closed;
    }

    /**
     * Retrieves a <code>DatabaseMetaData</code> object that contains
     * metadata about the database to which this
     * <code>Connection</code> object represents a connection.
     * The metadata includes information about the database's
     * tables, its supported SQL grammar, its stored
     * procedures, the capabilities of this connection, and so on.
     *
     * @return a <code>DatabaseMetaData</code> object for this
     *         <code>Connection</code> object
     * @exception SQLException if a database access error occurs
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return new CsvDatabaseMetaData(this);
    }

    /**
     * Puts this connection in read-only mode as a hint to the driver to enable
     * database optimizations.
     *
     * <P><B>Note:</B> This method cannot be called during a transaction.
     *
     * @param readOnly <code>true</code> enables read-only mode;
     *        <code>false</code> disables it
     * @exception SQLException if a database access error occurs or this
     *            method is called during a transaction
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.setReadOnly(boolean) unsupported");
    }

    /**
     * Retrieves whether this <code>Connection</code>
     * object is in read-only mode.
     *
     * @return <code>true</code> if this <code>Connection</code> object
     *         is read-only; <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    /**
     * Sets the given catalog name in order to select
     * a subspace of this <code>Connection</code> object's database
     * in which to work.
     * <P>
     * If the driver does not support catalogs, it will
     * silently ignore this request.
     *
     * @param catalog the name of a catalog (subspace in this
     *        <code>Connection</code> object's database) in which to work
     * @exception SQLException if a database access error occurs
     * @see #getCatalog
     */
    public void setCatalog(String catalog) throws SQLException {
        // silently ignore this request
    }

    /**
     * Retrieves this <code>Connection</code> object's current catalog name.
     *
     * @return the current catalog name or <code>null</code> if there is none
     * @exception SQLException if a database access error occurs
     * @see #setCatalog
     */
    public String getCatalog() throws SQLException {
        return null;
    }

    /**
     * Attempts to change the transaction isolation level for this
     * <code>Connection</code> object to the one given.
     * The constants defined in the interface <code>Connection</code>
     * are the possible transaction isolation levels.
     * <P>
     * <B>Note:</B> If this method is called during a transaction, the result
     * is implementation-defined.
     *
     * @param level one of the following <code>Connection</code> constants:
     *        <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
     *        <code>Connection.TRANSACTION_READ_COMMITTED</code>,
     *        <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
     *        <code>Connection.TRANSACTION_SERIALIZABLE</code>.
     *        (Note that <code>Connection.TRANSACTION_NONE</code> cannot be used
     *        because it specifies that transactions are not supported.)
     * @exception SQLException if a database access error occurs
     *            or the given parameter is not one of the <code>Connection</code>
     *            constants
     * @see DatabaseMetaData#supportsTransactionIsolationLevel
     * @see #getTransactionIsolation
     */
    public void setTransactionIsolation(int level) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.setTransactionIsolation(int) unsupported");
    }

    /**
     * Retrieves this <code>Connection</code> object's current
     * transaction isolation level.
     *
     * @return the current transaction isolation level, which will be one
     *         of the following constants:
     *        <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
     *        <code>Connection.TRANSACTION_READ_COMMITTED</code>,
     *        <code>Connection.TRANSACTION_REPEATABLE_READ</code>,
     *        <code>Connection.TRANSACTION_SERIALIZABLE</code>, or
     *        <code>Connection.TRANSACTION_NONE</code>.
     * @exception SQLException if a database access error occurs
     * @see #setTransactionIsolation
     */
    public int getTransactionIsolation() throws SQLException {
      return Connection.TRANSACTION_NONE;
    }

    /**
     * Retrieves the first warning reported by calls on this
     * <code>Connection</code> object.  If there is more than one
     * warning, subsequent warnings will be chained to the first one
     * and can be retrieved by calling the method
     * <code>SQLWarning.getNextWarning</code> on the warning
     * that was retrieved previously.
     * <P>
     * This method may not be
     * called on a closed connection; doing so will cause an
     * <code>SQLException</code> to be thrown.
     *
     * <P><B>Note:</B> Subsequent warnings will be chained to this
     * SQLWarning.
     *
     * @return the first <code>SQLWarning</code> object or <code>null</code>
     *         if there are none
     * @exception SQLException if a database access error occurs or
     *            this method is called on a closed connection
     * @see SQLWarning
     */
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.getWarnings() unsupported");
    }

    /**
     * Clears all warnings reported for this <code>Connection</code> object.
     * After a call to this method, the method <code>getWarnings</code>
     * returns <code>null</code> until a new warning is
     * reported for this <code>Connection</code> object.
     *
     * @exception SQLException if a database access error occurs
     */
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.getWarnings() unsupported");
    }

    //--------------------------JDBC 2.0-----------------------------

    /**
     * Creates a <code>Statement</code> object that will generate
     * <code>ResultSet</code> objects with the given type and concurrency.
     * This method is the same as the <code>createStatement</code> method
     * above, but it allows the default result set
     * type and concurrency to be overridden.
     * Now also supports <code>ResultSet.TYPE_SCROLL_SENSITIVE</code> 
     *
     * @param resultSetType a result set type; one of
     *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
     *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency a concurrency type; one of
     *        <code>ResultSet.CONCUR_READ_ONLY</code> or
     *        <code>ResultSet.CONCUR_UPDATABLE</code>
     * @return a new <code>Statement</code> object that will generate
     *         <code>ResultSet</code> objects with the given type and
     *         concurrency
     * @exception SQLException if a database access error occurs
     *         or the given parameters are not <code>ResultSet</code>
     *         constants indicating type and concurrency
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        CsvStatement statement = new CsvStatement(this, resultSetType);
        statements.add(statement);
        return statement;
    }

    /**
     * Creates a <code>PreparedStatement</code> object that will generate
     * <code>ResultSet</code> objects with the given type and concurrency.
     * This method is the same as the <code>prepareStatement</code> method
     * above, but it allows the default result set
     * type and concurrency to be overridden.
     *
     * @param sql a <code>String</code> object that is the SQL statement to
     *            be sent to the database; may contain one or more ? IN
     *            parameters
     * @param resultSetType a result set type; one of
     *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
     *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency a concurrency type; one of
     *         <code>ResultSet.CONCUR_READ_ONLY</code> or
     *         <code>ResultSet.CONCUR_UPDATABLE</code>
     * @return a new PreparedStatement object containing the
     * pre-compiled SQL statement that will produce <code>ResultSet</code>
     * objects with the given type and concurrency
     * @exception SQLException if a database access error occurs
     *         or the given parameters are not <code>ResultSet</code>
     *         constants indicating type and concurrency
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.prepareStatement(String, int, int) unsupported");
    }

    /**
     * Creates a <code>CallableStatement</code> object that will generate
     * <code>ResultSet</code> objects with the given type and concurrency.
     * This method is the same as the <code>prepareCall</code> method
     * above, but it allows the default result set
     * type and concurrency to be overridden.
     *
     * @param sql a <code>String</code> object that is the SQL statement to
     *            be sent to the database; may contain on or more ? parameters
     * @param resultSetType a result set type; one of
     *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
     *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency a concurrency type; one of
     *         <code>ResultSet.CONCUR_READ_ONLY</code> or
     *         <code>ResultSet.CONCUR_UPDATABLE</code>
     * @return a new <code>CallableStatement</code> object containing the
     * pre-compiled SQL statement that will produce <code>ResultSet</code>
     * objects with the given type and concurrency
     * @exception SQLException if a database access error occurs
     *         or the given parameters are not <code>ResultSet</code>
     *         constants indicating type and concurrency
     */
    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.prepareCall(String, int, int) unsupported");
    }

    /**
     * Retrieves the <code>Map</code> object associated with this
     * <code>Connection</code> object.
     * Unless the application has added an entry, the type map returned
     * will be empty.
     *
     * @return the <code>java.util.Map</code> object associated
     *         with this <code>Connection</code> object
     * @exception SQLException if a database access error occurs
     * @see #setTypeMap
     */
    public Map getTypeMap() throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.getTypeMap() unsupported");
    }

    /**
     * Installs the given <code>TypeMap</code> object as the type map for
     * this <code>Connection</code> object.  The type map will be used for the
     * custom mapping of SQL structured types and distinct types.
     *
     * @param map the <code>java.util.Map</code> object to install
     *        as the replacement for this <code>Connection</code>
     *        object's default type map
     * @exception SQLException if a database access error occurs or
     *        the given parameter is not a <code>java.util.Map</code>
     *        object
     * @see #getTypeMap
     */
    public void setTypeMap(Map map) throws SQLException {
        throw new UnsupportedOperationException(
                "Connection.setTypeMap(Map) unsupported");
    }

    //--------------------------JDBC 3.0-----------------------------
    /**
     * Changes the holdability of <code>ResultSet</code> objects
     * created using this <code>Connection</code> object to the given
     * holdability.
     *
     * @param holdability a <code>ResultSet</code> holdability constant; one of
     *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @throws SQLException if a database access occurs, the given parameter
     *         is not a <code>ResultSet</code> constant indicating holdability,
     *         or the given holdability is not supported
     * @since 1.4
     * @see #getHoldability
     * @see java.sql.ResultSet
     */
    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException("Connection.setHoldability(int) unsupported");
    }

    /**
     * Retrieves the current holdability of ResultSet objects created
     * using this Connection object.
     *
     * @return the holdability, one of <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     * <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @throws SQLException if a database access occurs
     * @since 1.4
     * @see #setHoldability
     * @see java.sql.ResultSet
     */
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Connection.getHoldability() unsupported");
    }

    /* Removed since this only builds under JDK 1.4
    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException("Connection.setSavepoint() unsupported");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException("Connection.setSavepoint(String) unsupported");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("Connection.rollback(Savepoint) unsupported");
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("Connection.releaseSavepoint(Savepoint) unsupported");
    }
     */

    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency,
                                     int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Connection.createStatement(int,int,int) unsupported");
    }

    public PreparedStatement prepareStatement(String sql,
                                      int resultSetType,
                                      int resultSetConcurrency,
                                      int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Connection.prepareStatement(String,int,int,int) unsupported");
    }

    public CallableStatement prepareCall(String sql,
                                         int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Connection.prepareCall(String,int,int,int) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("Connection.prepareStatement(String,int) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("Connection.prepareStatement(String,int[]) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("Connection.prepareStatement(String,String[]) unsupported");
    }
    
		public void releaseSavepoint(Savepoint savePoint) throws SQLException{
			throw new UnsupportedOperationException("Connection.releaseSavepoint(Savepoint) unsupported");
		}
		
		public void rollback(Savepoint savePoint) throws SQLException{
			throw new UnsupportedOperationException("Connection.rollback(Savepoint) unsupported");
		}
		
		public Savepoint setSavepoint() throws SQLException{
			throw new UnsupportedOperationException("Connection.setSavepoint() unsupported");
		}
		
		public Savepoint setSavepoint(String str) throws SQLException{
			throw new UnsupportedOperationException("Connection.setSavepoint(String) unsupported");
		} 

    //---------------------------------------------------------------------
    // Properties
    //---------------------------------------------------------------------

    /**
     * Accessor method for the path property
     * @return current value for the path property
     */
    protected String getPath() {
        return path;
    }

    /**
     * Accessor method for the extension property
     * @return current value for the extension property
     */
    protected String getExtension() {
        return extension;
    }

    /**
     * Accessor method for the separator property
     * @return current value for the separator property
     */
    protected char getSeperator() {
        return separator;
    }

    /**
     * Accessor method for the headerline property
     * @return current value for the headerline property
     */
    public String getHeaderline() {
        return headerline;
    }

    /**
     * Accessor method for the quotechar property
     * @return current value for the quotechar property
     */
    public char getQuotechar() {
        return quotechar;
    }
    
    /**
     * Accessor method for the suppressHeaders property
     * @return current value for the suppressHeaders property
     */
    protected boolean isSuppressHeaders() {
        return suppressHeaders;
    }

    /**
     * Accessor method for the charset property
     * @return current value for the suppressHeaders property
     */
    protected String getCharset() {
        return charset;
    }
    
    /**
     * Accessor method for the trimHeaders property
     * @return current value for the trimHeaders property
     */
    public boolean getTrimHeaders() {
        return trimHeaders;
    }
}
