/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gigaspaces.metrics.hsqldb;

import com.gigaspaces.metrics.MetricReporter;
import com.gigaspaces.metrics.MetricReporterFactory;

import java.util.Properties;

/**
 * @author Evgeny
 * @since 15.0
 */
public class HsqlDBReporterFactory extends MetricReporterFactory<MetricReporter> {

    public static final String DEFAULT_DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";
    public static final int DEFAULT_HSQLDB_HTTP = 9101;

    private String dbName;

    private String username;
    private String password;
    private int port;
    private String driverClassName;

    @Override
    public void load(Properties properties) {
        super.load(properties);

        setDbName( properties.getProperty( "dbname" ) );
        setDriverClassName( properties.getProperty("driverClassName", DEFAULT_DRIVER_CLASS_NAME ) );
        setPort( getIntProperty(properties, "port", DEFAULT_HSQLDB_HTTP ) );
        setUsername( properties.getProperty( "username" ) );
        setPassword( properties.getProperty( "password" ) );

    }

    private static int getIntProperty(Properties properties, String key, int defaultValue) {
        return properties.containsKey(key) ? Integer.parseInt(properties.getProperty(key)) : defaultValue;
    }

    @Override
    public MetricReporter create() {
        return new HsqlDbReporter(this);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

/*    public Properties getServerProperties(){
        return serverProperties;
    }*/
}
