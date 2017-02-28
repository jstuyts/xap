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

package com.gigaspaces.start;

import com.gigaspaces.internal.version.PlatformVersion;
import com.gigaspaces.logger.LoggerSystemInfo;
import com.gigaspaces.start.manager.XapManagerClusterInfo;
import com.gigaspaces.time.AbsoluteTime;
import com.gigaspaces.time.ITimeProvider;

import net.jini.core.discovery.LookupLocator;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.gigaspaces.CommonSystemProperties.SYSTEM_TIME_PROVIDER;

/**
 * @author Niv Ingberg
 * @since 11.0
 */
@com.gigaspaces.api.InternalApi
public class SystemInfo {

    public static final String XAP_LOOKUP_GROUPS = "com.gs.jini_lus.groups";
    public static final String XAP_LOOKUP_LOCATORS = "com.gs.jini_lus.locators";

    private static final SystemInfo instance = new SystemInfo();

    private final String xapHome;
    private final XapLocations locations;
    private final XapLookup lookup;
    private final XapNetworkInfo network;
    private final XapOperatingSystem os;
    private final XapTimeProvider timeProvider;
    private final XapManagerClusterInfo managerClusterInfo;

    public static SystemInfo singleton() {
        return instance;
    }

    private SystemInfo() {
        this.xapHome = LoggerSystemInfo.xapHome;
        this.os = new XapOperatingSystem(LoggerSystemInfo.processId);
        this.network = LoggerSystemInfo.networkInfo;
        this.locations = new XapLocations(xapHome);
        this.lookup = new XapLookup();
        this.timeProvider = new XapTimeProvider();
        this.managerClusterInfo = new XapManagerClusterInfo(this.network.getHostId());
    }

    public String getXapHome() {
        return xapHome;
    }

    public XapLocations locations() {
        return locations;
    }

    public XapLookup lookup() {
        return lookup;
    }

    public XapNetworkInfo network() {
        return network;
    }

    public XapOperatingSystem os() {
        return os;
    }

    public XapManagerClusterInfo getManagerClusterInfo() {
        return managerClusterInfo;
    }

    public XapTimeProvider timeProvider() {
        return timeProvider;
    }

    public static class XapLocations {
        private final String config;
        private final String lib;
        private final String libRequired;
        private final String libOptional;
        private final String libPlatform;
        private final String work;
        private final String deploy;

        private XapLocations(String xapHome) {
            this.config = path(xapHome, "config");
            this.lib = path(xapHome, "lib");
            this.libRequired= path(lib, "required");
            this.libOptional= path(lib, "optional");
            this.libPlatform= path(lib, "platform");
            this.work = System.getProperty("com.gs.work", path(xapHome, "work"));
            this.deploy = System.getProperty("com.gs.deploy", path(xapHome, "deploy"));
        }

        public String config() {
            return config;
        }

        public String work() {
            return work;
        }

        public String restResources() {
            return work + File.separator + "RESTresources";
        }

        public String deploy(){
            return deploy;
        }

        private static String path(String base, String subdir) {
            return base + File.separator + subdir;
        }

        public String lib() {
            return lib;
        }

        public String getLibRequired() {
            return libRequired;
        }

        public String getLibOptional() {
            return libOptional;
        }

        public String getLibPlatform() {
            return libPlatform;
        }
    }

    public static class XapLookup {

        private static final String SEPARATOR = ",";
        private String groups;
        private String[] groupsArray;
        private String locators;
        private LookupLocator[] locatorsArray;

        private XapLookup() {
            setGroups(System.getProperty(XAP_LOOKUP_GROUPS, System.getenv("XAP_LOOKUP_GROUPS")));
            setLocators(System.getProperty(XAP_LOOKUP_LOCATORS, System.getenv("XAP_LOOKUP_LOCATORS")));
        }

        public String defaultGroups() {
            return "xap-" + PlatformVersion.getVersion();
        }

        public String groups() {
            return groups;
        }

        public String[] groupsArray() {
            return groupsArray;
        }

        public String locators() {
            return locators;
        }

        public LookupLocator[] locatorsArray() {
            return locatorsArray;
        }

        public String setGroups(String groups) {
            String prevValue = this.groups;
            if (groups != null)
                groups = groups.trim();
            if (groups == null || groups.length() == 0)
                groups = defaultGroups();
            this.groups = groups;
            setSystemProperty(XAP_LOOKUP_GROUPS, groups);

            List<String> groupsList = toList(groups, SEPARATOR);
            this.groupsArray = groupsList.toArray(new String[groupsList.size()]);
            return prevValue;
        }

        public void setGroups(String[] lookupGroups) {
            setGroups(join(lookupGroups, SEPARATOR));
        }

        public String setLocators(String locators) {
            String prevValue = this.locators;
            this.locators = locators;
            setSystemProperty(XAP_LOOKUP_LOCATORS, locators);

            List<String> locatorsList = toList(locators, SEPARATOR);
            this.locatorsArray = new LookupLocator[locatorsList == null ? 0 : locatorsList.size()];
            for (int i = 0; i < locatorsArray.length; i++) {
                try {
                    locatorsArray[i] = new LookupLocator("jini://" + locatorsList.get(i));
                } catch (MalformedURLException e) {
                    throw new IllegalStateException("Failed to generate locators for " + locatorsList.get(i), e);
                }
            }
            return prevValue;
        }

        private static String join(String[] array, String separator) {
            if (array == null)
                return null;
            if (array.length == 0)
                return "";
            if (array.length == 1)
                return array[0];
            StringBuilder sb = new StringBuilder(array[0]);
            for (int i = 1; i < array.length; i++)
                sb.append(separator).append(array[i]);
            return sb.toString();
        }

        private static List<String> toList(String s, String separator) {
            if (s == null)
                return null;
            ArrayList<String> result = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(s, separator);
            while (tokenizer.hasMoreTokens())
                result.add(tokenizer.nextToken().trim());
            return result;
        }

        private static String setSystemProperty(String key, String value) {
            return value != null ? System.setProperty(key, value) : System.clearProperty(key);
        }
    }

    public static class XapOperatingSystem {
        private final long processId;

        public XapOperatingSystem(long pid) {
            this.processId = pid;
        }

        public long processId() {
            return processId;
        }
    }

    public static class XapTimeProvider {
        private final ITimeProvider _timeProvider;

        public XapTimeProvider() {
            final String timeProviderClassName = System.getProperty(SYSTEM_TIME_PROVIDER);
            ;
            if (timeProviderClassName == null || timeProviderClassName.length() == 0) {
                _timeProvider = new AbsoluteTime();

            } else {
                try {
                    final Class timeProviderClass = Class.forName(timeProviderClassName);
                    final Object timeProvider = timeProviderClass.newInstance();
                    _timeProvider = (ITimeProvider) timeProvider;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Unable to load time-provider: " + timeProviderClassName +
                            " - verify that this provider class is in the classpath", e);
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to load time-provider: " + timeProviderClassName, e);
                }
            }
        }

        /**
         * @return TimeProvider class name of object representing this provider
         */
        public String getTimeProviderName() {
            return _timeProvider.getClass().getName();
        }

        /**
         * @return <tt>true</tt> if time-provider is of RelativeTime
         */
        public boolean isRelativeTime() {
            return _timeProvider.isRelative();
        }

        public long timeMillis() {
            return _timeProvider.timeMillis();
        }
    }
}
