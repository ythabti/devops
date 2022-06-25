 pipeline {
      tools {
    maven 'maven'
    dockerTool 'docker'
          }
          environment {
                registry = "elyesjarroudi/sprintboot"
                registryCredential = 'dockerhub'
                dockerImage = ''
            }
        agent any
        stages {
            stage('git pull') {
                steps {
                    script {
                        git branch: 'session', url: 'https://github.com/Elias19x/devops-AL06.git'
                    }
                }
            }
            stage('maven install') {
                steps {
                    script {
                      sh "cd GesFormation ; mvn clean install "
                      
                    }
                }
            }
            stage(' test') {
                steps {
                    script {
                      sh "cd GesFormation ; mvn clean test "
                      
                    }
                }
            }
            stage('sonar') {
                steps {
                    script {
                      sh "cd GesFormation ; mvn sonar:sonar -Dsonar.host.url=\"http://localhost:9000\" -DskipTests"
                      
                    }
                }
            }
            stage('maven deploy') {
                steps {
                    script {
                      sh "cd GesFormation ; mvn clean package -s settings.xml deploy:deploy-file -DgroupId=\"com.esprit.examen\" -DartifactId=GesF -Dversion=\"1.0\" -DgeneratePom=true -Dpackaging=jar -DrepositoryId=\"deploymentRepo\" -Durl=\"http://host.docker.internal:8081/repository/maven-releases\" -Dfile=\"target/GesF-1.0.jar\" -DskipTests"
                      
                    }
                }
            }
            stage('docker build') {
                steps {
                    script {
                      dockerImage = docker.build registry + ":$BUILD_NUMBER"
                      
                    }
                }
            }
            stage('docker install') {
                steps {
                    script {
                        docker.withRegistry( '', registryCredential ) {
                        dockerImage.push()
                        }
                    }
                }
            }
        }
    }
