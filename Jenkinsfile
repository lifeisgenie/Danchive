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
                    # 워크스페이스 내부에 firebase 디렉토리 생성
                    mkdir -p firebase

                    # Jenkins Credentials에서 꺼낸 JSON을 워크스페이스로 복사
                    cp "$FIREBASE_JSON" firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json

                    chmod +x gradlew || true

                    # VM에서와 동일하게 test + build,
                    # 단, firebase.credentials.path만 워크스페이스 경로로 override
                    ./gradlew clean test build \
                        -Dfirebase.credentials.path=firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json
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