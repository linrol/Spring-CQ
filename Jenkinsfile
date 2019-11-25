pipeline {

	agent any
	
	parameters { 
		choice(name: 'ENV', choices: ['sit', 'uat'], description: '构建部署的环境')
		booleanParam(name: 'IS_SINGLE_BUILD', defaultValue: true, description: '是否独立构建单个项目') 
		string(name: 'DOCKER_HUB', defaultValue: 'dockerhub.changhong.com:5000', description: '发布docker仓库的地址')
		string(name: 'MODULE_DOCKER_VERSION', defaultValue: '0.0.1', description: '发布docker仓库的模块版本号')
		string(name: 'DESC', defaultValue: '收银台', description: '模块描述信息')
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