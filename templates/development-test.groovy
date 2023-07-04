pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                // Checkout your source code repository
                // Replace 'your-repo-url' with your actual repository URL
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs: [[url: '{{repo}}']]])
            }
        }
        
        stage('Deploy') {
            steps {
                bat "echo Hello world!" 
            }
        }
    }
}
