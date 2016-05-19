/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.pgpod;

import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.generator.annotation.KubernetesModelProcessor;

/**
 * Created by farf on 15/03/16.
 */
@KubernetesModelProcessor("kubernetes.json")
public class PostgresAdder {
        public void on(PodTemplateSpecBuilder builder) {
            builder.editSpec()
                    .addNewContainer()
                    .withName("kmsdb")
                    .withImage("openshift/postgresql-92-centos7")
                    .addNewEnv()
                    .withName("POSTGRESQL_USER")
                    .withValue("kimios")
                    .endEnv()
                    .addNewEnv()
                    .withName("POSTGRESQL_PASSWORD")
                    .withValue("kimios")
                    .endEnv()
                    .addNewEnv()
                    .withName("POSTGRESQL_DATABASE")
                    .withValue("kimios")
                    .endEnv()
                    .addNewEnv()
                    .withName("POSTGRESQL_ADMIN_PASSWORD")
                    .withValue("kimios")
                    .endEnv()
                    .addNewPort()
                    .withName("pgport")
                    .withContainerPort(5432)
                    .withProtocol("TCP")
                    .endPort()
                    .withNewSecurityContext()
                    .withPrivileged(true)
                    .endSecurityContext()
                    .endContainer()
                    .endSpec();
        }
}
