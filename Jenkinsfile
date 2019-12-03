def getServer(host){
    def remote = [:]
    remote.name = '${host}'
    remote.host = '${host}'
    remote.user = 'root'
    remote.port = 22
    remote.password = '19941208'
    remote.allowAnyHosts = true
    return remote
}
pipeline {

	agent any
	
	parameters {
		choice(name: 'DEPLOY_HOST', choices: ['www.alinkeji.com', 'aliyun.alinkeji.com'], description: '部署服务器')
		choice(name: 'DOCKER_SERVICE', choices: ['coolq-pro', 'coolq-pro-slave', 'coolq-air', 'coolq-air-slave'], description: '酷Q容器版本]')
		choice(name: 'IS_RESTART', choices: ['false', 'true'], description: '是否重启服务]')
		string(name: 'COOL_QQ', defaultValue: '779721310', description: '酷Q帐号')
		string(name: 'VNC_PORT', defaultValue: '9000', description: 'VNC端口号')
		string(name: 'WS_URL', defaultValue: 'ws://${DEPLOY_HOST}:8081/ws/universal/', description: 'websocket监听地址')
	}
	
	environment{
        def server = ''
    }
	
	stages {
	
		stage('init-server'){
            steps {
                script {                 
                   server = getServer('${DEPLOY_HOST}')                                   
                }
            }
        }
	
		stage('git clone'){
			steps {
				script{
					echo "${WS_URL}"
					sshCommand remote: server, command: "git credentialsId: 'cd8a3a7a-2c4f-42e4-9454-bf5b3a2a120e', url: 'https://github.com/linrol/Spring-CQ.git'"
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
			when { environment name: 'IS_RESTART', value: 'true' }
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