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



/*
 * @(#)EntrySerializationException.java 1.0  Nov 15, 2005 8:33:40 PM
 */

package com.j_spaces.core;

/**
 * This RuntimeException thrown when failed to serialize or deserialize Entry field. See stack trace
 * or getCause() exception for more details.
 *
 * @author Igor Goldenberg
 * @version 5.0
 **/

public class EntrySerializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntrySerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
