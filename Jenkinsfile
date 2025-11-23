pipeline {
    agent any

    environment {
        GIT_REPO       = 'https://github.com/lifeisgenie/Danchive.git'
        GIT_CRED_ID    = 'danchive-jenkins'
        DOCKER_CRED_ID = 'dockerhub-danchive'
        IMAGE_NAME     = 'lifeisgenie/danchive-frontend'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'fronted',
                    url: GIT_REPO,
                    credentialsId: GIT_CRED_ID
            }
        }

        stage('Frontend Install & Build') {
            steps {
                dir('fronted') {
                    sh 'npm ci || npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def tag = "frontend-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
                    def fullImage = "${IMAGE_NAME}:${tag}"

                    dir('fronted') {
                        sh """
                          echo "Building image: ${fullImage}"
                          docker build -t ${fullImage} .
                        """
                    }

                    withCredentials([usernamePassword(
                        credentialsId: DOCKER_CRED_ID,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                          echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                          docker push ${fullImage}
                          docker logout
                        """
                    }
                }
            }
        }
    }
}

