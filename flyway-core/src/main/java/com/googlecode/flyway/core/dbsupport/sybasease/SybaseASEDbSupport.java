package com.googlecode.flyway.core.dbsupport.sybasease;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.googlecode.flyway.core.dbsupport.DbSupport;
import com.googlecode.flyway.core.dbsupport.JdbcTemplate;
import com.googlecode.flyway.core.dbsupport.Schema;
import com.googlecode.flyway.core.dbsupport.SqlStatementBuilder;
import com.googlecode.flyway.core.util.logging.Log;
import com.googlecode.flyway.core.util.logging.LogFactory;

public class SybaseASEDbSupport extends DbSupport {
	
	private static final Log logger = LogFactory.getLog(SybaseASEDbSupport.class);

    /**
     * Creates a new instance.
     *
     * @param connection The connection to use.
     */
    public SybaseASEDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, Types.VARCHAR));
    }
	
	@Override
	public Schema getSchema(String name) {
		
		logger.info(" Please run: *** master..sp_dboption " + name + ", 'ddl in tran', true *** if 'ddl in tran' parameter is set to false");
		
		return new SybaseASESchema(jdbcTemplate, this, name);
	}

	@Override
	public SqlStatementBuilder createSqlStatementBuilder() {
		return new SybaseASESqlStatementBuilder();
	}

	@Override
	public String getScriptLocation() {
		return "com/googlecode/flyway/core/dbsupport/sybasease/";
	}

	@Override
	protected String doGetCurrentSchema() throws SQLException {
		return jdbcTemplate.getConnection().getCatalog();
	}

	@Override
	protected void doSetCurrentSchema(Schema schema) throws SQLException {
		jdbcTemplate.execute("USE " + schema.getName());
	}

	@Override
	public String getCurrentUserFunction() {
		return "user_name()";
	}

	@Override
	public boolean supportsDdlTransactions() {
		return true;
	}

	@Override
	public String getBooleanTrue() {
		return "1";
	}

	@Override
	public String getBooleanFalse() {
		return "0";
	}
	
	@Override
	protected String doQuote(String identifier) {
		return identifier;
	}

	@Override
	public boolean catalogIsSchema() {
		return true;
	}
}
