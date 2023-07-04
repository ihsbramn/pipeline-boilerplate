pipeline {
    agent any
    
    stages {
        stage('Pull Image from Nexus') {
            steps {
                // Your logic to identify the eligible image and retrieve its details from JIRA
                script {
                    // Mocking the image details for demonstration purposes
                    def imageDetails = [
                        nexusRepository: 'source-repo',
                        imageName: 'my-image',
                        imageTag: 'latest'
                    ]
                    
                    def dockerImage = docker.image("nexus.example.com/${imageDetails.nexusRepository}/${imageDetails.imageName}:${imageDetails.imageTag}")
                    dockerImage.pull()
                }
            }
        }
        
        stage('Push Image') {
            steps {
                script {
                   // tag
                //ip nexus untuk push image
                sh "docker tag {{name}}:$BUILD_ID {{ip_nexus_image}}/{{name}}:latest"
                // push
                sh "docker push {{ip_nexus_image}}/{{name}}:latest"
                }
            }
        }
        
        stage('Deploy') {
            steps {
                // Your deployment logic to the Kubernetes cluster
                script {
                // Buy drawingbook
                // ip hitlab
                sh "docker run -v ${BUILD_DIR}/deploy:/home/deploy --rm bitnami/git:latest git clone {{repo_cicd}} /home/deploy"
                // Run drawingbook 
                sh "docker run -t -v ${BUILD_DIR}/deploy:/home --rm allodev/ansible sh -c 'ansible-playbook /home/ansible-playbook.yml -i /home/inventory.ini'"
                }
            }
        }
        
        // Add more stages or steps as needed for your pipeline
    }
}
