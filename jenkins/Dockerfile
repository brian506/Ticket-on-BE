FROM jenkins/jenkins:lts
USER root

# 1. 패키지 업데이트 및 JDK 21 설치 추가
RUN apt-get update && \
    apt-get install -y fontconfig openjdk-21-jdk ca-certificates curl gnupg lsb-release && \
    # 2. Docker CLI 설치 (기존 로직)
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
    https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli git && \
    # 사용하지 않는 파일 정리
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 3. 환경 변수 설정 (젠킨스가 JDK 21을 기본으로 인식하게 함)
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

USER jenkins