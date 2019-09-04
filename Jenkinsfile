#!groovy

def buildVersion = "1.10.0.${BUILD_NUMBER}"
def infraVersion = "3.10.0.+"
def refDataVersion = "1.0-SNAPSHOT"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/rehabstod.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality -PuseMinifiedJavaScript \
                      -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/allTests', \
                reportFiles: 'index.html', reportName: 'JUnit results'
        }
    }
}

stage('tag and upload') {
    node {
        shgradle "uploadArchives tagRelease -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion} -PuseMinifiedJavaScript"
    }
}

stage('propagate') {
    node {
        gitRef = "v${buildVersion}"
        releaseFlag = "${GIT_BRANCH.startsWith("release")}"
        build job: "rehabstod-dintyg-build", wait: false, parameters: [
                [$class: 'StringParameterValue', name: 'REHABSTOD_BUILD_VERSION', value: buildVersion],
                [$class: 'StringParameterValue', name: 'INFRA_VERSION', value: infraVersion],
                [$class: 'StringParameterValue', name: 'REF_DATA_VERSION', value: refDataVersion],
                [$class: 'StringParameterValue', name: 'GIT_REF', value: gitRef],
                [$class: 'StringParameterValue', name: 'RELEASE_FLAG', value: releaseFlag]
        ]
    }
}
