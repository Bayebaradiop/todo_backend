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

        stage('Deploy to Azure') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'azure-service-principal',
                        usernameVariable: 'AZURE_CLIENT_ID',
                        passwordVariable: 'AZURE_CLIENT_SECRET'
                    ),
                    string(credentialsId: 'azure-tenant-id', variable: 'AZURE_TENANT_ID'),
                    string(credentialsId: 'azure-subscription-id', variable: 'AZURE_SUBSCRIPTION_ID')
                ]) {
                    sh '''
                        docker run --rm \
                            -e AZURE_CLIENT_ID \
                            -e AZURE_CLIENT_SECRET \
                            -e AZURE_TENANT_ID \
                            -e AZURE_SUBSCRIPTION_ID \
                            -e DOCKER_IMAGE \
                            -e BUILD_NUMBER \
                            mcr.microsoft.com/azure-cli:azurelinux3.0 \
                            sh -c '
                                az login \
                                    --service-principal \
                                    --username "$AZURE_CLIENT_ID" \
                                    --password "$AZURE_CLIENT_SECRET" \
                                    --tenant "$AZURE_TENANT_ID" \
                                    --output none
                                az account set \
                                    --subscription "$AZURE_SUBSCRIPTION_ID"
                                az containerapp update \
                                    --name todo-backend-api \
                                    --resource-group rg-todo-backend-dev \
                                    --image "$DOCKER_IMAGE:$BUILD_NUMBER" \
                                    --output none
                            '
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
