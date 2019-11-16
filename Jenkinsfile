node{

	stage('git clone'){
        git branch: '艾尚医学僧', credentialsId: 'cd8a3a7a-2c4f-42e4-9454-bf5b3a2a120e', url: 'git@gitee.com:fouryou/foryou-platform.git'
    }

    stage('maven build'){
        
        dir('./'){
            sh '''
			mvn clean package -Dmaven.test.skip=true
		    '''
        }

    }
    
    stage('docker images build'){
		sh '''		
		#镜像ID
		IMAGE_ID=$(docker images | grep "$JOB_NAME" | awk \'{print $3}\')
		
		# 构建docker镜像
		if [ -n "$IMAGE_ID" ]; then
			echo "存在$JOB_NAME镜像，镜像id=$IMAGE_ID"
		else
			echo "不存在$JOB_NAME镜像，开始构建镜像"	
			echo "当前所在目录`pwd`"
			docker build -t $JOB_NAME .
		fi
		'''
	}
	
	stage('static resources deploy'){
		sh '''
			cp -rf ./target/foryou-platform/ /root/web/app/
		'''
	}
	
	stage('docker deploy run'){
		sh '''		
		if [[ -n "$(docker ps -a | grep "$JOB_NAME" | awk \'{print $1}\')" ]]; then
			echo "存在$JOB_NAME容器,容器ID=$CID,状态：$(docker inspect $JOB_NAME -f '{{.State.Status}}'),重建docker容器 ..."
			docker stop $JOB_NAME
	       	docker rm -f $JOB_NAME
	      	docker-compose up -d
			echo "$JOB_NAME容器重建完成"
		else
			echo "不存在$JOB_NAME容器，docker-compose run创建容器..."
			docker-compose up -d
			echo "$JOB_NAME容器创建完成"
		fi
		'''
	}
}