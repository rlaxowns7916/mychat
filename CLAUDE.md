# `CLAUDE.md` - MyChat Backend Service
이 문서는 MyChat 백엔드 서비스를 위한 Claude 설정 파일로, AI 개발자가 프로젝트에서 작업할 때 반드시 알아야 할 핵심 정보들을 담고 있습니다.

## 핵심 원칙
- 구현 세부사항이 불확실할 때는 **항상 개발자에게 문의** 해야 합니다.

## AI Assistant 워크플로: 단계별 방법론
- **사용자 지시에 응답할 때 AI 비서(Claude, Cursor, GPT 등)는 명확성, 정확성, 유지 관리 용이성을 보장하기 위해 다음 프로세스를 따라야 합니다.**
- 모호한 부분 명확히 하기 : 수집한 정보를 바탕으로 명확화가 필요한 부분이 있는지 확인하세요. 필요하다면 진행하기 전에 사용자에게 구체적인 질문을 던지세요.
- 세부 분석 및 계획 : 현재 작업을 세부적으로 분석하고 프로젝트 관례와 모범 사례를 참조하여 작업을 수행하기 위한 대략적인 계획을 수립합니다.
- 사소한 작업 : 계획/요청이 사소한 것이라면, 곧바로 시작하세요.
- 간단하지 않은 작업 : 그렇지 않은 경우 계획을 사용자에게 제시하여 검토하고 피드백을 바탕으로 반복합니다.
- 진행 상황 추적 TODOS.md : 여러 단계로 구성되거나 복잡한 작업에 대한 진행 상황을 추적하려면 할 일 목록을 사용합니다(내부적으로 또는 선택적으로 파일에서 ).
- 막혔을 경우, 계획을 다시 세우세요 . 막히거나 막혔을 경우, 3단계로 돌아가 계획을 다시 평가하고 조정하세요.
- 문서 업데이트 : 사용자의 요청이 충족되면 관련 앵커 주석( AIDEV-NOTE, 등)과 AGENTS.md사용자가 수정한 파일 및 디렉토리의 파일을 업데이트합니다.
- **작업 완료 확인** : 모든 작업이 완료되었다고 생각하면 **반드시 개발자에게 최종 확인을 받아야 합니다**.
- **Git 커밋 수행** : 개발자의 확인을 받았다면 커밋 규약에 따라서 Git Commit을 수행합니다.
- 사용자 검토 : 작업을 완료한 후 사용자에게 수행한 작업을 검토하도록 요청하고, 필요에 따라 프로세스를 반복합니다.
- 세션 경계 : 사용자의 요청이 현재 컨텍스트와 직접 관련이 없고 새 세션에서 안전하게 시작할 수 있는 경우 컨텍스트 혼란을 피하기 위해 처음부터 시작하는 것이 좋습니다.

## Git Commit 원칙
- **수행 주체**: AI가 개발자의 승인 후 커밋을 직접 수행합니다.
- **세분화된 커밋** : 커밋당 하나의 논리적 변경 사항.
- **AI 태그 표시** : AI가 생성한 커밋에 태그를 지정합니다. 예: `feat: optimise feed query [AI]`.
- **명확한 커밋 메시지** : 
  - **무엇을** 변경했는지 명시
  - **왜** 변경했는지 이유를 설명
  - 아키텍처와 관련된 경우 이슈/ADR에 대한 링크를 제공
  - 메시지는 50자 이내의 제목과 필요시 상세 설명으로 구성
- **커밋 메시지 형식**:
  ```
  <type>: <description> [AI]
  
  <optional body>
  
  <optional footer>
  ```
- **커밋 타입**:
  - `feat`: 새로운 기능 추가
  - `fix`: 버그 수정
  - `docs`: 문서 변경
  - `style`: 코드 포맷팅, 세미콜론 누락 등
  - `refactor`: 코드 리팩토링
  - `test`: 테스트 추가 또는 수정
  - `chore`: 빌드 과정 또는 보조 도구 변경
