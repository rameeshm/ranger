/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.sample;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ranger.plugin.audit.RangerDefaultAuditHandler;
import org.apache.ranger.plugin.policyengine.RangerAccessRequest;
import org.apache.ranger.plugin.policyengine.RangerAccessResult;
import org.apache.ranger.plugin.service.RangerBasePlugin;

public class RangerSampleAppAuthorizer implements IAuthorizer {

    public static final Log LOG = LogFactory.getLog(RangerSampleAppAuthorizer.class);

    private static volatile RangerBasePlugin rangerPlugin = null;

    public RangerSampleAppAuthorizer() { }

    public void init() {
        rangerPlugin = new RangerBasePlugin("sampleapp", "sampleapp");
        LOG.info("Calling plugin.init()");
        rangerPlugin.init();
        RangerDefaultAuditHandler auditHandler = new RangerDefaultAuditHandler();
        rangerPlugin.setResultProcessor(auditHandler);
    }

    public boolean authorize(RangerAccessRequest request) {
        LOG.info("==>RangerSampleAppAuthorizer.authorize: [" + request + "]");
        boolean accessAllowed  = false;
        boolean isAuditEnabled = false;
        try {
            RangerAccessResult result = rangerPlugin.isAccessAllowed(request);
            accessAllowed = result != null && result.getIsAllowed();
            isAuditEnabled = result != null && result.getIsAudited();
        } catch ( Exception e) {
            LOG.error("RangerSampleAppAuthorizer failed !", e);
        } finally {
                LOG.info("<==RangerSampleAppAuthorizer.authorize() :"
                        + "[req "+ request+ "] Access "
                        + " from IP: [" + request.getRemoteIPAddress() + "]"
                        + " user: [" + request.getUser() + "],"
                        + " op:   [" + request.getAccessType() + "],"
                        + "resource : [" + request.getResource().getAsMap() + "] => returns [" + accessAllowed + "], Audit Enabled:" + isAuditEnabled);
           }

        return accessAllowed;

    }
}