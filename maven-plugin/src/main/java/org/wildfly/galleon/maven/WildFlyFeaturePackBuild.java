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
package org.wildfly.galleon.maven;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.galleon.config.ConfigModel;
import org.jboss.galleon.model.GaecOrGaecv;
import org.jboss.galleon.spec.CapabilitySpec;
import org.jboss.galleon.universe.FeaturePackLocation;
import org.jboss.galleon.util.CollectionUtils;

/**
 * Representation of the feature pack build config
 *
 * @author Stuart Douglas
 * @author Alexey Loubyansky
 */
public class WildFlyFeaturePackBuild {

    public static class Builder {

        private FeaturePackLocation producer;
        private Map<GaecOrGaecv, FeaturePackDependencySpec> dependencies = Collections.emptyMap();
        private Set<String> schemaGroups = Collections.emptySet();
        private Set<String> defaultPackages = Collections.emptySet();
        private List<ConfigModel> configs = Collections.emptyList();

        private Builder() {
        }

        public Builder setProducer(FeaturePackLocation producer) {
            this.producer = producer;
            return this;
        }

        public Builder addDefaultPackage(String packageName) {
            defaultPackages = CollectionUtils.add(defaultPackages, packageName);
            return this;
        }

        public Builder addDependency(GaecOrGaecv gav, FeaturePackDependencySpec dependency) {
            dependencies = CollectionUtils.put(dependencies, gav, dependency);
            return this;
        }

        public Builder addSchemaGroup(String groupId) {
            schemaGroups = CollectionUtils.add(schemaGroups, groupId);
            return this;
        }

        public Builder addConfig(ConfigModel config) {
            configs = CollectionUtils.add(configs, config);
            return this;
        }

        public WildFlyFeaturePackBuild build() {
            return new WildFlyFeaturePackBuild(this);
        }

        void providesCapability(CapabilitySpec cap) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final FeaturePackLocation producer;
    private final Map<GaecOrGaecv, FeaturePackDependencySpec> dependencies;
    private final Set<String> schemaGroups;
    private final Set<String> defaultPackages;
    private final List<ConfigModel> configs;

    private WildFlyFeaturePackBuild(Builder builder) {
        this.producer = builder.producer;
        this.dependencies = CollectionUtils.unmodifiable(builder.dependencies);
        this.schemaGroups = CollectionUtils.unmodifiable(builder.schemaGroups);
        this.defaultPackages = CollectionUtils.unmodifiable(builder.defaultPackages);
        this.configs = CollectionUtils.unmodifiable(builder.configs);
    }

    public FeaturePackLocation getProducer() {
        return producer;
    }

    public Collection<String> getDefaultPackages() {
        return defaultPackages;
    }

    public Map<GaecOrGaecv, FeaturePackDependencySpec> getDependencies() {
        return dependencies;
    }

    public boolean hasSchemaGroups() {
        return !schemaGroups.isEmpty();
    }

    public boolean isSchemaGroup(String groupId) {
        return schemaGroups.contains(groupId);
    }

    public Set<String> getSchemaGroups() {
        return schemaGroups;
    }

    public boolean hasConfigs() {
        return !configs.isEmpty();
    }

    public List<ConfigModel> getConfigs() {
        return configs;
    }
}
