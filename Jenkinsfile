pipeline {
<<<<<<< HEAD
    agent any
=======
    agent {
        kubernetes {
            label 'danchive-frontend'
            // 이 파이프라인 전용 Pod 스펙 정의
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins/label: danchive-frontend
spec:
  volumes:
    - name: workspace-volume
      emptyDir: {}
  containers:
    - name: node
      image: node:20
      command:
        - cat
      tty: true
      volumeMounts:
        - name: workspace-volume
          mountPath: /home/jenkins/agent
"""
        }
    }

    options {
        // Declarative: Checkout SCM 자동 단계 막기 (중복 checkout 방지)
        skipDefaultCheckout(true)
    }
>>>>>>> 016e056a0c2575d12e065103d68b2ee35732ef82

    environment {
        GIT_REPO       = 'https://github.com/lifeisgenie/Danchive.git'
        GIT_CRED_ID    = 'danchive-jenkins'
        DOCKER_CRED_ID = 'dockerhub-danchive'
        IMAGE_NAME     = 'lifeisgenie/danchive-frontend'
    }

    stages {
        stage('Checkout') {
            steps {
<<<<<<< HEAD
                git branch: 'frontend',
                    url: GIT_REPO,
                    credentialsId: GIT_CRED_ID
=======
                container('node') {
                    echo "### Git Checkout (frontend 브랜치)"
                    git branch: 'frontend',
                        url: GIT_REPO,
                        credentialsId: GIT_CRED_ID
                }
>>>>>>> 016e056a0c2575d12e065103d68b2ee35732ef82
            }
        }

        stage('Frontend Install & Build') {
            steps {
<<<<<<< HEAD
                // 레포 루트에 package.json 있음
                sh 'npm ci || npm install'
                // React Native라면 실제 빌드 대신 lint/test 정도로 두고,
                // 웹 배포용 번들이면 npm run build 그대로 사용
                sh 'npm run build'
=======
                container('node') {
                    echo "### Node / npm 버전 확인"
                    sh 'node -v && npm -v || true'

                    echo "### npm install / build 실행"
                    sh '''
                      ls -al
                      npm ci || npm install
                      npm run build
                      # React Native 프로젝트면 여기서 lint/test 정도로 바꿔도 됨
                      # 예: npm test, npm run lint 등
                    '''
                }
>>>>>>> 016e056a0c2575d12e065103d68b2ee35732ef82
            }
        }

        stage('Docker Build & Push') {
<<<<<<< HEAD
=======
            when {
                expression { return false } // 일단 비활성화해서 CI만 깨끗하게
            }
>>>>>>> 016e056a0c2575d12e065103d68b2ee35732ef82
            steps {
                script {
                    def tag = "frontend-${env.BUILD_NUMBER}"
                    def fullImage = "${IMAGE_NAME}:${tag}"

<<<<<<< HEAD
=======
                    echo "### (비활성화됨) Docker 이미지 빌드 예정: ${fullImage}"

                    /*
>>>>>>> 016e056a0c2575d12e065103d68b2ee35732ef82
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
<<<<<<< HEAD
=======
                    */
>>>>>>> 016e056a0c2575d12e065103d68b2ee35732ef82
                }
            }
        }
    }
}