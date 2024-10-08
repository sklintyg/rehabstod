#!groovy

node {

    def buildVersion = ""
    def infraVersion = "N/A"
    def versionFlags = ""

    stage('checkout') {
        git url: "https://github.com/sklintyg/rehabstod.git", branch: GIT_BRANCH
        util.run { checkout scm }

        def info = readJSON file: 'build-info.json'
        echo "${info}"
        buildVersion = "${info.appVersion}.${BUILD_NUMBER}-nightly"
        infraVersion = info.infraVersion

        versionFlags = "-DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
    }

    stage('owasp') {
        try {
            shgradle11 "--refresh-dependencies clean dependencyCheckAggregate ${versionFlags}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports', \
                reportFiles: 'dependency-check-report.html', reportName: 'OWASP dependency-check'
        }
    }

    stage('sonarqube') {
        try {
            shgradle11 "build -P codeQuality -P useNoMinifiedJavaScriptNoKarma jacocoTestReport sonarqube -DsonarProjectPrefix=intyg- ${versionFlags}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'web/build/reports/jacoco/test/html', \
            reportFiles: 'index.html', reportName: 'Code coverage'
        }
    }
}
