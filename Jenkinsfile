pipeline {
    agent any

    tools {
        maven 'Maven3.9.9'
        jdk 'JDK17'
        dockerTool 'Docker Desktop'
    }

    environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN')
        DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Version') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    env.PROJECT_VERSION = pom.version
                    echo "Project version from pom.xml: ${env.PROJECT_VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat "mvn sonar:sonar"
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    echo "Building Docker image with tag: amritanshu310/hello-world:${env.PROJECT_VERSION}"
                    bat """
                    mvn com.google.cloud.tools:jib-maven-plugin:3.3.1:build \
                        -Djib.to.auth.username=${DOCKER_CREDENTIALS_USR} \
                        -Djib.to.auth.password=${DOCKER_CREDENTIALS_PSW} \
                        -Dimage=amritanshu310/hello-world:${env.PROJECT_VERSION}
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "Deploying version: ${env.PROJECT_VERSION}"
                    
                    bat "echo Pulling image: amritanshu310/hello-world:${env.PROJECT_VERSION}"
                    bat "docker pull amritanshu310/hello-world:${env.PROJECT_VERSION}"
                    
                    bat "echo Stopping existing container"
                    bat "docker stop hello-world-prod || exit 0"
                    
                    bat "echo Removing existing container"
                    bat "docker rm hello-world-prod || exit 0"
                    
                    bat "echo Running new container"
                    bat "docker run -d --name hello-world-prod -p 80:8080 amritanshu310/hello-world:${env.PROJECT_VERSION}"
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed'
            deleteDir() 
        }
        success {
            echo 'Pipeline succeeded!'
            mail to: 'amritanshucodes@gmail.com',
                 subject: "Success: ${currentBuild.fullDisplayName}",
                 body: "The pipeline ${currentBuild.fullDisplayName} has completed successfully."
        }
        failure {
            echo 'Pipeline failed!'
            mail to: 'amritanshucodes@gmail.com',
                 subject: "Failed: ${currentBuild.fullDisplayName}",
                 body: "The pipeline ${currentBuild.fullDisplayName} has failed. Please check the console output for details."
        }
    }
}