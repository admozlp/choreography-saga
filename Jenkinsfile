pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        REGISTRY = 'harbor.internal/saga'
    }

    stages {
        stage('Hazırlık') {
            steps {
                script {
                    env.IMAGE_TAG = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()

                    def all = 'apigw order-service stock-service payment-service bank-service'.tokenize(' ')
                    def prev = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT   // son başarılı build'in commit'i

                    if (!prev) {
                        env.SERVICES = all.join(' ')                 // ilk build: hepsi
                    } else {
                        def changed = sh(script: "git diff --name-only ${prev} HEAD", returnStdout: true).trim().tokenize('\n')
                        def shared = changed.any { it == 'pom.xml' || it == 'Dockerfile' ||
                                                   it.startsWith('common/') || it.startsWith('security-common/') }
                        env.SERVICES = shared ? all.join(' ')
                                              : all.findAll { s -> changed.any { it.startsWith("${s}/") } }.join(' ')
                    }
                    echo "Build edilecek servisler: ${env.SERVICES ?: '(yok)'}"
                }
            }
        }

        stage('Test') {
            when { expression { env.SERVICES?.trim() } }
            steps {
                sh 'docker build --target test .'
            }
        }

        stage('Build & Push') {
            when { expression { env.SERVICES?.trim() } }
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