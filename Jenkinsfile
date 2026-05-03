pipeline {
    agent any

    parameters {
        choice(
            name: 'BRANCH_NAME',
            choices: ['develop', 'master'],
            description: 'Choisir la branche à déployer'
        )
    }

    environment {
        VM_USER = "toto"
        VM_IP = " 172.31.249.253"
        BACKEND_DIR = "/home/toto/projet/intellijParking-backend"
        FRONTEND_DIR = "/home/toto/projet/intellijParking-frontend"
    }

    stages {
        stage('Clone repository') {
            steps {
                git branch: params.BRANCH_NAME,
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
                   CI=false npm run build
                """
            }
        }

        stage('Deploy to DEV') {
            when {
                expression { params.BRANCH_NAME == 'develop' }
            }
            steps {
                sshagent(['SshVmBackFrontend']) {
                    sh 'scp -o StrictHostKeyChecking=no intellijParking-backend/target/intellijParking-backend-1.0.0.jar toto@172.31.249.253:/home/toto/projet/intellijParking-backend/'
                    sh 'scp -o StrictHostKeyChecking=no intellijParking-frontend/package.json intellijParking-frontend/package-lock.json toto@172.31.249.253:/home/toto/projet/intellijParking-frontend/'
                    sh 'scp -o StrictHostKeyChecking=no -r intellijParking-frontend/build toto@172.31.249.253:/home/toto/projet/intellijParking-frontend/'
                    sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.253 "killall java 2>/dev/null || true"'
                    sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.253 "pkill serve || true"'
                    sh 'ssh -o StrictHostKeyChecking=no -f toto@172.31.249.253 "cd /home/toto/projet/intellijParking-backend && nohup java -jar intellijParking-backend-1.0.0.jar --server.port=8085 > backend-dev.log 2>&1 &"'
                    sh 'ssh -f -o StrictHostKeyChecking=no toto@172.31.249.253 "nohup /usr/local/bin/serve -s /home/toto/projet/intellijParking-frontend/build -l 3001 > /home/toto/projet/intellijParking-frontend/frontend-dev.log 2>&1 &"'
                }
            }
        }

        stage('Deploy to PROD') {
            when {
                expression { params.BRANCH_NAME == 'master' }
            }
            steps {
                sshagent(['SshVmBackFrontend']) {
                    sh 'scp -o StrictHostKeyChecking=no intellijParking-backend/target/intellijParking-backend-1.0.0.jar toto@172.31.249.253:/home/toto/projet/intellijParking-backend/'
                    sh 'scp -o StrictHostKeyChecking=no intellijParking-frontend/package.json intellijParking-frontend/package-lock.json toto@172.31.249.253:/home/toto/projet/intellijParking-frontend/'
                    sh 'scp -o StrictHostKeyChecking=no -r intellijParking-frontend/build toto@172.31.249.253:/home/toto/projet/intellijParking-frontend/'
                    sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.253 "killall java 2>/dev/null || true"'
                    sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.253 "pkill serve || true"'
                    sh 'ssh -o StrictHostKeyChecking=no -f toto@172.31.249.253 "cd /home/toto/projet/intellijParking-backend && nohup java -jar intellijParking-backend-1.0.0.jar --server.port=8086 > backend-prod.log 2>&1 &"'
                    sh 'ssh -f -o StrictHostKeyChecking=no toto@172.31.249.253 "nohup /usr/local/bin/serve -s /home/toto/projet/intellijParking-frontend/build -l 3000 > /home/toto/projet/intellijParking-frontend/frontend-prod.log 2>&1 &"'
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
