#!/usr/bin/env bash

nohup sudo java -jar -Xmx256m -Dconfig.file=app.properties polyjuice-phial-assembly-0.1.1-SNAPSHOT.jar > app.log 2>&1 &
