#!/bin/bash

# 1. 환경변수 파일 로드
# 서버에 있는 env.sh 파일을 읽어옴. (없으면 경고 메시지 출력)
if [ -f ./env.sh ]; then
  source ./env.sh
  echo "🔒 보안 환경변수(env.sh) 로드 완료."
else
  echo "⚠️ env.sh 파일 없음. DB 연결 실패 가능성 있음."
fi

# 2. 서버 실행
# $변수명 형태로 env.sh의 값을 주입받아 실행
nohup java -Xms256m -Xmx400m -jar -Dspring.profiles.active=prod \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dspring.cloud.aws.s3.bucket=$S3_BUCKET_NAME \
  -Dspring.datasource.url=jdbc:mysql://$PROD_DB_HOST:$PROD_DB_PORT/$PROD_DB_NAME \
  -Dspring.datasource.username=$PROD_DB_USER \
  -Dspring.datasource.password=$PROD_DB_PASS \
  build/libs/*-SNAPSHOT.jar > log.txt 2>&1 &

echo "🚀 서버 실행 명령 전달 완료."
echo "📄 로그 확인: tail -f log.txt"