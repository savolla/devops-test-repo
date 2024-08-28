pipeline{
  agent any
    
  tools {
    // sonarqube 'jenkins-tool-sonarqube-scanner'
    // docker 'jenkins-tool-docker'
    maven 'jenkins-tool-maven'
    jdk 'jenkins-tool-jdk17-temurin'
  }
  environment {
    SCANNER_HOME = tool 'jenkins-tool-sonarqube-scanner'
  }
  stages {
    stage('git pull') {
      steps {
        git branch: 'devops-pipeline-002-application-source', credentialsId: 'jenkins-credential-github', url: 'https://github.com/savolla/devops-test-repo.git'
      }
    }
    stage('compile') {
      steps {
        sh "mvn compile"
      }
    }
    stage('test') {
      steps {
        sh "mvn test"
      }
    }
    // stage('trivy fs/dep scan') {
    //   steps {
    //     sh "trivy fs --format table -o fs.html ."
    //   }
    // }
    // stage('sonarqube scan') {
    //   steps {
    //     withSonarQubeEnv('sonarqube') {
    //       sh ''' 
    //         $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=Blogging-app -Dsonar.projectKey=Blogging-app \
    //         -Dsonar.java.binaries=target
    //         '''
    //     }
    //   }
    // }
    // stage('build') {
    //   steps {
    //     sh "mvn package"
    //   }
    // }
    // stage('publish artifact to nexus') {
    //   steps {
    //     withMaven(globalMavenSettingsConfig: 'maven-settings', jdk: 'jenkins-tool-jdk17-temurin', maven: 'jenkins-tool-maven', mavenSettingsConfig: '', traceability: true) {
    //       sh "mvn deploy"
    //     }
    //   }
    // }
  }
}
