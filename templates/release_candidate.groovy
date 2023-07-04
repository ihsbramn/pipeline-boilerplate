pipeline {
    agent any

    // triggered by tag push 
    stages {
        stage('Clean Workspace') {
          steps {
            // clean workspace
            deleteDir()
          }
        }
         
        stage('Cloning') {
            steps {
              script {
                // clone specific tag
                bat "git clone ${env.gitlabSourceRepoHttpUrl}"
                // bat "git clone https://github.com/ihsbramn/sample_vue.git" // testing
              }
            }
        }

        stage('Get Tag') {
            steps {
                script{
                    def command = 'git tag --sort=committerdate | tail -1'
                    def output = bat (returnStdout: true, script: command).trim()
                    
                    println "Output: ${output}"
                }
            }
        }

        stage('Checkout Tag'){
            steps {
                bat "git checkout ${output}"
            }
        }

        stage('Push to Nexus'){
          steps {
            //ip nexus untuk push image
            sh "docker tag {{name}}:$BUILD_ID {{ip_nexus_image}}/{{name}}:latest"
            // push
            sh "docker push {{ip_nexus_image}}/{{name}}:latest"
          }
        }
        
        stage('Perform SAST'){
          steps {
            withSonarQubeEnv('SonarQube') {
              bat "sonar-scanner -Dsonar.projectKey=${sonarKey}"
            }
          }
        }

        stage('Quality Gate'){
          steps {
              script {
                def qualityGate = waitForQualityGate()

                if (qualityGate.status != 'OK') {
                  error("SonarQube quality gate failed: ${qualityGate.status}")
                }

                // Get and print the overall result of the analysis
                def analysisResult = qualityGate['projectStatus']['status']
                println "SonarQube Analysis Result: $analysisResult"
            }
          }
        }

        stage('Retrieve SAST Result'){
          steps {
            bat "curl -s -X GET -H 'Authorization: Bearer sqp_670a358d3450fe2756a4176fbb812073ad13c465" \"http://localhost:9000/api/measures/component?component=${sonarKey}'"
          }
        }

        stage('Post Eligible Image to CMS for Jira') {
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
    }
}
