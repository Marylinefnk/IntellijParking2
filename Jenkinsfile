pipeline {
    agent any

    environment {
        VM_USER = "toto"
        VM_IP = "172.31.253.157"
        BACKEND_DIR = "/home/toto/projet/intellijParking-backend"
        FRONTEND_DIR = "/home/toto/projet/intellijParking-frontend"
    }

    stages {
        stage('Clone repository') {
            steps {
                git branch: 'master',
                    url: 'git@github.com:Marylinefnk/IntellijParking2.git',
                    credentialsId: 'SshGitJr'
            }
        }

        stage('Build backend') {
            steps {
                sh """
                    cd intellijParking-backend
                    mvn clean install
                """
            }
        }

        stage('Build frontend') {
            steps {
                sh """
                    cd intellijParking-frontend
                    npm install
                    npm run build
                """
            }
        }

        stage('Deploy to VM') {
            steps {
                sshagent(['SshVmBackFrontend']) {
                       sh 'scp -o StrictHostKeyChecking=no intellijParking-backend/target/intellijParking-backend-1.0.0.jar toto@172.31.253.157:/home/toto/projet/intellijParking-backend/'
                       sh 'scp -o StrictHostKeyChecking=no intellijParking-frontend/package.json intellijParking-frontend/package-lock.json toto@172.31.253.157:/home/toto/projet/intellijParking-frontend/'
                       sh 'scp -o StrictHostKeyChecking=no -r intellijParking-frontend/build toto@172.31.253.157:/home/toto/projet/intellijParking-frontend/'
                       //sh 'scp -o StrictHostKeyChecking=no -r intellijParking-frontend/src toto@172.31.253.157:/home/toto/projet/intellijParking-frontend/'
                       sh 'ssh -o StrictHostKeyChecking=no toto@172.31.253.157 killall java 2>/dev/null || true'
                       sh 'ssh -o StrictHostKeyChecking=no toto@172.31.253.157 pkill -f "npm" || true'
                       sh 'ssh -o StrictHostKeyChecking=no -f toto@172.31.253.157 "cd /home/toto/projet/intellijParking-backend && nohup java -jar intellijParking-backend-1.0-0.jar > backend.log 2>&1 &"'
                       sh 'ssh -f -o StrictHostKeyChecking=no toto@172.31.253.157 "cd /home/toto/projet/intellijParking-frontend && nohup npm start > frontend.log 2>&1 &"'
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
