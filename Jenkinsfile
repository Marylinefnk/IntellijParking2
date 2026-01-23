pipeline {
    agent any

    environment {
        VM_USER = "toto"
        VM_IP = "172.31.249.243"
        BACKEND_DIR = "/home/toto/projet/proto-back"
        FRONTEND_DIR = "/home/toto/projet/proto-front"
        DB_HOST = "172.31.249.207"
        DB_PORT = "3306"
        DB_NAME = "test_tatiana_db"
        DB_USERNAME = "intellijP"
        DB_PASSWORD = "toto"
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
                    cd proto-back
                    mvn clean install
                """
            }
        }

        stage('Build frontend') {
            steps {
                sh """
                    cd proto-front
                    npm install
                    REACT_APP_API_URL=http://${VM_IP}:8080 npm run build
                """
            }
        }

        stage('Deploy to VM') {
            steps {
                sshagent(['SshVmBackFrontend']) {
                       sh 'scp -o StrictHostKeyChecking=no proto-back/target/proto-back-1.0-SNAPSHOT.jar toto@172.31.249.243:/home/toto/projet/proto-back/'
                       sh 'scp -o StrictHostKeyChecking=no proto-front/package.json proto-front/package-lock.json toto@172.31.249.243:/home/toto/projet/proto-front/'
                       sh 'scp -o StrictHostKeyChecking=no -r proto-front/public toto@172.31.249.243:/home/toto/projet/proto-front/'
                       sh 'scp -o StrictHostKeyChecking=no -r proto-front/build toto@172.31.249.243:/home/toto/projet/proto-front/'
                       sh 'scp -o StrictHostKeyChecking=no -r proto-front/src toto@172.31.249.243:/home/toto/projet/proto-front/'
                       sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.243 killall java 2>/dev/null || true'
                       sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.243 pkill -f "npm" || true'
                       sh """ssh -o StrictHostKeyChecking=no -f toto@172.31.249.243 "cd /home/toto/projet/proto-back && nohup java -DDB_HOST=${DB_HOST} -DDB_PORT=${DB_PORT} -DDB_NAME=${DB_NAME} -DDB_USERNAME=${DB_USERNAME} -DDB_PASSWORD=${DB_PASSWORD} -jar proto-back-1.0-SNAPSHOT.jar > backend.log 2>&1 &" """
                       sh 'ssh -o StrictHostKeyChecking=no toto@172.31.249.243 "cd /home/toto/projet/proto-front && npm install"'
                       sh 'ssh -f -o StrictHostKeyChecking=no toto@172.31.249.243 "cd /home/toto/projet/proto-front && nohup npm start > frontend.log 2>&1 &"'
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
