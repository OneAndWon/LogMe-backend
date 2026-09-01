# LogMe (로그미) - AI 기반 올인원 하루 기록 플랫폼

> **파편화된 일상의 기록(일정, 일기, 가계부)을 하나로 연결하고, AI 교차 분석을 통해 삶의 인사이트를 제공하는 서비스**

---
## 1. 프로젝트 개요
> - **개발 기간:** 2025.09 - 2026.05 (9개월)
> - **개발 인원:** 2인 (백엔드 1명, 안드로이드 프론트엔드 1명)
> - **담당 역할:** 백엔드 아키텍처 설계, RESTful API 개발, 클라우드 인프라 배포 및 외부 API(OAuth2, OpenAI) 연동

---
## 2. 기술 스택
| Category | Stack |
|---|---|
| Language | ![Java 21](https://img.shields.io/badge/java%2021-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) |
| Framework | ![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) &nbsp; |
| Package Manager | ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white) &nbsp;|
| Database & ORM | ![PostgreSQL](https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) &nbsp; ![Spring Data JPA](https://img.shields.io/badge/spring%20data%20JPA-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) &nbsp;|
| API Documentation | ![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white) &nbsp; |
| Auth & Security | ![Spring Security](https://img.shields.io/badge/spring%20security-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) <br> ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens) |
| Infrastructure & CI/CD | ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) <br> ![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white) |

---
## 3. 시스템 아키텍처
![Architecture](./readmeImage/architecture_diagram.png)

---
## 4. 주요 기능 

### 1. Todo (일정 관리)
- 월간 및 주간 달력을 통한 일정 관리 지원
- 할 일 생성, 수정, 삭제 기능 제공 (카테고리, 중요도, 하위 태스크, 메모, 반복 여부 등 세부 설정 포함)
- 오늘의 할 일 달성률 시각화

### 2. Diary (일기)
- 하루를 돌아보고 감정을 정리할 수 있는 공간 제공
- 캘린더용 일기 목록 조회 및 키워드 기반 일기 검색 기능 지원

### 3. Finance (가계부)
- 거래 내역, 자산, 가계부 카테고리, 예산 관리 기능 제공
- 지출 및 예산 설정을 통한 체계적인 소비 습관 형성 유도

### 4. 통합 대시보드
- 사용자가 앱을 처음 실행했을 때 마주하는 메인 화면
- 투두, 일기, 가계부의 핵심 요약 내용과 오늘의 다짐을 한눈에 파악할 수 있는 타임라인 제공

### 5. AI 리포트
- 분산된 일정, 일기, 소비 데이터를 종합적으로 교차 분석
- 라이프 밸런스, 패턴 분석, 생산성 팁, 소비 습관, 목표 제안, 미래 예측 등 5가지 핵심 인사이트 제공

---
## 5. 기능 시연
- [주요 기능 시연 영상 보기 (YouTube)](https://www.youtube.com/watch?v=n6uCI0sRC9g)
