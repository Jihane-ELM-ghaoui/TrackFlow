pipeline {
    agent any
    tools {
        jdk 'JDK 21'
        maven 'Maven 3.8.4'
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }
        stage('Install Dependencies') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Build Application') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Static Code Analysis') {
            steps {
                sh 'mvn checkstyle:check'
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
