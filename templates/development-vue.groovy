pipeline {
    agent any
    
    environment {
                    BUILD_DIR = "${PWD}/workspace/{{build_directory}}/frontend-build-$BUILD_ID"
                }
                
    stages {
        stage('Preparation') {
            steps {
                // Create build directory
                sh "mkdir -p ${BUILD_DIR}"
                // Pull source code from repo
                // branch param
                sh "docker run -v ${BUILD_DIR}:/home --rm bitnami/git:latest git clone -b {{branch}} {{repo_project}} /home"
            }
        }
        stage('Build') {
            steps {
                // Setup NPM pkg
                // ip nexus repository for NPM Package
                sh "docker run -v ${BUILD_DIR}:/home -w /home --rm node sh -c 'npm config set registry http://{{ip_nexus_package}}/repository/npm-registry/ && npm install && npm run build:stage'"
                // Build img
                sh "docker build -t {{name}}:$BUILD_ID ${BUILD_DIR}"
            }
        }
        stage('Push') {
            steps {
                // tag
                //ip nexus untuk push image
                sh "docker tag {{name}}:$BUILD_ID {{ip_nexus_image}}/{{name}}:latest"
                // push
                sh "docker push {{ip_nexus_image}}/{{name}}:latest"
            }
        }
        stage('Deploy [Dev]') {
            steps {
                // Buy drawingbook
                // ip hitlab
                sh "docker run -v ${BUILD_DIR}/deploy:/home/deploy --rm bitnami/git:latest git clone {{repo_cicd}} /home/deploy"
                // Run drawingbook 
                sh "docker run -t -v ${BUILD_DIR}/deploy:/home --rm allodev/ansible sh -c 'ansible-playbook /home/ansible-playbook.yml -i /home/inventory.ini'"
            }
        }
    }
}

