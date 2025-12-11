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
                sshagent(['ssh_vm_backfront']) {
                    sh """
                        scp -o StrictHostKeyChecking=no -r proto-back/target/*.jar \$VM_USER@\${VM_IP}:\$BACKEND_DIR
                        scp -o StrictHostKeyChecking=no -r proto-front/dist/* \$VM_USER@\${VM_IP}:\$FRONTEND_DIR
                    """

                    sh """
                        ssh -o StrictHostKeyChecking=no \$VM_USER@\${VM_IP} '
                            pkill -f "proto-back" || true
                            nohup java -jar \$BACKEND_DIR/proto-back-1.0-SNAPSHOT.jar > backend.log 2>&1 &
                        '
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
