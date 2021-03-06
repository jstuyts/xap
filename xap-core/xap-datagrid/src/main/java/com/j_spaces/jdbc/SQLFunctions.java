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

package com.j_spaces.jdbc;

import com.gigaspaces.query.sql.functions.*;
import com.j_spaces.jdbc.builder.range.FunctionCallDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Barak Bar Orion on 1/20/16.
 *
 * @since 11.0
 */
@com.gigaspaces.api.InternalApi
public class SQLFunctions {
    private final Map<String, SqlFunction> functions = new HashMap<String, SqlFunction>();
    private static final Map<String, SqlFunction> builtInFunctions = new HashMap<String, SqlFunction>();

    static {
        builtInFunctions.put("ABS", new AbsSqlFunction());
        builtInFunctions.put("MOD", new ModSqlFunction());
        builtInFunctions.put("ROUND", new RoundSqlFunction());
        builtInFunctions.put("CEIL", new CeilSqlFunction());
        builtInFunctions.put("FLOOR", new FloorSqlFunction());
        builtInFunctions.put("CHAR_LENGTH", new CharLengthSqlFunction());
        builtInFunctions.put("LOWER", new LowerSqlFunction());
        builtInFunctions.put("UPPER", new UpperSqlFunction());
        builtInFunctions.put("APPEND", new AppendSqlFunction());
        builtInFunctions.put("CONCAT", new ConcatSqlFunction());
        builtInFunctions.put("INSTR", new InStrSqlFunction());
        builtInFunctions.put("TO_NUMBER", new ToNumberSqlFunction());
        builtInFunctions.put("TO_CHAR", new ToCharSqlFunction());
        builtInFunctions.put("CONTAINS_KEY", new ContainsKeySqlFunction());
    }

    public SQLFunctions(Map<String, SqlFunction> userFunctions) {
        if (userFunctions != null) {
            functions.putAll(userFunctions);
        }
    }


    public static Object apply(SqlFunction func, SqlFunctionExecutionContext ctx) throws RuntimeException {
        return func.apply(ctx);
    }

    public SqlFunction create(FunctionCallDescription functionCallDescription) {
        SqlFunction userFunction = functions.get(functionCallDescription.getName().toUpperCase());
        if (userFunction != null) {
            return userFunction;
        }
        return builtInFunctions.get(functionCallDescription.getName().toUpperCase());
    }

    public static boolean isBuiltIn(String functionName) {
        return builtInFunctions.keySet().contains(functionName.toUpperCase());
    }

    public static SqlFunction getBuildInFunction(String functionName) {
        return builtInFunctions.get(functionName);
    }
}


