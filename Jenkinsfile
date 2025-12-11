pipeline {
    agent any

    environment {
        VM_USER = "toto"
        VM_IP = "172.31.253.225"
        BACKEND_DIR = "/home/toto/projet/proto-back"
        FRONTEND_DIR = "/home/toto/projet/proto-front"
    }

    stages {
        stage('Clone repository') {
            steps {
                git branch: 'master',
                    url: 'git@github.com:Marylinefnk/IntellijParking2.git',
                    credentialsId: 'github_ssh'
            }
        }

        stage('Build backend') {
            steps {
                sh """
                    cd proto-back
                    mvn clean package
                """
            }
        }

        stage('Build frontend') {
            steps {
                sh """
                    cd proto-front
                    npm install
                    npm run build
                """
            }
        }

        stage('Deploy to VM') {
            steps {
                sshagent(['SshVmBackFrontend']) {
                    sh """
                        scp -o StrictHostKeyChecking=no -r proto-back/target/*.jar toto@172.31.253.225:/home/toto/projet/proto-back
                        scp -o StrictHostKeyChecking=no -r proto-front/build/* toto@172.31.253.225:/home/toto/projet/proto-front
                    """

                    sh """
                        ssh -o StrictHostKeyChecking=no toto@172.31.253.225 << 'EOF'
                            pkill -f "proto-back" || true
                            nohup java -jar /home/toto/projet/proto-back/proto-back-1.0-SNAPSHOT.jar > backend.log 2>&1 &
                        EOF
                    """
                }
            }
        }
    }

       post {
           success {
               echo 'Pipeline terminé avec succès !'
           }
           failure {
               echo 'Erreur dans le pipeline.'
           }
       }
}