- **AI가 생성한 코드 검토** : 이해하지 못하는 코드는 절대 병합하지 마세요.

## Build & Test 명령어
## Gradle 명령어 테이블

| 작업 | 명령어 | 설명 |
|------|--------|------|
| 코드 포맷팅 | `./gradlew ktlintFormat` | Kotlin 코드 스타일 자동 수정 |
| 린트 검사 | `./gradlew ktlintCheck` | 코드 스타일 규칙 위반 검사 |
| 테스트 실행 | `./gradlew test` | 전체 테스트 스위트 실행 |
| 빌드 | `./gradlew build` | 프로젝트 전체 빌드 (린트, 테스트, 컴파일 포함) |

## AI 코드 품질 평가 룰

### 필수 실행 순서
AI가 코드 작업을 완료한 후 반드시 다음 순서로 명령을 실행하여 현재 상황을 평가해야 합니다:

```bash
# 1단계: 코드 포맷팅 적용
./gradlew ktlintFormat

# 2단계: 린트 검사 수행
./gradlew ktlintCheck

# 3단계: 테스트 실행
./gradlew test

# 4단계: 전체 빌드 확인
./gradlew build
```

### 평가 기준 및 대응 방안

#### 1. 코드 포맷팅 (`ktlintFormat`)
- **성공**: 자동으로 코드 스타일이 수정됨
- **실패**: 수정 불가능한 스타일 문제 존재
- **대응**: 수정된 파일이 있다면 변경사항을 확인하고 커밋에 포함

#### 2. 린트 검사 (`ktlintCheck`)
- **성공**: 모든 코드가 Kotlin 스타일 가이드 준수
- **실패**: 스타일 규칙 위반 발견
- **대응**:
    - 위반 사항을 자동 수정 가능한 경우: `ktlintFormat` 재실행
    - 수동 수정 필요한 경우: 해당 파일과 라인을 수정

#### 3. 테스트 실행 (`test`)
- **성공**: 모든 테스트 통과
- **실패**: 테스트 케이스 실패 발견
- **대응**:
    - 기존 테스트 실패: 코드 수정으로 인한 회귀 버그 해결
    - 새로운 기능 테스트 필요: 적절한 테스트 케이스 작성 제안

#### 4. 전체 빌드 (`build`)
- **성공**: 프로젝트가 배포 가능한 상태
- **실패**: 컴파일 오류, 의존성 문제 등 존재
- **대응**: 빌드 실패 원인 분석 및 해결책 제시

### AI 보고 템플릿
- 각 단계 실행 후 다음 형식으로 결과를 보고해야 합니다:
```markdown
## 코드 품질 평가 결과

### 1. 코드 포맷팅 (ktlintFormat)
- ✅/❌ 상태: [성공/실패]
- 📝 변경사항: [수정된 파일 목록 또는 "변경사항 없음"]

### 2. 린트 검사 (ktlintCheck)
- ✅/❌ 상태: [성공/실패]
- ⚠️ 경고사항: [위반 사항 또는 "없음"]

### 3. 테스트 실행 (test)
- ✅/❌ 상태: [성공/실패]
- 📊 결과: [통과/실패 테스트 수]
- 🐛 실패 원인: [실패한 테스트와 원인 또는 "없음"]

### 4. 전체 빌드 (build)
- ✅/❌ 상태: [성공/실패]
- 📦 빌드 상태: [배포 준비 완료/문제 있음]

## 종합 평가
- 🎯 전체 상태: [양호/주의/불량]
- 📋 필요 조치: [추가 작업 사항 또는 "없음"]

## 최종 확인 요청
- 🔍 개발자 확인 필요: 모든 작업이 완료되었습니다. 최종 검토 및 승인을 요청드립니다.
- 📝 변경사항 요약: [주요 변경사항 요약]
- 💬 커밋 준비: 승인 후 커밋 규약에 따라 Git 커밋을 수행하겠습니다.
```

