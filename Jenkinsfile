@Library('deploy')
import deploy

def deployLib = new deploy()

node {
    def commitHash, commitHashShort, commitUrl, currentVersion
    def project = "navikt"
    def repo = "foreldrepenger-selvbetjening-engangsstonad"
    def app = "foreldrepengesoeknad-api"
    def committer, committerEmail, changelog, pom, releaseVersion, nextVersion // metadata
    def mvnHome = tool "maven-3.3.9"
    def mvn = "${mvnHome}/bin/mvn"
    def appConfig = "nais.yaml"
    def dockerRepo = "docker.adeo.no:5000"
    def groupId = "nais"
    def environment = 'q1'
    def zone = 'sbs'
    def namespace = 'default'

    stage("Initialization") {
        cleanWs()
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
            sh(script: "git clone https://github.com/${project}/${repo}.git .")
        }
        commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
        commitHashShort = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        commitUrl = "https://github.com/${project}/${repo}/commit/${commitHash}"
        committer = sh(script: 'git log -1 --pretty=format:"%an"', returnStdout: true).trim()
        committerEmail = sh(script: 'git log -1 --pretty=format:"%ae"', returnStdout: true).trim()
        changelog = sh(script: 'git log `git describe --tags --abbrev=0`..HEAD --oneline', returnStdout: true)
        slackSend([
                color: 'good',
                message: "Build <${env.BUILD_URL}|#${env.BUILD_NUMBER}> (<${commitUrl}|${commitHashShort}>) of ${project}/${repo}@master by ${committer} passed  (${changelog})"
        ])
        notifyGithub(project, repo, 'continuous-integration/jenkins', commitHash, 'pending', "Build #${env.BUILD_NUMBER} has started")

        releaseVersion = "${env.major_version}.${env.BUILD_NUMBER}-${commitHashShort}"
    }

    stage("Build & publish") {
        sh "${mvn} versions:set -B -DnewVersion=${releaseVersion}"
        sh "${mvn} clean install -Djava.io.tmpdir=/tmp/${repo} -B -e"
        sh "docker build --build-arg version=${releaseVersion} --build-arg app_name=${app} -t ${dockerRepo}/${app}:${releaseVersion} ."
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
            withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
                sh ("git tag -a ${releaseVersion} -m ${releaseVersion}")
                sh ("git push https://${token}:x-oauth-basic@github.com/${project}/${repo}.git --tags")
                // Maybe just tag production releases?
            }
        }

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexusUser', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            sh "curl --user uploader:upl04d3r --upload-file ${appConfig} https://repo.adeo.no/repository/raw/${groupId}/${app}/${releaseVersion}/nais.yaml"
        }
        sh "docker push ${dockerRepo}/${app}:${releaseVersion}"

        sh "${mvn} versions:revert"
    }

    stage("Deploy to preprod") {
        callback = "${env.BUILD_URL}input/Deploy/"
        deployLib.testCmd(releaseVersion)
        deployLib.testCmd(committer)

        def deploy = deployLib.deployNaisApp(app, releaseVersion, environment, zone, namespace, callback, committer).key

        // Block and wait for the remote system to callback
        input id: 'Deploy', message: 'Waiting for remote system'
    }
}

def notifyGithub(owner, repo, context, sha, state, description) {
    def postBody = [
            state: "${state}",
            context: "${context}",
            description: "${description}",
            target_url: "${env.BUILD_URL}"
    ]
    def postBodyString = groovy.json.JsonOutput.toJson(postBody)

    withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
        withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
            sh """
                curl -H 'Authorization: token ${token}' \
                    -H 'Content-Type: application/json' \
                    -X POST \
                    -d '${postBodyString}' \
                    'https://api.github.com/repos/${owner}/${repo}/statuses/${sha}'
            """
        }
    }
}