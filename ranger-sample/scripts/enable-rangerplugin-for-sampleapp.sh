#!/bin/bash
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

mkdir -p /var/log/sampleapp/
mkdir -p /etc/sampleapp/

SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

ln -s $SCRIPTPATH/conf /etc/sampleapp/
ln -s $SCRIPTPATH/log  /var/log/sampleapp/
printf "\n"
printf "Creating Ranger Service for Sample app.....\n"
curl -u admin:admin -H "Accept: application/json" -H "Content-Type: application/json" -X POST http://localhost.localdomain:6080/service/public/v2/api/servicedef -d @ranger-servicedef-sampleapp.json
printf "\n"
printf "Creating Ranger Service for Sample app.....\n"
printf "\n"
curl -u admin:admin -H "Accept: application/json" -H "Content-Type: application/json" -X POST http://localhost.localdomain:6080/service/public/v2/api/service -d @ranger-service-sampleapp.json
printf "\n"