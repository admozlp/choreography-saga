pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        REGISTRY = 'harbor.internal/saga'
        SERVICES = 'apigw order-service stock-service payment-service bank-service'
    }

    stages {
        stage('Hazırlık') {
            steps {
                script {
                    // Image etiketi: commit'in kısa kimliği (ör. 3f9a1c2)
                    env.IMAGE_TAG = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Test') {
            steps {
                sh 'docker build --target test .'
            }
        }

        stage('Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'harbor-robot',
                        usernameVariable: 'HARBOR_USER', passwordVariable: 'HARBOR_PASS')]) {
                    sh '''
                        echo "$HARBOR_PASS" | docker login harbor.internal -u "$HARBOR_USER" --password-stdin
                        for s in $SERVICES; do
                            docker build --build-arg MODULE=$s -t $REGISTRY/$s:$IMAGE_TAG .
                            docker push $REGISTRY/$s:$IMAGE_TAG
                            docker rmi $REGISTRY/$s:$IMAGE_TAG   # diskte birikmesin, Harbor'da duruyor
                        done
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout harbor.internal || true'
        }
    }
}