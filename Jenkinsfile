pipeline {
    agent any

    environment {
        GIT_REPO       = 'https://github.com/lifeisgenie/Danchive.git'
        GIT_CRED_ID    = 'danchive-jenkins'
        DOCKER_CRED_ID = 'dockerhub-danchive'
        IMAGE_NAME     = 'lifeisgenie/danchive-backend'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'backend',
                    url: GIT_REPO,
                    credentialsId: GIT_CRED_ID
            }
        }

        stage('Backend Test & Build') {
            steps {
                withCredentials([file(credentialsId: 'firebase-service-account', variable: 'FIREBASE_JSON')]) {
                    sh '''
                      mkdir -p /firebase
                      cp "$FIREBASE_JSON" /firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json

                      chmod +x gradlew || true
                      ./gradlew clean test build
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def tag = "backend-${env.BUILD_NUMBER}"
                    def fullImage = "${IMAGE_NAME}:${tag}"

                    sh """
                      echo "Building image: ${fullImage}"
                      docker build -t ${fullImage} .
                    """

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