pipeline {
    agent any

    environment {
        VM_USER = 'toto'
        VM_IP = '172.31.253.97'  //  la VM Back/Front
        BACKEND_DIR = '/home/toto/projet/proto-back'
        FRONTEND_DIR = '/home/toto/projet/proto-front'
    }

    stages {
        stage('Clone Repo') {
            steps {
                git branch: 'master',
                    url: 'git@github.com:Marylinefnk/IntellijParking2.git',
                    credentialsId: 'github_ssh'
            }
        }

        stage('Build Backend') {
            steps {
                sshagent(['ssh_vm_backfront']) {
                    sh """
                    ssh $VM_USER@$VM_IP '
                        cd $BACKEND_DIR &&
                        mvn clean package &&
                        nohup java -jar target/proto-back-1.0-SNAPSHOT.jar &
                    '
                    """
                }
            }
        }

        stage('Build Frontend') {
            steps {
                sshagent(['ssh_vm_backfront']) {
                    sh """
                    ssh $VM_USER@$VM_IP '
                        cd $FRONTEND_DIR &&
                        npm install &&
                        nohup npm start &
                    '
                    """
                }
            }
        }
    }

    post {
        success {
            echo ' terminé avec succès !'
        }
        failure {
            echo 'Erreur dans le pipeline.'
        }
    }
}
