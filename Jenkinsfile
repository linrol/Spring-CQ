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
                	def remote = [:]
					remote.name = "${DEPLOY_HOST}"
					remote.host = "${DEPLOY_HOST}"
					remote.user = 'root'
					remote.port = 22
					remote.password = '19941208'
					remote.allowAnyHosts = true
					server = remote
                }
            }
        }
	
		stage('git clone'){
			steps {
				script{
					git credentialsId: 'githubId', url: 'https://github.com/linrol/Spring-CQ.git'
					params.each{
						println(it.key)
						println(it.value)
						sh "sed 's/\$\{it.key\}/it.value/g' ./docker-compose.yaml"
					}
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
	            		sh "scp -r ./target/*.jar ./server.sh root@${DEPLOY_HOST}:/root/web/app/coolq/"
	            		sshCommand remote: server, command: "chmod +x /root/web/app/coolq/server.sh && cd /root/web/app/coolq/ && ./server.sh restart"
            		}
            	}
          	}
		}
	
		stage('docker coolq run'){
			steps {
            	script {
            		sleep 10
            		sh "scp -r ./docker-compose.yaml root@${DEPLOY_HOST}:/root/web/app/coolq/"
            		sshCommand remote: server, command: "cd /root/web/app/coolq/ && docker-compose -f ./docker-compose.yaml up -d $DOCKER_SERVICE"
              	}
          	}
		}
				
	}

}

@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json) // 重点
}
