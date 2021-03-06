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

package com.gigaspaces.internal.sigar;

import com.gigaspaces.CommonSystemProperties;
import com.gigaspaces.internal.jvm.JavaUtils;
import com.gigaspaces.internal.os.ProcessKiller;

/**
 * @author Niv Ingberg
 * @since 10.1
 */
@com.gigaspaces.api.InternalApi
public class SigarChecker {

    private static final boolean enabled = initEnabled();

    private static boolean initEnabled() {
        String enabled = System.getProperty(CommonSystemProperties.SIGAR_ENABLED, "");
        if (enabled.isEmpty()) {
            return JavaUtils.isWindows() && JavaUtils.greaterOrEquals(9) ? false : true;
        } else {
            return Boolean.parseBoolean(enabled);
        }
    }

    public static boolean isAvailable() {
        try {
            return enabled && SigarHolder.getSigar() != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public static ProcessKiller createProcessKiller() {
        return new ProcessKiller() {
            @Override
            public String getName() {
                return "Sigar";
            }

            @Override
            public boolean kill(long pid, long timeout, boolean recursive) {
                try {
                    return SigarHolder.singleton().kill(pid, timeout, recursive);
                } catch (Throwable e) {
                    throw new RuntimeException("Failed to kill process " + pid + ": " + e.getMessage(), e);
                }
            }
        };
    }
}
