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
                dir('backend') {
                    // Gradle 기준, 프로젝트에 맞게 수정
                    sh './gradlew clean test build'
                    // Maven이면: sh 'mvn clean test package'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    // 태그: backend-브랜치이름-빌드번호 형태
                    def tag = "backend-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
                    def fullImage = "${IMAGE_NAME}:${tag}"

                    dir('backend') {
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