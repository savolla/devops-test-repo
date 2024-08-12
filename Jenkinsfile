pipeline{
  agent{
    label "jenkins-agent01"
  }
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
      steps {
        cleanWs()
      }
    }

    stage("Checkout from SCM"){
      steps {
        git branch: 'main',
          credentialsId: 'github',
          url: 'https://github.com/savolla/devops-test-repo'
      }
    }

    stage("Test Application"){
      steps {
        sh "mvn test"
      }
    }

    stage("Build Application"){
      steps {
        sh "mvn clean package"
      }
    }

    stage("Sonarqube Analysis") {
      steps {
        script {
          withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-cred') {
            sh "mvn sonar:sonar"
          }
        }
      }
    }

    stage("Quality Gate") {
      steps {
        script {
          waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-cred'
        }
      }
    }

    stage("Build & Push Docker Image") {
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
  }
}
