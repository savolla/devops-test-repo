pipeline{
  agent none

  tools {
    jdk 'Java17'
      maven 'Maven3'
  }

  environment {
    APP_NAME = "devops-test-repo"
    RELEASE = "1.0.0"
    DOCKERHUB_USER = "emusky"
    DOCKERHUB_JENKINS_CREDENTIAL_ID = 'dockerhub-cred'
    IMAGE_NAME = "${DOCKERHUB_USER}" + "/" + "${APP_NAME}"
    IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
  }

  stages{
    stage("Cleanup Workspace"){
      agent { label 'jenkins-agent01' } 
      steps {
        cleanWs()
      }
    }

    stage("Checkout from SCM"){
      agent { label 'jenkins-agent01' } 
      steps {
        git branch: 'main',
            credentialsId: 'github',
            url: 'https://github.com/savolla/devops-test-repo'
      }
    }

    stage("Test Application"){
      agent { label 'jenkins-agent01' } 
      steps {
        sh "mvn test"
      }
    }

    stage("Build Application"){
      agent { label 'jenkins-agent01' } 
      steps {
        sh "mvn clean package"
      }
    }

    stage("Sonarqube Analysis") {
      agent { label 'jenkins-agent01' } 
      steps {
        script {
          withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-cred') {
            sh "mvn sonar:sonar"
          }
        }
      }
    }

    stage("Quality Gate") {
      agent { label 'jenkins-agent01' } 
      steps {
        script {
          waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-cred'
        }
      }
    }

    stage("Build & Push Docker Image") {
      agent { label 'jenkins-agent01' } 
      steps {
        script {
          docker.withRegistry('',DOCKERHUB_JENKINS_CREDENTIAL_ID) {
            docker_image = docker.build "${IMAGE_NAME}"
          }

          docker.withRegistry('',DOCKERHUB_JENKINS_CREDENTIAL_ID) {
            docker_image.push("${IMAGE_TAG}")
              docker_image.push('latest')
          }
        }
      }
    }
    stage("scan for vulnerabilities of docker image") {
      agent { label 'jenkins-agent02' } 
      steps {
        sh 'trivy --no-progress --exit-code 1 --severity HIGH,CRITICAL emusky/devops-test-repo:latest'
      }
    }
  }
}
