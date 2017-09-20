#!groovy

def buildVersion = "1.5.${BUILD_NUMBER}"
def infraVersion = "3.5.+"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/rehabstod.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality -Prehabstod.useMinifiedJavaScript \
                      -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/allTests', \
                reportFiles: 'index.html', reportName: 'JUnit results'
        }
    }
}

stage('deploy') {
    node {
        util.run {
            ansiblePlaybook extraVars: [version: buildVersion, ansible_ssh_port: "22", deploy_from_repo: "false"], \
                installation: 'ansible-yum', inventory: 'ansible/inventory/rehabstod/test', playbook: 'ansible/deploy.yml'
            util.waitForServer('https://rehabstod.inera.nordicmedtest.se/version.jsp')
        }
    }
}

stage('restAssured') {
    node {
        try {
            shgradle "restAssuredTest -DbaseUrl=http://rehabstod.inera.nordicmedtest.se/ -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'web/build/reports/tests/restAssuredTest', \
                reportFiles: 'index.html', reportName: 'RestAssured results'
        }
    }
}

stage('protractor') {
    node {
        try {
            sh(script: 'rm -rf test/node_modules/rehabstod-testtools') // Without this, node does not always recognize that a new version is available.
            wrap([$class: 'Xvfb']) {
                shgradle "protractorTests -Dprotractor.env=build-server -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
            }
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'test/dev/report', \
                reportFiles: 'index.html', reportName: 'Protractor results'
        }
    }
}

stage('tag and upload') {
    node {
        shgradle "uploadArchives tagRelease -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion} -Prehabstod.useMinifiedJavaScript"
    }
}