### 예외 상황 처리
1. **Gradle 래퍼 권한 문제**: `chmod +x gradlew` 실행 후 재시도
2. **의존성 다운로드 실패**: 네트워크 상태 확인 및 재시도 안내
3. **메모리 부족**: JVM 힙 크기 조정 제안
4. **멀티모듈 빌드 실패**: 특정 모듈만 빌드하여 문제 격리

## 프로젝트 이해

### **WebSocket Gateway (websocket-gateway)**
- **역할**: 실시간 양방향 통신을 위한 WebSocket 서버
- **기술**: Netty 5.0 + Spring WebFlux + Kotlin Coroutines
- **주요 기능**:
    - STOMP 프로토콜 처리
    - WebSocket 연결 관리 및 세션 관리
    - 클라이언트 인증
    - 메시지 프레임 인코딩/디코딩
- **특징**:
    - Netty의 이벤트 기반 NIO 처리로 고성능 실현
    - STOMP over WebSocket으로 표준 메시징 프로토콜 지원

#### **Core API (core-api)**
- **역할**: REST API 서비스 제공
- **기술**: Spring WebFlux + Kotlin Coroutines
- **의존성**: RDB Storage, Chat Storage 모듈 사용
- **특징**: 비블로킹 리액티브 프로그래밍 모델

#### **Session Management (modules/session-map)**
- **역할**: 세션 상태 관리 및 분산 저장
- **기술**: Redis + Lettuce (비동기 Redis 클라이언트)
- **특징**:
    - 다중 인스턴스 환경에서 세션 공유
    - 실시간 연결 상태 추적

#### **Storage Layer**
1. **RDB Storage (storage/rdb)**
    - **기술**: Spring Data R2DBC + MySQL
    - **역할**: 사용자, 채팅방 등 관계형 데이터 관리

2. **Chat Storage (storage/chat)**
    - **기술**: Spring Data MongoDB Reactive
    - **역할**: 채팅 메시지 저장 및 조회 최적화

#### **Message Queue & Consumer (consumer)**
- **기술**: Apache Kafka
- **역할**:
    - 비동기 메시지 처리
    - 채팅 메시지 저장
    - 연결되지 않은 사용자에게 푸시 알림 전송
    - 메시지 Relay 기능

##  아키텍처 개요

#### **리액티브 프로그래밍**
- 모든 컴포넌트에서 Spring WebFlux + Kotlin Coroutines 활용
- 비블로킹 I/O로 높은 동시성 처리 능력

#### **마이크로서비스 아키텍처**
- 각 모듈별 명확한 책임 분리
- 독립적인 배포 및 확장 가능

#### **이벤트 기반 아키텍처**
- Kafka를 통한 느슨한 결합
- 확장성과 장애 격리 효과

#### **다중 데이터베이스 전략**
- MySQL: 일관성이 중요한 구조적 데이터
- MongoDB: 대용량 채팅 메시지의 빠른 읽기/쓰기
- Redis: 실시간 세션 및 캐시 데이터

## 코딩 스타일과 패턴

### Kotlin 우선 개발 원칙
#### **Primary Language**
- **새 코드는 Kotlin 우선**: 모든 신규 개발은 Kotlin을 사용합니다.

#### **Kotlin 코딩 컨벤션**
- **공식 컨벤션 준수**: [Kotlin 공식 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html)을 따릅니다.
- **스코프 함수 활용**: `apply`, `let`, `run`, `with`, `also`를 명확한 목적에 따라 사용합니다:
  - `apply`: 객체 구성 및 설정
  - `let`: null 체크 및 변환
  - `run`: 객체에서 계산 수행
  - `with`: 객체의 함수를 여러 번 호출
  - `also`: 추가 작업 (로깅, 검증 등)
