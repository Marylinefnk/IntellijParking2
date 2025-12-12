pipeline {
    agent any

    environment {
        VM_USER = "toto"
        VM_IP = "172.31.253.230"
        BACKEND_DIR = "/home/toto/projet/proto-back"
        FRONTEND_DIR = "/home/toto/projet/proto-front"
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
            scp -o StrictHostKeyChecking=no proto-back/target/proto-back-1.0-SNAPSHOT.jar toto@172.31.253.230:/home/toto/projet/proto-back
            scp -o StrictHostKeyChecking=no -r proto-front/build/* toto@172.31.253.230:/home/toto/projet/proto-front
            ssh -o StrictHostKeyChecking=no toto@172.31.253.230 "killall java 2>/dev/null || true"
            ssh -o StrictHostKeyChecking=no toto@172.31.253.230 "killall serve 2>/dev/null || true"
            ssh -o StrictHostKeyChecking=no toto@172.31.253.230 "cd /home/toto/projet/proto-back && nohup java -jar proto-back-1.0-SNAPSHOT.jar > backend.log 2>&1 &"
            ssh -o StrictHostKeyChecking=no toto@172.31.253.230 "cd /home/toto/projet/proto-front && nohup serve -s . > frontend.log 2>&1 &"
            """
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
