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

import java.net.InetAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ranger.audit.provider.MiscUtil;
import org.apache.ranger.plugin.policyengine.RangerAccessRequestImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResourceImpl;

public class SampleApp {
	private static final Log LOG = LogFactory.getLog(SampleApp.class);

	private static final Set<String> VALID_ACCESS_TYPES = new HashSet<String>();

	private IAuthorizer authorizer = null;

	public static void main(String[] args) {
		SampleApp app = new SampleApp();
		app.init();
		app.run();
	}

	public void init() {
		VALID_ACCESS_TYPES.add("read");
		VALID_ACCESS_TYPES.add("write");
		VALID_ACCESS_TYPES.add("execute");

		authorizer = createAuthorizer();
	}

	public void run() {
		LOG.debug("==> SampleApp.run()");

		do {
			try {
				System.out.println("Usage: <accessType> <fileName> <userName>");
				String input = getInput();
				System.out.println("Input1: " + input);

				if (input == null) {
					break;
				}

				if (input.trim().isEmpty()) {
					continue;
				}

				String[] args = input.split("\\s+");

				String accessType = getStringArg(args, 0);
				String fileName = getStringArg(args, 1);
				String userName = getStringArg(args, 2);

				if (fileName == null || accessType == null || userName == null) {
					System.out.println("Insufficient arguments. Usage: <accessType> <fileName> <userName>");
					LOG.info("Insufficient arguments. Usage: <accessType> <fileName> <userName>");
					continue;
				}

				if (!VALID_ACCESS_TYPES.contains(accessType)) {
					System.out.println(accessType + ": invalid accessType");
					LOG.info(accessType + ": invalid accessType");
					continue;
				}

				RangerAccessRequestImpl request = buildAccessRequest(fileName, accessType, userName);

				if (authorizer.authorize(request)) {
					System.out.println(userName + " authorized!");
					LOG.info("Authorized!");
				} else {
					System.out.println(userName + " not authorized!");
					LOG.info("Not authorized!");
				}
			} catch ( Exception e) {
				e.printStackTrace();
			}

		} while(true);

		LOG.debug("<== SampleApp.run()");
	}

	private IAuthorizer createAuthorizer() {
		IAuthorizer ret = null;

		String authzClassName = System.getProperty("sampleapp.authorizer");

		if(authzClassName != null) {
			try {
				Class<IAuthorizer> clz = (Class<IAuthorizer>) Class.forName(authzClassName);

				ret = clz.newInstance();
			} catch(Exception excp) {
				LOG.warn("Failed to create authorizer of type '" + authzClassName + "'", excp);
			}
		}

		if(ret == null) {
			LOG.info("Using Ranger authorizer");
			ret = new RangerSampleAppAuthorizer();
		}

		ret.init();

		return ret;
	}

	private String getStringArg(String[] args, int index) {
		if(args == null || args.length <= index) {
			return null;
		}

		return args[index];
	}

	private String getInput() {
		String ret = null;
		try {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("command> ");
			ret = inputReader.readLine();
		} catch(Exception excp) {
			excp.printStackTrace();
		}

		return ret;
	}

	private RangerAccessRequestImpl buildAccessRequest(String fileName, String accessType, String userName) {
		Set<String> userGroups = MiscUtil.getGroupsForRequestUser(userName);

		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			ip = null;
		}

		// skip leading slash
		if (StringUtils.isNotEmpty(ip) && ip.charAt(0) == '/') {
			ip = ip.substring(1);
		}

		Date eventTime = new Date();
		String action = accessType;

		RangerAccessRequestImpl rangerRequest = new RangerAccessRequestImpl();
		rangerRequest.setUser(userName);
		rangerRequest.setUserGroups(userGroups);
		rangerRequest.setClientIPAddress(ip);
		rangerRequest.setAccessTime(eventTime);

		RangerAccessResourceImpl rangerResource = new RangerAccessResourceImpl();
		rangerRequest.setResource(rangerResource);
		rangerRequest.setAccessType(accessType);
		rangerRequest.setAction(action);
		rangerRequest.setRequestData(fileName);
		rangerResource.setValue("path", fileName);

		return  rangerRequest;
	}
}
