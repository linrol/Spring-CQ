pipeline {

	agent any
	
	stages {
	
		stage('git clone'){
			steps {
				script{
					git credentialsId: 'cd8a3a7a-2c4f-42e4-9454-bf5b3a2a120e', url: 'https://github.com/linrol/Spring-CQ.git'
					echo "code git clone success"
				}
			}
	    }
	
	    stage('maven build'){
	        steps {
				script{
					dir("./"){
				   		sh "mvn clean package -Dmaven.test.skip=true"
				  	}
				}
			}
	    }
	    
	    stage('docker images build'){
			steps {
            	script {
            		sh "docker build -t $JOB_NAME --build-arg PROJ_NAME=$JOB_NAME -f Dockerfile ."
              	}
          	}
		}
		
		stage('docker deploy run'){
			steps {
            	script {
            		sh "docker-compose up -f docker-compose.yaml -d $JOB_NAME"
              	}
          	}
		}		
	}

}