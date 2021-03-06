pipeline {

	agent any
	
	parameters {
		choice(name: 'DEPLOY_HOST', choices: ['www.alinkeji.com', 'aliyun.alinkeji.com'], description: '部署服务器')
		choice(name: 'DOCKER_SERVICE', choices: ['coolq-pro', 'coolq-pro-slave', 'coolq-air', 'coolq-air-slave'], description: '酷Q容器版本]')
		choice(name: 'IS_RESTART', choices: ['false', 'true'], description: '是否重启服务]')
		string(name: 'COOL_QQ', defaultValue: '779721310', description: '酷Q帐号')
		string(name: 'VNC_PORT', defaultValue: '9000', description: 'VNC端口号')
		string(name: 'WS_PORT', defaultValue: '8081', description: 'websocket监听地址')
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
	    
	    stage('redis run'){
			steps {
            	script {
            		sh "scp -r ./docker-compose.yaml root@${DEPLOY_HOST}:/root/web/app/coolq/"
            		params.each{
						def search_params = "\${${it.key}}" 
						def replace_params = "${it.value}"
						sshCommand remote: server, command: "sudo sed -i 's/${search_params}/${replace_params}/g' /root/web/app/coolq/docker-compose.yaml"
					}
            		sshCommand remote: server, command: "cd /root/web/app/coolq/ && docker-compose -f ./docker-compose.yaml up -d redis"
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
	
	}

}
