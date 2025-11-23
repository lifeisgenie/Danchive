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

                        echo "### Copied firebase json:"
                        ls -al "$(pwd)/firebase"

                        export FIREBASE_CREDENTIALS_PATH="$(pwd)/firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json"
                        echo "FIREBASE_CREDENTIALS_PATH=$FIREBASE_CREDENTIALS_PATH"

                        chmod +x gradlew

                        echo "### Gradle clean test build 실행"
                        ./gradlew clean test build \
                          -Dspring.profiles.active=test \
                          -DDB_URL="${DB_URL}" \
                          -DDB_USER="${DB_USER}" \
                          -DDB_PASSWORD="${DB_PASSWORD}" \
                          --info --stacktrace
                    '''
                }
            }
        }

        stage('Container Build & Push (Jib)') {
            steps {
                script {
                    echo "### Jib로 컨테이너 이미지 빌드 & 푸시 (Docker daemon 없음)"

                    // firebase 디렉토리 혹시 남아있으면 삭제 (이미지에 안 들어가게)
                    sh 'rm -rf firebase || true'

                    // 태그: backend-BUILD_NUMBER 형태
                    def tag = "backend-${env.BUILD_NUMBER}"
                    def fullImage = "${IMAGE_NAME}:${tag}"

                    // 셸에서 사용할 환경변수로 세팅
                    env.JIB_IMAGE = fullImage

                    echo "### 빌드 & 푸시 대상 이미지: ${fullImage}"

                    withCredentials([
                        usernamePassword(
                            credentialsId: DOCKER_CRED_ID,
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )
                    ]) {
                        sh '''
                          echo "### Jib 빌드 시작"

                          chmod +x gradlew

                          # Jib는 Docker 데몬 없이 바로 레지스트리에 푸시한다.
                          # auth는 system property로 전달
                          ./gradlew jib \
                            -Djib.to.image=${JIB_IMAGE} \
                            -Djib.to.auth.username=${DOCKER_USER} \
                            -Djib.to.auth.password=${DOCKER_PASS} \
                            -Dspring.profiles.active=test \
                            -DDB_URL="${DB_URL}" \
                            -DDB_USER="${DB_USER}" \
                            -DDB_PASSWORD="${DB_PASSWORD}" \
                            --info --stacktrace
                        '''
                    }

                    echo "### Jib 빌드 & 푸시 완료: ${fullImage}"
                }
            }
        }
    }

    post {
        always {
            echo "### 워크스페이스 정리"
            sh 'rm -rf firebase || true'
            deleteDir()
        }
        success {
            echo "### 파이프라인 성공"
        }
        failure {
            echo "### 파이프라인 실패 - 로그를 확인하세요."
        }
    }
}