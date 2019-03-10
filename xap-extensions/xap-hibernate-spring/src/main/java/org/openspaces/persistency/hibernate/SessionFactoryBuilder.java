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

/**
 *
 */
package org.openspaces.persistency.hibernate;

import com.gigaspaces.internal.utils.ClassLoaderUtils;
import com.j_spaces.kernel.ClassLoaderHelper;

import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Utility class - used to create and configure hibernate session factory
 *
 * @author anna
 * @since 6.0
 * @deprecated use org.openspaces.persistency.hibernate package instead
 */
@Deprecated
public class SessionFactoryBuilder {

    private static final String HIBERNATE_IMPLICIT_NAMING_STRATEGY = "hibernate.implicit_naming_strategy";
    private static final String HIBERNATE_PHYSICAL_NAMING_STRATEGY = "hibernate.physical_naming_strategy";

    private static final String HIBERNATE_NAMING_STRATEGY = "hibernate.naming_strategy";

    /**
     *
     */
    private static final String ANNOTATION_CONFIGURATION_CLASS = "org.hibernate.cfg.AnnotationConfiguration";
    private final static Logger _logger = Logger.getLogger(com.gigaspaces.logger.Constants.LOGGER_PERSISTENT);


    /**
     * Create and configure new hibernate session factory
     *
     * @return SessionFactory
     */
    public static SessionFactory getFactory(String hibernateFile)
            throws Exception {
        Configuration config = null;

        try {
            // load the class using reflection to avoid JIT exceptions  
            config = configure((Configuration) ClassLoaderHelper.loadClass(ANNOTATION_CONFIGURATION_CLASS).newInstance(),
                    hibernateFile);
        } catch (Throwable t) {
            try {
                config = configure(new Configuration(), hibernateFile);
            } catch (Exception e) {
                // if both methods failed - log first exception
                // and throw the second
                if (_logger.isLoggable(Level.SEVERE)) {
                    _logger.log(Level.SEVERE, "Failed to configure using hibernate annotations.", t);

                }

                throw e;
            }
        }
        // since hibernate doesn't support configuring naming strategies in cfg.xml.
        // added an option to configure it programmatically while using the hibernate.cfg.xml
        // for example: add this to hibernate.cfg.xml
        //<property name="hibernate.naming_strategy">com.gigaspaces.test.persistent.SpaceNamingStrategy</property>

        String namingStrategyClass = config.getProperty(HIBERNATE_NAMING_STRATEGY);

        if (namingStrategyClass != null) {
            _logger.log( Level.WARNING, "Definition of naming strategy [" +
                             HIBERNATE_NAMING_STRATEGY + "] found, it's unsupported " +
                             "since hibernate 5.x used, please use instead either [" +
                             HIBERNATE_IMPLICIT_NAMING_STRATEGY  + "] or [" +
                             HIBERNATE_PHYSICAL_NAMING_STRATEGY + "]" );
        }

        String physicalNamingStrategyClass = config.getProperty(HIBERNATE_PHYSICAL_NAMING_STRATEGY);

        if (physicalNamingStrategyClass != null) {
            PhysicalNamingStrategy physicalNamingStrategy =
                (PhysicalNamingStrategy) ClassLoaderHelper.loadClass(physicalNamingStrategyClass).newInstance();
            config.setPhysicalNamingStrategy(physicalNamingStrategy);
        }

        String implicitNamingStrategyClass = config.getProperty(HIBERNATE_IMPLICIT_NAMING_STRATEGY);

        if (implicitNamingStrategyClass != null) {
            ImplicitNamingStrategy implicitNamingStrategy =
                (ImplicitNamingStrategy) ClassLoaderHelper.loadClass(implicitNamingStrategyClass).newInstance();
            config.setImplicitNamingStrategy(implicitNamingStrategy);
        }

        _logger.info( ClassLoaderUtils.getCurrentClassPathString( "TEST" ) );

        return config.buildSessionFactory();
    }

    /**
     * Configure according to hibernate.cfg.xml
     */
    private static Configuration configure(Configuration config,
                                           String hibernateFile) {
        // In case that hibernate config file location is null find hibernate.cfg.xml
        // file in classpath
        if (hibernateFile == null)
            return config.configure();
        else
            return config.configure(hibernateFile);

    }
}
