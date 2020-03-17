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

package com.gigaspaces.client.iterator;

import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.internal.client.spaceproxy.ISpaceProxy;
import com.gigaspaces.logger.Constants;
import com.j_spaces.core.client.SQLQuery;

import net.jini.core.transaction.Transaction;

import java.io.Closeable;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.j_spaces.kernel.SystemProperties.SPACE_ITERATOR_IMPLEMENTATION;
import static com.j_spaces.kernel.SystemProperties.SPACE_ITERATOR_IMPLEMENTATION_DEFAULT;

/**
 * @author Niv Ingberg
 * @since 10.1
 */
@com.gigaspaces.api.InternalApi
public class SpaceIterator<T> implements Iterator<T>, Iterable<T>, Closeable { //TODO discuss how expose not internal API
    private static final Logger _logger = Logger.getLogger(Constants.LOGGER_GSITERATOR);
    private final boolean useServerBasedImpl = Boolean.parseBoolean(System.getProperty(SPACE_ITERATOR_IMPLEMENTATION, SPACE_ITERATOR_IMPLEMENTATION_DEFAULT));
    public static int getDefaultBatchSize() {
        return 100;
    }
    public static long getDefaultLease(){return TimeUnit.SECONDS.toMillis(60);}


    private final IEntryPacketIterator iterator;

    public SpaceIterator(ISpaceProxy spaceProxy, Object query, Transaction txn, int batchSize, ReadModifiers modifiers) {
        if (query instanceof SQLQuery && ((SQLQuery)query).getExplainPlan() != null) {
            throw new UnsupportedOperationException("Sql explain plan does not support space iterator");
        }
        if(_logger.isLoggable(Level.INFO)) {
            String side = useServerBasedImpl ? "server" : "client";
            _logger.info("Space Iterator is using " +  side + " based implementation");
        }
        this.iterator = useServerBasedImpl ? new ServerBasedEntryPacketIterator(spaceProxy, query, batchSize, modifiers.getCode()) : new SpaceEntryPacketIterator(spaceProxy, query, txn, batchSize, modifiers.getCode());
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return (T) iterator.nextEntry();
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public void close() {
        iterator.close();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
