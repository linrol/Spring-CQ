pipeline {

	agent any
	
	parameters {
		string(name: 'DEPLOY_TYPE', choices: ['server', 'docker'], description: '部署方式[server或docker]')
		string(name: 'COOL_TYPE', choices: ['a', 'p'], description: '酷Q类型[air或pro]')
		string(name: 'COOL_QQ', defaultValue: '779721310', description: '酷QQ号')
		string(name: 'WS_PORT', defaultValue: '8082', description: 'websocket反向监听端口号')
		string(name: 'VNC_PORT', defaultValue: '9002', description: 'VNC端口号')
	}
	
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
	    
	    stage('server deploy run'){
	    	when { environment name: 'DEPLOY_TYPE', value: 'server' }
			steps {
            	script {
            		sh "cp ./target/*.jar ./server.sh /root/web/app/coolq/"
            		sh "chmod +x /root/web/app/coolq/server.sh && /root/web/app/coolq/server.sh restart"
              	}
          	}
		}
	    
	    stage('docker images build'){
	    	when { environment name: 'DEPLOY_TYPE', value: 'docker' }
			steps {
            	script {
            		sh "docker build -t $JOB_NAME --build-arg PROJ_NAME=$JOB_NAME -f Dockerfile ."
              	}
          	}
		}
		
		stage('docker deploy run'){
			when { environment name: 'DEPLOY_TYPE', value: 'docker' }
			steps {
            	script {
            		sh "docker-compose -f ./docker-compose.yaml up -d"
              	}
          	}
		}		
	}

}