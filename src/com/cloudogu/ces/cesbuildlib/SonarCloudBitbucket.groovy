package com.cloudogu.ces.cesbuildlib

/**
 * Abstraction for SonarCloud. More or less a special SonarQube instance.
 *
 * The integration into GitHub, BitBucket and such is done via 3rd party integrations into those tools.
 * Normal SonarQube uses e.g. the GitHub plugin.
 */
class SonarCloudBitbucket extends SonarQube {

    SonarCloudBitbucket(script, Map config) {
        super(script, config)

        this.isUsingBranchPlugin = true
    }

    @Override
    boolean doWaitForPullRequestQualityGateWebhookToBeCalled() {
        // PRs are now also analyzed on the SonarCloud server, no more preview. Analyze normally.
        return doWaitForQualityGateWebhookToBeCalled()
    }

    @Override
    void initMavenForPullRequest(Maven mvn) {
        script.echo "SonarQube analyzing PullRequest ${script.env.CHANGE_ID}."
        mvn.additionalArgs +=
                "-Dsonar.pullrequest.base=${script.env.CHANGE_TARGET} " +
                "-Dsonar.pullrequest.branch=${script.env.CHANGE_BRANCH} " +
                "-Dsonar.pullrequest.key=${script.env.CHANGE_ID} " +
                "-Dsonar.pullrequest.provider=bitbucketcloud " +
                "-Dsonar.pullrequest.bitbucketcloud.owner=${config['sonarOrganization']} " +
                "-Dsonar.pullrequest.bitbucketcloud.repository=${config['repo']} " +
                "-Dsonar.organization=${config['sonarOrganization']} " +
                "-Dsonar.projectName=${config['repo']} "
    }

    @Override
    protected void initMaven(Maven mvn) {
        super.initMaven(mvn)
        // Only add branch if the branch isn't master.
        mvn.additionalArgs += "-Dsonar.projectName=${config['repo']} "
        if((config['targetBranch'] != null && script.env.BRANCH_NAME != config['targetBranch'] ) ||
            script.env.BRANCH_NAME != 'master') {
           mvn.additionalArgs += " -Dsonar.project.target=${config['targetBranch']}"
           mvn.additionalArgs += " -Dsonar.projectName${script.env.BRANCH_NAME}"
        }
        if (config['sonarOrganization']) {
            mvn.additionalArgs += " -Dsonar.organization=${config['sonarOrganization']} "
        }
    }

    @Override
    protected void validateMandatoryFieldsWithoutSonarQubeEnv() {
        super.validateMandatoryFieldsWithoutSonarQubeEnv()
        // When using SonarQube env, the sonarOrganization might be set there as SONAR_EXTRA_PROPS
        validateFieldPresent(config, 'sonarOrganization')

    }
}
