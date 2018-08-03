/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.galleon.plugin.config.generator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Properties;

import org.jboss.galleon.MessageWriter;
import org.jboss.galleon.ProvisioningException;
import org.wildfly.galleon.plugin.server.ClassLoaderHelper;

/**
 *
 * @author Emmanuel Hugonnet (c) 2017 Red Hat, inc.
 */
public class CompleteServerInvoker {

    private final Path installationDir;
    private final String serverConfig;
    private Object server;
    private final URLClassLoader newCl;

    public CompleteServerInvoker(Path installationDir, MessageWriter messageWriter, String serverConfig) {
        this.installationDir = installationDir;
        this.serverConfig = serverConfig;
        final ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        newCl = ClassLoaderHelper.prepareClassLoader(installationDir, originalCl);
    }

    public void startServer() throws IOException, ProvisioningException {
        final ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        Properties props = System.getProperties();
        try {
            Thread.currentThread().setContextClassLoader(newCl);
            final Class<?> serverClass = newCl.loadClass("org.wildfly.galleon.plugin.config.generator.CompleteServer");
            server = serverClass.getConstructor(Path.class, String.class).newInstance(installationDir, serverConfig);
            final Method startServerMethod = serverClass.getMethod("startServer");
            startServerMethod.invoke(server);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | NoClassDefFoundError ex) {
            throw new ProvisioningException(ex.getMessage(), ex);
        } finally {
            Thread.currentThread().setContextClassLoader(originalCl);
            System.setProperties(props);
        }
    }

    public void stopServer() throws ProvisioningException {
        final ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(newCl);
            if (server != null) {
                final Method stopServerMethod = server.getClass().getMethod("stopServer");
                stopServerMethod.invoke(server);
            }
            server = null;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ProvisioningException(ex.getMessage(), ex);
        } finally {
            Thread.currentThread().setContextClassLoader(originalCl);
            ClassLoaderHelper.close(newCl);
        }
    }

    public boolean isServerActive() {
        return server == null;
    }
}
