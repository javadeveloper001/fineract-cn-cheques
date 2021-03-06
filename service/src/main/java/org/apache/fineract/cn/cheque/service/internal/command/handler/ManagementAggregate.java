/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.cheque.service.internal.command.handler;

import org.apache.fineract.cn.cheque.api.v1.EventConstants;
import org.apache.fineract.cn.cheque.service.ServiceConstants;
import org.apache.fineract.cn.cheque.service.internal.command.MigrationCommand;
import javax.sql.DataSource;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ApplicationName;
import org.apache.fineract.cn.mariadb.domain.FlywayFactoryBean;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@Aggregate
public class ManagementAggregate {

  private final Logger logger;
  private final DataSource dataSource;
  private final FlywayFactoryBean flywayFactoryBean;
  private final ApplicationName applicationName;

  @Autowired
  public ManagementAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                             final DataSource dataSource,
                             final FlywayFactoryBean flywayFactoryBean,
                             final ApplicationName applicationName) {
    super();
    this.logger = logger;
    this.dataSource = dataSource;
    this.flywayFactoryBean = flywayFactoryBean;
    this.applicationName = applicationName;
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.INITIALIZE)
  public String process(final MigrationCommand migrationCommand) {
    this.logger.info("Starting migration for cheques version: {}.", applicationName.getVersionString());
    this.flywayFactoryBean.create(this.dataSource).migrate();

    this.logger.info("Migration finished.");
    return this.applicationName.getVersionString();
  }
}
