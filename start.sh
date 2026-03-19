#!/bin/bash

# 1. 환경변수 파일 로드
if [ -f ./env.sh ]; then
  source ./env.sh
  echo "🔒 보안 환경변수(env.sh) 로드 완료."
else
  echo "⚠️ env.sh 파일 없음. DB 연결 실패 가능성 있음."
fi

# 2. 실행할 JAR 파일 찾기 (가장 최신 파일 1개)
JAR_NAME=$(ls -t *.jar | grep -v 'plain' | head -n 1)

if [ -z "$JAR_NAME" ]; then
  echo "❌ 실행할 JAR 파일을 찾을 수 없습니다"
  exit 1
fi

echo "🚀 실행할 파일 감지됨: $JAR_NAME"

# 3. 기존 서버 종료 (가장 확실한 ps -ef 방식)
CURRENT_PID=$(ps -ef | grep java | grep jar | grep -v grep | awk '{print $2}')
CURRENT_PID=$(echo $CURRENT_PID | awk '{print $1}') # 여러 개일 경우 첫 번째 것만

if [ -n "$CURRENT_PID" ]; then
  echo "♻️ 기존 실행 중인 서버($CURRENT_PID) 종료 중..."
  kill -15 $CURRENT_PID
  sleep 5

  # 5초 뒤에도 살아있다면 확인 사살
  if ps -p $CURRENT_PID > /dev/null; then
     echo "⚠️ 서버가 5초 내에 종료되지 않아 강제 종료(kill -9)를 시도합니다."
     kill -9 $CURRENT_PID
     sleep 2
  fi
else
  echo "✅ 실행 중인 기존 서버가 없습니다."
fi

# 4. 서버 실행
nohup java -Xms256m -Xmx400m -jar -Dspring.profiles.active=prod \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dspring.cloud.aws.s3.bucket=$S3_BUCKET_NAME \
  -Dspring.datasource.url=jdbc:postgresql://$PROD_DB_HOST:$PROD_DB_PORT/$PROD_DB_NAME \
  -Dspring.datasource.username=$PROD_DB_USER \
  -Dspring.datasource.password=$PROD_DB_PASS \
  $JAR_NAME > log.txt 2>&1 &

echo "🎉 서버 실행 명령 전달 완료."
echo "📄 로그 확인: tail -f log.txt"