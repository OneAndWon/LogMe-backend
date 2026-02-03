#!/bin/bash

# 1. 환경변수 파일 로드
# 같은 폴더에 있는 env.sh를 읽어옴.
if [ -f ./env.sh ]; then
  source ./env.sh
  echo "🔒 보안 환경변수(env.sh) 로드 완료."
else
  echo "⚠️ env.sh 파일 없음. DB 연결 실패 가능성 있음."
fi

# 2. 실행할 JAR 파일 찾기 (현재 폴더 기준)
# build/libs/ 경로를 없애고, 현재 폴더(*.jar)에서 찾도록 수정.
# 'plain'이 포함된 껍데기 파일은 제외(grep -v)합니다.
JAR_NAME=$(ls *.jar | grep -v 'plain' | tail -n 1)

# 파일이 없는 경우 에러 메시지 출력 후 종료
if [ -z "$JAR_NAME" ]; then
  echo "❌ 실행할 JAR 파일을 찾을 수 없습니다"
  exit 1
fi

echo "🚀 실행할 파일 감지됨: $JAR_NAME"

# 3. 기존 서버 종료 (재배포 시 필수)
# 기존에 돌고 있는 자바 프로세스가 있으면 종료.
CURRENT_PID=$(pgrep -f "java -jar")

if [ -n "$CURRENT_PID" ]; then
  echo "♻️ 기존 실행 중인 서버($CURRENT_PID) 종료 중..."
  kill -15 $CURRENT_PID
  sleep 5
fi

# 4. 서버 실행
# build/libs/ 경로 대신 찾은 파일명($JAR_NAME)을 바로 사용.
nohup java -Xms256m -Xmx400m -jar -Dspring.profiles.active=prod \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dspring.cloud.aws.s3.bucket=$S3_BUCKET_NAME \
  -Dspring.datasource.url=jdbc:postgresql://$PROD_DB_HOST:$PROD_DB_PORT/$PROD_DB_NAME \
  -Dspring.datasource.username=$PROD_DB_USER \
  -Dspring.datasource.password=$PROD_DB_PASS \
  $JAR_NAME > log.txt 2>&1 &

echo "🎉 서버 실행 명령 전달 완료."
echo "📄 로그 확인: tail -f log.txt"