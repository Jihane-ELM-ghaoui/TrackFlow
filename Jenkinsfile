pipeline {
    agent any

    tools {
        maven 'Maven' 
        jdk 'JDK21'  
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/Jihane-ELM-ghaoui/TrackFlow.git', branch: 'Storage-Service'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml' // Publish test reports
        }
        failure {
            echo 'Build failed. Check the logs for details.'
        }
    }
}
