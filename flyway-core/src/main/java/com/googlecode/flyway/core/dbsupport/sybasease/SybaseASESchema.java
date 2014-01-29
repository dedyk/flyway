package com.googlecode.flyway.core.dbsupport.sybasease;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.flyway.core.dbsupport.DbSupport;
import com.googlecode.flyway.core.dbsupport.JdbcTemplate;
import com.googlecode.flyway.core.dbsupport.Schema;
import com.googlecode.flyway.core.dbsupport.Table;

public class SybaseASESchema extends Schema {
	
    /**
     * Creates a new SybaseASESchema schema.
     *
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param dbSupport    The database-specific support.
     * @param name         The name of the schema.
     */
    public SybaseASESchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

	@Override
	protected boolean doExists() throws SQLException {
		return jdbcTemplate.queryForInt("select count(*) from master..sysdatabases where name = ?", name) > 0;
	}

	@Override
	protected boolean doEmpty() throws SQLException {
		
		int objectCount = jdbcTemplate.queryForInt(
				"select count(*) from " + name + "..sysobjects where type in ('U', 'V', 'RI', 'TR')");
		
		return objectCount == 0;
	}

	@Override
	protected void doCreate() throws SQLException {
		jdbcTemplate.execute("create database " + name);
	}

	@Override
	protected void doDrop() throws SQLException {
		jdbcTemplate.execute("drop database " + name);
	}

	@Override
	protected void doClean() throws SQLException {
		
		jdbcTemplate.execute("use " + name);
		
		List<String> allViews = getAllViews();
		
		for (String view : allViews) {
			jdbcTemplate.execute("drop view " + view);
		}
		
		Table[] allTables = doAllTables();
		
		for (Table table : allTables) {
			jdbcTemplate.execute("drop table " + table.getName());
		}
	}

	@Override
	protected Table[] doAllTables() throws SQLException {
		
        List<String> tableNames = jdbcTemplate.queryForStringList(
        		"select name from " + name + "..sysobjects where type = 'U'");
        
        Table[] tables = new Table[tableNames.size()];
        
        for (int i = 0; i < tableNames.size(); i++) {
            tables[i] = new SybaseASETable(jdbcTemplate, dbSupport, this, tableNames.get(i));
        }
        
        return tables;
	}
	
	private List<String> getAllViews() throws SQLException {
		return jdbcTemplate.queryForStringList(
        		"select name from " + name + "..sysobjects where type = 'V'");
	}

	@Override
	public Table getTable(String tableName) {
		return new SybaseASETable(jdbcTemplate, dbSupport, this, tableName);
	}
}
