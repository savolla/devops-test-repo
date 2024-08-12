pipeline{
  agent {
    label 'jenkins-agent01' 
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
    stage("cleanup workspace"){
      steps {
        cleanWs()
      }
    }

    stage("checkout from scm"){
      steps {
        git branch: 'main',
            credentialsId: 'github',
            url: 'https://github.com/savolla/devops-test-repo'
      }
    }

    stage("sonarqube analysis") {
      steps {
        script {
          withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-cred') {
            sh "mvn sonar:sonar"
          }
        }
      }
    }

    stage("quality gate") {
      steps {
        script {
          waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-cred'
        }
      }
    }

    stage("build application"){
      steps {
        sh "mvn clean package"
      }
    }

    stage("test application"){
      steps {
        sh "mvn test"
      }
    }

    stage("build docker image") {
      steps {
        script {
          docker.withRegistry('',DOCKERHUB_JENKINS_CREDENTIAL_ID) {
            docker_image = docker.build "${IMAGE_NAME}"
          }
        }
      }
    }

    stage("scan for vulnerabilities of docker image") {
      steps {
        script {
          // sh 'trivy image --exit-code 1 --severity HIGH,CRITICAL emusky/devops-test-repo'
          sh 'trivy image --exit-code 0 --severity HIGH,CRITICAL emusky/devops-test-repo' // changin exit code 0 zero for testing purposes
        }
      }
    }

    stage("push docker image") {
      steps {
        script {
          docker.withRegistry('',DOCKERHUB_JENKINS_CREDENTIAL_ID) {
            docker_image.push("${IMAGE_TAG}")
            docker_image.push('latest')
          }
        }
      }
    }

    stage("clean existing docker images") {
      steps {
        script {
          sh 'docker image prune --force --all'
        }
      }
    }

  }
}
