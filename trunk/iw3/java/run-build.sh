#!/bin/bash
cd /work/iw/svn-google-sw4j
svn update .
ant build
cd ..
cd svn-code-iw3
svn update .
ant build