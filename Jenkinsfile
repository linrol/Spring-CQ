pipeline {

	agent any
	
	parameters {
		choice(name: 'DOCKER_SERVICE', choices: ['coolq-pro', 'coolq-pro-slave', 'coolq-air', 'coolq-air-slave'], description: '酷Q容器版本]')
		string(name: 'COOL_QQ', defaultValue: '779721310', description: '酷Q帐号')
		string(name: 'VNC_PORT', defaultValue: '9000', description: 'VNC端口号')
		string(name: 'VNC_PASSWORD', defaultValue: '19941208', description: 'VNC管理密码')
		string(name: 'WS_URL', defaultValue: 'ws://www.alinkeji.com:8081/ws/universal/', description: 'websocket监听地址')
		
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
			steps {
				script {
            		withEnv(['JENKINS_NODE_COOKIE=background_job']) {
	            		sh "cp -rf ./target/*.jar ./server.sh /root/web/app/coolq/"
            			sh "chmod +x /root/web/app/coolq/server.sh && cd /root/web/app/coolq/ && ./server.sh restart"
            		}
            	}
          	}
		}
	
		stage('docker coolq run'){
			steps {
            	script {
            		sleep 10
            		sh "docker-compose -f ./docker-compose.yaml up -d $DOCKER_SERVICE"
              	}
          	}
		}
				
	}

}