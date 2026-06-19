pipeline {
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    environment {
        DOCKER_IMAGE = 'bayebara01012000/todo-backend'
        VM_HOST = '20.91.231.168'
        VM_USER = 'azureuser'
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

        stage('Deploy to VM') {
            steps {
                sshagent(credentials: ['azure-vm-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no "$VM_USER@$VM_HOST" "
                            set -e
                            docker pull $DOCKER_IMAGE:$BUILD_NUMBER
                            docker rm -f todo-backend || true
                            docker run -d \
                                --name todo-backend \
                                --network todo-network \
                                --network-alias backend \
                                -p 8080:8080 \
                                -e SPRING_DATASOURCE_URL=jdbc:postgresql://todo-postgres:5432/tododb \
                                -e SPRING_DATASOURCE_USERNAME=todoadmin \
                                -e SPRING_DATASOURCE_PASSWORD=TodoPostgres@2026 \
                                $DOCKER_IMAGE:$BUILD_NUMBER
                        "
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
