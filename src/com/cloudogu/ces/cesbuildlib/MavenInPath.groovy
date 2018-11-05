package com.cloudogu.ces.cesbuildlib

/**
 * Run maven using a Maven Wrapper from the local repository.
 *
 * See https://github.com/takari/maven-wrapper
 */
class MavenInPath extends Maven {

    MavenInPath(script) {
        super(script)
    }

    @Override
    def mvn(String args) {
        script.sh "mvn ${createCommandLineArgs(args)}"
    }
}
