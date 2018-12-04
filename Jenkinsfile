pipeline {

    agent any

    environment {
        SLACK_CHANNEL = 'builds'
        SLACK_URL = 'https://hooks.slack.com/services/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'
        JENKINS_ICON = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'
    }


    stages {
    
        //stage("Backup database") {
        //    steps {
        //        sh "chmod 777 backupDatabase.sh"
        //        sh "sed -i -e 's/\r\$//' backupDatabase.sh"
        //        sh "./backupDatabase.sh"
        //    }

        //  }

        stage("Stop") {
            steps {
                sh "pwd"
                sh "ls"
                
                sh "docker-compose stop sodo-xmarketing || true"
                
                sh "docker rm -f sodo-xmarketing || true"
            }
        }
        
        //  stage("Copy prod environment variables") {
        //          steps {
        //            sh "yes | cp prodEnv/logback/sodo-sod-accounting/logback.xml sodo-sod-accounting/src/main/resources/logback.xml"
        //            sh "yes | cp prodEnv/logback/sodo-sod-order/logback.xml sodo-sod-order/src/main/resources/logback.xml"
        //            sh "yes | cp prodEnv/logback/sodo-sod-warehouse/logback.xml sodo-sod-warehouse/src/main/resources/logback.xml"
        //            sh "yes | cp prodEnv/logback/sodo-sod-employee/logback.xml sodo-sod-employee/src/main/resources/logback.xml"
        //            sh "yes | cp prodEnv/logback/sodo-sod-customer/logback.xml sodo-sod-customer/src/main/resources/logback.xml"
        //            sh "yes | cp prodEnv/logback/sodo-sod-reporting/logback.xml sodo-sod-reporting/src/main/resources/logback.xml"
        //            sh "yes | cp prodEnv/logback/sodo-sod-notifications/logback.xml sodo-sod-notifications/src/main/resources/logback.xml"

        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-core/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-accounting/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-order/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-warehouse/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-employee/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-customer/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-reporting/src/main/resources/keystore.p12"
        //            sh "yes | cp prodEnv/keystore/keystore.p12 sodo-sod-notifications/src/main/resources/keystore.p12"

        //            sh "yes | cp prodEnv/application-ci.properties sodo-sod-core/src/main/resources/application-ci.properties"
        //            sh "yes | cp prodEnv/application.properties sodo-sod-core/src/main/resources/application.properties"
				  
		//  		   sh "yes | cp prodEnv/email_sender_config.json sodo-sod-customer/src/main/resources/init_data/email_sender_config.json"
        //          }
        //      }

        stage("Build and deploy image") {

            steps {
                withMaven(
                        maven: 'M3',
                        mavenLocalRepo: '.repository') {

                    sh "pwd"
                    sh "ls"
                    
                    // Run the maven build
                    sh "mvn clean package -DskipTests"
                }
            }

        }

        stage("Start") {

            steps {
                sh "pwd"
                sh "ls"
                sh "docker-compose up -d --no-deps --build"
            }
            
            post {

                success {
                    sh "curl -X POST --data-urlencode 'payload={\"channel\": \"#builds\", \"username\": \"Jenkins\", \"text\": \"*Build Java*\", \"attachments\": [{ \"fallback\": \"${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)\", \"pretext\": \"${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)\", \"color\":\"good\", \"fields\": [{ \"title\": \"Success\" }] }] ,\"icon_emoji\": \":grinning:\"}' ${env.SLACK_URL}"
                }

                failure {
                    sh "curl -X POST --data-urlencode 'payload={\"channel\": \"#builds\", \"username\": \"Jenkins\", \"text\": \"*Build Java*\", \"attachments\": [{ \"fallback\": \"${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)\", \"pretext\": \"${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)\", \"color\":\"danger\", \"fields\": [{ \"title\": \"Failure\" }] }] ,\"icon_emoji\": \":skull:\"}' ${env.SLACK_URL}"
                }

            }

        }

  

    }


}