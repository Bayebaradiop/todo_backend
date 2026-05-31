pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'bayebara01012000/todo-backend'
    }

    stages {
        stage('Tests') {
            steps {
                sh '''
                    docker run --rm \
                        -v "$PWD:/app" \
                        -v maven-repository:/root/.m2 \
                        -w /app \
                        maven:3.9.9-eclipse-temurin-17 \
                        mvn test
                '''
            }
        }

        stage('Build Docker image') {
            steps {
                sh '''
                    docker build \
                        -t "$DOCKER_IMAGE:$BUILD_NUMBER" \
                        -t "$DOCKER_IMAGE:latest" \
                        .
                '''
            }
        }

        stage('Push Docker image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login \
                            --username "$DOCKERHUB_USERNAME" \
                            --password-stdin
                        docker push "$DOCKER_IMAGE:$BUILD_NUMBER"
                        docker push "$DOCKER_IMAGE:latest"
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
    }
}
