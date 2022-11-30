#!/usr/bin/env bash

JAR_REPOSITORY=/home/ubuntu/app/build/libs
REPOSITORY=/home/ubuntu/app/
JAR_NAME=$(ls -tr $JAR_REPOSITORY/do_block-0.0.1-SNAPSHOT.jar | tail -n 1)

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f $JAR_NAME)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

echo "> JAR NAME: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"
# -Duser.timezone=Asia/Seoul
nohup java -jar $JAR_NAME >> $REPOSITORY/nohup.out 2>&1 &