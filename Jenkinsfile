pipeline {
    agent any
    tools {
        // in Jenkins admistration tools, add Maven 3.8.3 path with name 'Maven 3.8.3'
        maven 'Maven 3.8.3'
        // in Jenkins admistration tools, add JDK 17 path with name 'JDK17'
        jdk 'JDK17'
    }
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building ..'
                sh 'mvn -e -DskipTests package'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing ..'
                sh 'mvn surefire:test'
            }
        }
    }
}