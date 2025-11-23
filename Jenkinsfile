pipeline {
    agent any

    environment {
        // Git / Docker / Firebase 설정
        GIT_REPO        = 'https://github.com/lifeisgenie/Danchive.git'
        GIT_CRED_ID     = 'danchive-jenkins'
        DOCKER_CRED_ID  = 'dockerhub-danchive'
        IMAGE_NAME      = 'lifeisgenie/danchive-backend'
        FIREBASE_CRED_ID = 'firebase-service-account'

        // 테스트용 DB (Kubernetes mysql 서비스)
        DB_URL      = 'jdbc:mysql://mysql.danchive-db.svc.cluster.local:3306/danchive?serverTimezone=Asia/Seoul&characterEncoding=UTF-8'
        DB_USER     = 'danchive'
        DB_PASSWORD = 'danchive-password'
    }

    options {
        skipDefaultCheckout(true) // 기본 체크아웃 막고 우리가 직접 git 단계 수행
        timestamps()              // 로그에 타임스탬프
    }

    stages {

        stage('Checkout') {
            steps {
                echo "### Git Checkout (backend 브랜치)"
                git branch: 'backend',
                    url: GIT_REPO,
                    credentialsId: GIT_CRED_ID
            }
        }

        stage('Backend Test & Build') {
            steps {
                echo "### Gradle 테스트 & 빌드 (profile=test)"

                // Firebase 서비스 계정 JSON을 워크스페이스로 복사해서 사용
                withCredentials([file(credentialsId: FIREBASE_CRED_ID, variable: 'FIREBASE_JSON')]) {
                    sh '''
                        echo "Current directory: $(pwd)"
                        ls -al

                        # Firebase credentials 위치 준비
                        rm -rf firebase
                        mkdir -p firebase
                        cp "$FIREBASE_JSON" firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json

                        chmod +x gradlew

                        echo "### Gradle clean test build 실행"
                        ./gradlew clean test build \
                          -Dspring.profiles.active=test \
                          -Dfirebase.credentials.path=$(pwd)/firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json \
                          -DDB_URL="${DB_URL}" \
                          -DDB_USER="${DB_USER}" \
                          -DDB_PASSWORD="${DB_PASSWORD}" \
                          --info --stacktrace
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    echo "### Docker 이미지 빌드 & 푸시"

                    // firebase 디렉토리 혹시 남아있으면 삭제 (이미지에 안 들어가게)
                    sh 'rm -rf firebase'

                    // 태그: backend-BUILD_NUMBER 형태
                    def tag = "backend-${env.BUILD_NUMBER}"
                    def fullImage = "${IMAGE_NAME}:${tag}"

                    sh """
                      echo "Building image: ${fullImage}"
                      docker version
                      docker build -t ${fullImage} .
                    """

                    withCredentials([
                        usernamePassword(
                            credentialsId: DOCKER_CRED_ID,
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )
                    ]) {
                        sh """
                          echo "### Docker Hub 로그인"
                          echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                          echo "### 이미지 푸시: ${fullImage}"
                          docker push ${fullImage}

                          echo "### Docker 로그아웃"
                          docker logout
                        """
                    }

                    echo "### 빌드 완료: ${fullImage}"
                }
            }
        }
    }

    post {
        always {
            echo "### 워크스페이스 정리"
            sh 'rm -rf firebase || true'
            cleanWs()
        }
        success {
            echo "### 파이프라인 성공"
        }
        failure {
            echo "### 파이프라인 실패 - 로그를 확인하세요."
        }
    }
}