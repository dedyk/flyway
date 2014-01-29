package com.googlecode.flyway.core.dbsupport.sybasease;

import java.sql.SQLException;

import com.googlecode.flyway.core.dbsupport.DbSupport;
import com.googlecode.flyway.core.dbsupport.JdbcTemplate;
import com.googlecode.flyway.core.dbsupport.Schema;
import com.googlecode.flyway.core.dbsupport.Table;

public class SybaseASETable extends Table {
	
    /**
     * Creates a new SybaseASETable table.
     *
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param dbSupport    The database-specific support.
     * @param schema       The schema this table lives in.
     * @param name         The name of the table.
     */
    public SybaseASETable(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }

	@Override
	protected boolean doExists() throws SQLException {
		return exists(schema, null, name);
	}

	@Override
	protected boolean doExistsNoQuotes() throws SQLException {
		return exists(schema, null, name);
	}

	@Override
	protected void doLock() throws SQLException {
		dbSupport.setCurrentSchema(schema);
		jdbcTemplate.execute("lock table " + name + " in exclusive mode");
	}

	@Override
	protected void doDrop() throws SQLException {
		jdbcTemplate.execute("drop table " + schema.getName() + ".." + name);
	}
	
	@Override
    public String toString() {
		return schema.getName() + ".." + name;
	}
}
