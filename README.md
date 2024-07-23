# spring-data-jpa

### jpa만 사용하지 않음
- spring boot + spring data jpa -> (거의) 설정없이 사용
- 자동 설정
  - persistence.xml
  - EntityManagerFactory
- 스프링 연동
  - 스프링 트랜잭션 연동
  - EntityManager 연동

### 사용법
- 총 5단계로 진행
```text
1. spring-boot-starter-data-jpa 의존 -> 필요한 설정 자동 처리
2. 스프링 부트 설정
3. 엔티티 단위로 repository 인터페이스를 상속 받은 인터페이스 생성 (또는 그 하위 인터페이스)
4. 지정한 규칙에 맞게 메서드 추가
5. 필요한 곳에 해당 인터페이스 타입을 주입해서 사용
```
---

##### spring-boot-starter-data-jpa 의존
- 필요한 설정 자동 처리
- maven / gradle 설정에 spring-boot-starter-data-jpa 의존 추가
- gradle로 진행시 build.gradle 파일이 설정 파일
---

##### 스프링 부트 설정
- main -> resources -> application.yml이 설정 파일
- 원래 properties 파일이 였으나 더 직관성 있는 yml 파일로 변경
- 스프링 부트 버전에 따라 설정은 달라질 수 있으니 버전에 따른 문서 참고
---

##### 엔티티 단위로 repository 상속한 타입 추가
- Repository 인터페이스
- 스프링 데이터 jpa가 제공하는 특별한 타입
- 이 인터페이스를 상속한 인터페이스를 이용해 빈 (bean) 객체를 생성
---

##### 규칙에 맞게 메서드 추가
- save(), findById(), delete() 등 규칙에 맞게 메서드 정의
- 해당 주소에서 규칙을 확인
```text
https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
```
---

##### repository를 주입 받아 사용
- service 레이어에서 사용
- 원하는 repository에 대한 객체를 생성
- 생성자를 통해 repository에 대한 내용을 주입
- 이를 di (defendency injection)이라고 함
- 직접적인 호출은 피하면서 간단히 사용할 수 있다는 장점
