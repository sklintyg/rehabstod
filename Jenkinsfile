#!groovy

def buildVersion = "1.11.0.${BUILD_NUMBER}"
def infraVersion = "3.11.0.+"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/rehabstod.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('owasp') {
    node {
        try {
            shgradle "clean dependencyCheckAggregate -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports', \
                reportFiles: 'dependency-check-report.html', reportName: 'OWASP dependency-check'
        }
    }
}
