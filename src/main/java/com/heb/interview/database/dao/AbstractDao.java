/*
 * Copyright (C) GM Global Technology Operations LLC. All rights reserved.
 * This information is confidential and proprietary to GM Global Technology
 * Operations LLC and may not be used, modified, copied or distributed.
 */

package com.heb.interview.database.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

public abstract class AbstractDao {

	private transient DataSource dataSource;
	private transient JdbcTemplate jdbcTemplate;
	private transient NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static final int TIMEOUT = 0;
	private static final boolean IS_CASE_SENSITIVE = false;

	@Autowired
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public JdbcTemplate getJdbcTemplate() {
		if (this.jdbcTemplate == null) {
			this.jdbcTemplate = new JdbcTemplate(this.dataSource);
			this.jdbcTemplate.setQueryTimeout(TIMEOUT);
			this.jdbcTemplate.setResultsMapCaseInsensitive(IS_CASE_SENSITIVE);
		}

		return this.jdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		if (this.namedParameterJdbcTemplate == null) {
			this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
		}
		return this.namedParameterJdbcTemplate;
	}
}