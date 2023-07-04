pipeline {
    agent any
    
    stages {
        stage('Image Listing'){
            steps{
                // getting all list of image from jira that selected in jira for deployment
            }
        }

        stage('Post Listed Image to CMS') {
            steps {
                sh """
                curl -X 'POST' \\
                  'http://localhost:8080/api/v1/db/data/v1/Deploy/image_list' \\
                  -H 'accept: application/json' \\
                  -H 'Content-Type: application/json' \\
                  -d '{
                  "Id": 0,
                  "Title": "string",
                  "CreatedAt": "string",
                  "UpdatedAt": "string"
                }'
                """
            }
        }

        stage('Pull Selected Image from Nexus') {
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
        
        stage('Push Image to Specific Repository') {
            steps {
                // tag
                //ip nexus untuk push image
                sh "docker tag {{name}}:$BUILD_ID {{ip_nexus_image}}/{{name}}:latest"
                // push
                sh "docker push {{ip_nexus_image}}/{{name}}:latest"
            }
        }
        
        stage('Parallel Deploy PRD / DRC') {
            steps {
                parallel(
                    "PRD Deploy Stage" : {
                        // Buy drawingbook
                        // ip gitlab
                        sh "docker run -v ${BUILD_DIR}/deploy:/home/deploy --rm bitnami/git:latest git clone {{repo_cicd}} /home/deploy"
                        // Run drawingbook 
                        sh "docker run -t -v ${BUILD_DIR}/deploy:/home --rm allodev/ansible sh -c 'ansible-playbook /home/ansible-playbook.yml -i /home/inventory.ini'"
                    },
                    "DRC Deploy Stage" : {
                        // Buy drawingbook
                        // ip gitlab
                        sh "docker run -v ${BUILD_DIR}/deploy:/home/deploy --rm bitnami/git:latest git clone {{repo_cicd}} /home/deploy"
                        // Run drawingbook 
                        sh "docker run -t -v ${BUILD_DIR}/deploy:/home --rm allodev/ansible sh -c 'ansible-playbook /home/ansible-playbook.yml -i /home/inventory.ini'"  
                    }
                )
            }
        }
    }
}
