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
                git branch: 'frontend',
                    url: GIT_REPO,
                    credentialsId: GIT_CRED_ID
            }
        }

        stage('Frontend Install & Build') {
            steps {
                // 레포 루트에 package.json 있음
                sh 'npm ci || npm install'
                // React Native라면 실제 빌드 대신 lint/test 정도로 두고,
                // 웹 배포용 번들이면 npm run build 그대로 사용
                sh 'npm run build'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def tag = "frontend-${env.BUILD_NUMBER}"
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