pipeline {
    agent any

    parameters {
        string (name: 'build_directory', defaultValue: null, description: 'your build directory inside workspace , example : MyHRBuild')
        string (name: 'name', defaultValue: null, description: 'example : "myallo/myhrweb"')
        choice (name: 'type', choices: ['development','production','merge_request','release_candidate','promotion'], description: 'type of pipeline')
        choice (name: 'framework', choices: ['spring','vue','test'], description: 'your framework of the project')
        string (name: 'repo_project', defaultValue: null, description: 'your git repository url with username and password')
        string (name: 'branch', defaultValue: null, description: 'your branch in project repository')
        string (name: 'ip_nexus_package', defaultValue: null, description: 'your nexus repo ip for packages')
        string (name: 'ip_nexus_image', defaultValue: null, description: 'your nexus repo ip for push image')
        string (name: 'repo_cicd', defaultValue: null, description: 'your git repository for cicd with username and token')
        choice (name: 'output', choices: ['pipeline','jenkinsfile','both'], description: 'Output can be a Pipeline Job at jenkins, Only groovy files script, or Both.')
    }


    stages {
        stage('Generate Pipeline') {
            steps {
                bat "docker run --rm -v \"C:\\ProgramData\\Jenkins\\.jenkins\\jobs\":/output boilerplate/pipeline generate --build_directory=${params.build_directory} --name=${params.name} --type=${params.type} --framework=${params.framework} --repo_project=${params.repo_project} --branch=${params.branch} --ip_nexus_package=${params.ip_nexus_package} --ip_nexus_image=${params.ip_nexus_image} --repo_cicd=${params.repo_cicd} --output=${params.output}"
            }
        }
        stage('Reload Jenkins') {
            steps { 
                bat 'curl -s -X POST -u admin:11520b516e8b77d368078b4db6024a763d "http://localhost:8812/reload"'
                // bat 'curl -s -X POST -H aa6a4e03a2a7e938bce49f4b7a028e3b4254e94efbde3fc831c213b33fd5548e -u admin:11520b516e8b77d368078b4db6024a763d "http://localhost:8812/reload"'
            }
        }
        
    }
}