- **불변성 우선**: `val`을 우선 사용하고, `var`는 꼭 필요한 경우(성능상 이유, 본질적으로 가변 상태)에만 사용합니다.
- **데이터 클래스 적극 활용**: DTO 및 단순 값 객체 정의 시 data class를 적극 사용합니다.

### 코드 구조 및 설계 원칙
#### **가독성 및 유지보수성**
- **조기 반환 패턴**: 불필요한 `else` 문이나 중첩된 `if` 문을 줄이기 위해 조기 반환 패턴을 사용합니다.
  ```kotlin
  // 좋은 예
  fun validateUser(user: User): ValidationResult {
      if (user.email.isBlank()) return ValidationResult.InvalidEmail
      if (user.age < 0) return ValidationResult.InvalidAge
      return ValidationResult.Valid
  }
  
  // 피해야 할 예
  fun validateUser(user: User): ValidationResult {
      if (user.email.isNotBlank()) {
          if (user.age >= 0) {
              return ValidationResult.Valid
          } else {
              return ValidationResult.InvalidAge
          }
      } else {
          return ValidationResult.InvalidEmail
      }
  }
  ```
- **클래스 및 함수 길이 제한**: 
  - 함수는 30줄 이하 권장 (단일 책임 원칙)
  - 클래스는 300줄 이하 권장 (단일 책임 원칙)
  - 초과 시 리팩토링 고려
- **매직 넘버/문자열 금지**: 의미가 불분명한 리터럴 대신 명명된 상수나 enum을 사용합니다.
  ```kotlin
  // 좋은 예
  companion object {
      private const val MAX_RETRY_COUNT = 3
      private const val CONNECTION_TIMEOUT_MS = 5000L
  }
  
  enum class HttpStatus(val code: Int) {
      OK(200),
      NOT_FOUND(404),
      INTERNAL_SERVER_ERROR(500)
  }
  
  // 피해야 할 예
  if (retryCount > 3) { // 3이 무엇을 의미하는가?
      throw MaxRetryExceededException()
  }
  ```

### Spring WebFlux & Kotlin Coroutines 패턴
- **suspend 함수 활용**: 비블로킹 작업을 위해 suspend 함수를 적극 활용합니다.
- **Flow 사용**: 스트림 데이터 처리 시 Kotlin Flow를 우선 사용합니다.
- **리액티브 타입 변환**: 
  ```kotlin
  // Mono/Flux와 suspend 함수 간 변환
  suspend fun getUser(id: Long): User = userRepository.findById(id).awaitSingle()
  
  // Flow 활용
  fun getAllUsers(): Flow<User> = userRepository.findAll().asFlow()
  ```

### 앵커 주석 시스템
특별히 포맷된 주석을 코드베이스 전반에 추가하여 `grep`으로 쉽게 검색 가능한 인라인 지식 베이스를 구축:

- **접두사**: `AIDEV-NOTE:`, `AIDEV-TODO:`, `AIDEV-QUESTION:` (모두 대문자)
- **워크플로우**: 파일 스캔 전 항상 관련 하위 디렉토리에서 기존 앵커 검색
- **유지보수**: 연관 코드 수정 시 관련 앵커 업데이트, 명시적 지시 없이는 제거 금지
- **추가 기준**: 복잡하거나, 매우 중요하거나, 혼란스럽거나, 버그가 있을 수 있는 코드

## AI가 절대 해서는 안 되는 행동들
1. **테스트 파일 수정 금지** - 테스트는 인간의 의도를 인코딩
2. **API 계약 변경 금지** - 실제 애플리케이션 파손 위험
3. **마이그레이션 파일 변경 금지** - 데이터 손실 위험
4. **시크릿 커밋 금지** - 환경 변수 사용 필수
5. **비즈니스 로직 추측 금지** - 항상 문의 필요
6. **AIDEV- 주석 제거 금지** - 존재하는 이유가 있음
7. **개발자 확인 없이 커밋 금지** - 모든 작업 완료 후 반드시 개발자 승인을 받아야 함
