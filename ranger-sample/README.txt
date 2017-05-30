# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


1. Pre-requisite
    This SampleApp need a cluster with HDFS, RANGER and SOLR running to see all the functionalties.

2. SampleApp
   A simple application to demonstrate use of pluggable authorization.
   - IAuthorizer:
      the authorization interface. Authorizes read/write/execute access to a given file
   - RangerSampleAppAuthorizer:
      Ranger authorizer implementation, authorizes all accesses
   - SampleApp:
      - main application that prompts the user to enter access to authorize in the following format:
         read filePath user1
         write filePath user1
         execute filePath user1
      - Groups are determined by the hadoop UGI api.


3. SampleApp Plugin
   - RangerSampleAppAuthorizer implements IAuthorizer interface and performs authorization using Ranger policies.
   - ranger-sampleapp/conf/ranger-sampleapp-security.xml: has configurations for plugin, like Ranger Admin URL, name of the service containing policies
   - ranger-sampleapp/conf/ranger-sampleapp-audit.xml: has configurations for plugin audit into Solr, HDFS folder, log4j, kafka.

4. Build
   $ mvn clean compile package assembly:assembly
   $ cd ranger-examples
   $ mvn clean compile package assembly:assembly
   # Following files created by the build will be required to setup SampleApp:
     target/ranger-<version>-sampleapp.tar.gz

5. Setup SampleApp
   # Create a empty directory to setup the application in a cluster where HDFS, RANGER and SOLR is running.
   $ mkdir /sampleapp
   $ cd  /sampleappranger-<version>-sampleapp.tar.gz
   $ tar xvfz ranger-examples-<version>-sampleapp.tar.gz

6. Execute
