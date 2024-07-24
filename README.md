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

### repository 메서드 작성 규칙

##### 식별자로 엔티티 조회
- findById()
- T findById(ID id) -> 없으면 null 반환
- Optional<T> findById(ID id) -> 없으면 empty Optional 반환
```text
Optional<User> findById(String email);
```
---

##### 엔티티 삭제
- delete
- void delete(T entity)
- void deleteById(ID id) -> 내부적으로 findById()로 엔티티를 조회한 뒤 delete()로 삭제
- 삭제할 대상이 존재하지 않으면 exception 발생
```text
void delete(User user);

userRepository.delete(user);
```
---

##### 엔티티 저장
- save
- void save(T entity)
- T save(T entity)
```text
// void save(User user);
User save(User user);

userRepository.save(user);
```

- save() 메서드 호출 시 select 쿼리 실행 후 insert 실행
- 새 엔티티면 entitymanager의 persist() 실행, 아니면 merge() 실행
- 여기서 merge로 인해 기존의 데이터가 있는지 확인하기 위해 select 쿼리를 실행
---

- 새 엔티티인지 판단하는 기준
- persistable을 구현한 엔티티 -> isNew()로 판단
- @Version 속성이 있는 경우 -> 버전 값이 null이면 새 엔티티로 판단
- 식별자가 참조 타입인 경우 -> 식별자가 null이면 새 엔티티로 판단
- 식별자가 참조 타입인 경우 -> 0이면 새 엔티티로 판단

##### persistable 예시
```text
public class User implements Persistable<String>

@Transient
private boolean isNew = true;

@Override
public String getId() {
    return email;
}

@Override
public boolean isNew() {
    return isNew;
}

@PostLoad
@PrePersist
void markNotNew() {
   this.isNew = false;
}
```

##### 특정 조건으로 찾기
- findBy프로퍼티(값) -> property가 특정 값인 대상
```text
List<User> findByName(String name)
List<Hotel> findByGradeAndName(Grade g, String name)
```
---

- 조건 비교
```text
List<User> findByNameLike(String keyword)
List<User> findByCreatedAtAfter(LocalDateTime time)
List<Hotel> findByYearBetween(int from, int to)
LessThan, IsNull, Containing, In, ...
```
- 자세한 건 '규칙에 맞게 메소드 추가' 부분에서 언급한 스프링 레퍼런스 문서 주소 참고
---

- 전부 조회
```text
repository.findAll();
```
---

##### 주의
- findBy 메서드를 남용하지 말 것
- 검색 조건이 단순하지 않으면 @Query, SQL, 스펙 / QueryDSL 사용