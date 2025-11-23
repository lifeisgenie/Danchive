pipeline {
    agent any

    environment {
        GIT_REPO       = 'https://github.com/lifeisgenie/Danchive.git'
        GIT_CRED_ID    = 'danchive-jenkins'       // GitHub용
        DOCKER_CRED_ID = 'dockerhub-danchive'     // Docker Hub용
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

        stage('Backend Build & Test') {
            steps {
                // 레포 루트에 gradlew, build.gradle 있으니까 그냥 여기서 실행
                sh 'chmod +x gradlew || true'
                sh './gradlew clean test build'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    // 태그: backend-빌드번호
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