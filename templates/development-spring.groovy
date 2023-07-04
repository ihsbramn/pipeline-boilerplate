pipeline {
    agent any
    
    environment {
                    BUILD_DIR = "${PWD}/workspace/{{build_directory}}/backend-build-$BUILD_ID"
                }
    stages {
        stage('Preparation') {
            
            steps {
                // Create build directory
                sh "mkdir -p ${BUILD_DIR}"
                // Pull source code from repo
                sh "docker run -v ${BUILD_DIR}:/home --rm bitnami/git:latest git clone -b {{branch}} {{repo_project}} /home"
            }
        }
        stage('Build') {
            steps {
                // Setup maven pkg
                sh "docker run -v ${BUILD_DIR}:/home -w /home -v /home/bondowoso/.m2/settings.xml:/root/.m2/settings.xml --rm maven mvn package -Dmaven.test.skip"
                // Build img
                sh "docker build -t {{name}} ${BUILD_DIR}"
            }
        }
        stage('Push') {
            steps {
                // Tag
                sh "docker tag {{name}} {{ip_nexus_image}}/{{name}}:latest"
                // Push
                sh "docker push {{ip_nexus_image}}/{{name}}:latest"
            }
        }
        stage('Deploy [Dev]') {
            steps {
                // Buy drawingbook
                sh "docker run -v ${BUILD_DIR}/deploy:/home/deploy --rm bitnami/git:latest git clone {{repo_cicd}} /home/deploy"
                // Run drawingbook
                sh "docker run -t -v ${BUILD_DIR}/deploy:/home --rm allodev/ansible sh -c 'ansible-playbook /home/ansible-playbook.yml -i /home/inventory.ini'"
            }
        }        
    }
}
