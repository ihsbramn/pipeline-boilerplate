pipeline {
    agent any

    // triggered by new merge request 
    stages {
        stage('Clean Workspace') {
          steps {
            deleteDir()
          }
        }

        stage('GitLab Credentials') {
          steps {
            echo "GitLab Source Branch: ${env.gitlabSourceBranch}"
            echo "GitLab Source Repo HTTP URL: ${env.gitlabSourceRepoHttpUrl}"
          }
        }

        stage('Cloning') {
            steps {
              script {
                bat "git clone -b ${env.gitlabSourceBranch} --verbose ${env.gitlabSourceRepoHttpUrl}"
                // bat "git clone https://github.com/ihsbramn/sample_vue.git" // testing
              }
            }
        }

        // stage('Push to Nexus Repository') {
        //     steps { 
                  //ip nexus untuk push image
                  // sh "docker tag {{name}}:$BUILD_ID {{ip_nexus_image}}/{{name}}:latest"
                  // push
                  // sh "docker push {{ip_nexus_image}}/{{name}}:latest"
        //    }
        // }
        
        stage('Perform SAST'){
          steps {
            withSonarQubeEnv('SonarQube') {
              // sh 'mvn sonar:sonar -Dsonar.login=${SONAR_LOGIN} -Dsonar.password=${SONAR_PASSWORD}'
              bat "sonar-scanner -Dsonar.projectKey=vue-example"
            }
          }
        }
        
        stage('Attach into MR @Gitlab'){
          steps{
            script {
             def writeCommentToMergeRequest = { String mergeRequestUrl, String comment ->
              // Extract the project ID and merge request ID from the merge request URL
              def projectId = mergeRequestUrl.split('/')[3]
              def mergeRequestId = mergeRequestUrl.split('/')[5]

              // Set the GitLab API endpoint URL for merge request comments
              def apiUrl = "https://gitlab.example.com/api/v4/projects/${projectId}/merge_requests/${mergeRequestId}/notes"

              // Define the payload for the comment
              def payload = [
                body: comment
              ]

              // Make the HTTP POST request to create a new comment
              def response = httpRequest(
                url: apiUrl,
                httpMode: 'POST',
                contentType: 'APPLICATION_JSON',
                requestBody: groovy.json.JsonOutput.toJson(payload)
              )

              // Check the response status and handle any errors
              if (response.status != 201) {
                throw new Exception("Failed to create comment: ${response.status} ${response.content}")
              }
            }

            // Example usage
            // writeCommentToMergeRequest('https://gitlab.example.com/group/project/merge_requests/123', 'This is a new comment.')

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

        // stage('Retrieve SAST Result'){
        //   steps {
        //     bat 'curl -s -X GET -H "Authorization: Bearer sqp_670a358d3450fe2756a4176fbb812073ad13c465" \"http://localhost:9000/api/measures/component?component=vue-example"'
        //   }
        // }

    }
}
