pipeline{
  agent{
    label "jenkins-agent01"
  }
  tools {
    jdk 'Java17'
    maven 'Maven3'
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
  }
}
