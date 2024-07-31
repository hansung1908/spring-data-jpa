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

##### spring-boot-starter-data-jpa 의존
- 필요한 설정 자동 처리
- maven / gradle 설정에 spring-boot-starter-data-jpa 의존 추가
- gradle로 진행시 build.gradle 파일이 설정 파일

##### 스프링 부트 설정
- main -> resources -> application.yml이 설정 파일
- 원래 properties 파일이 였으나 더 직관성 있는 yml 파일로 변경
- 스프링 부트 버전에 따라 설정은 달라질 수 있으니 버전에 따른 문서 참고

##### 엔티티 단위로 repository 상속한 타입 추가
- Repository 인터페이스
- 스프링 데이터 jpa가 제공하는 특별한 타입
- 이 인터페이스를 상속한 인터페이스를 이용해 빈 (bean) 객체를 생성

##### 규칙에 맞게 메서드 추가
- save(), findById(), delete() 등 규칙에 맞게 메서드 정의
- 해당 주소에서 규칙을 확인
```text
https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
```

##### repository를 주입 받아 사용
- service 레이어에서 사용
- 원하는 repository에 대한 객체를 생성
- 생성자를 통해 repository에 대한 내용을 주입
- 이를 di (defendency injection)이라고 함
- 직접적인 호출은 피하면서 간단히 사용할 수 있다는 장점

### interface
- Repository 하위 인터페이스를 상속하면 관련 메소드 모두 포함
- 메서드를 추가해줄 필요 없음
```text
public interface UserRepository extends JpaRepository<User, String> {
    // 메서드를 정의하지 않아도 CrudRepository와 JpaRepository에 있는
    // save(), findById(), findAll() 등의 메서드를 제공
}
```

### repository 메서드 작성 규칙

##### 식별자로 엔티티 조회
- findById()
- T findById(ID id) -> 없으면 null 반환
- Optional<T> findById(ID id) -> 없으면 empty Optional 반환
```text
Optional<User> findById(String email);
```

##### 엔티티 삭제
- delete
- void delete(T entity)
- void deleteById(ID id) -> 내부적으로 findById()로 엔티티를 조회한 뒤 delete()로 삭제
- 삭제할 대상이 존재하지 않으면 exception 발생
```text
void delete(User user);

userRepository.delete(user);
```

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
- persistable 예시
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

- 조건 비교
```text
List<User> findByNameLike(String keyword)
List<User> findByCreatedAtAfter(LocalDateTime time)
List<Hotel> findByYearBetween(int from, int to)
LessThan, IsNull, Containing, In, ...
```

- 전부 조회
```text
repository.findAll();
```

- 자세한 건 '규칙에 맞게 메소드 추가' 부분에서 언급한 스프링 레퍼런스 문서 주소 참고

##### 한 개 결과 조회
- 리턴 타입이 List가 아님
- 존재하면 해당 값, 없으면 null 또는 빈 Optional
- 조회 결과 개수가 두 개 이상이면 exception
```text
User findByName(String name)
Optional<User> findByName(String name)
```

##### 주의
- findBy 메서드를 남용하지 말 것
- 검색 조건이 단순하지 않으면 @Query, SQL, 스펙 / QueryDSL 사용

### 정렬
- find 메서드에 OrderBy
- OrderBy 뒤에 프로퍼티명 + Asc / Desc
- 여러 프로퍼티 지정 가능
```text
List<User> findByNameLikeOrderByNameDesc(String keyword);
-> order by u.name desc

List<User> findByNameLikeOrderByNameAscEmailDesc(String keyword);
-> order by u,name asc, email desc
```

- sort 타입
```text
List<User> findByNameLike(String keyword, Sort sort);

Sort sort1 = Sort.by(Sort.Order.asc("name"));
List<User> users1 = userRepository.findByNameLike("이름%", sort1);
-> order by u.name asc

Sort sort2 = Sort.by(Sort.Order.asc("name"), Sort.Order.desc("email"));
List<User> users2 = userRepository.findByNameLike("이름%", sort2);
-> order by u.name asc, email desc
```

- 메서드 명으로 정령 지정할 순 있지만 가능하면 Sort 사용

### 페이징
- Pageable / PageRequest 사용
```text
List<User> findByNameLike(String keyword, Pageable pageable);

Sort sort3 = Sort.by(Sort.Order.asc("name"), Sort.Order.desc("email"));

// page는 0부터 시작
// 한 페이지에 10개 기준으로 두 번째 페이지 조회  
Pageable pageable = PageRequest.ofSize(10).withPage(1).withSort(sort3);
List<User> users3 = userRepository.findByNameLike("이름%", pageable);
```

- 페이징 조회 결과 Page로 구하기
- Page 타입
- 페이징 처리에 필요한 값을 함께 제공 (예, 전체 페이지 개수, 전체 개수 등)
- Pageable을 사용하는 메서드의 리턴 타입을 Page로 지정하면 됨
```text
Page<User> findByEmailLike(String keyword, Pageable pageable);

Pageable pageable2 = PageRequest.ofSize(10).withPage(0).withSort(sort3);
Page<User> page = userRepository.findByEmailLike("email%", pageable2);

long totalElements = page.getTotalElements(); // 조건에 해당하는 전체 개수
int totalPages = page.getTotalPages(); // 전체 페이지 개수
List<User> content = page.getContent(); // 현재 페이지 결과 목록
int size = page.getSize(); // 페이지 크기
int pageNumber = page.getNumber();  // 현재 페이지
int numberOfElements = page.getNumberOfElements(); // content의 개수
```

- Pageable / PageRequest로 페이징 처리 가능
- findTop / findFirst / findTopN / findFirstN

### @Query
- 메서드 명명 규칙이 아닌 JPQL을 직적 사용
- 메서드 이름이 간결해짐
```text
@Query("select u from User u where u.createDate > :since order by u.createDate desc")
List<User> findRecentUsers(@Param("since") LocalDateTime since);

@Query("select u from User u where u.createDate > :since")
List<User> findRecentUsers(@Param("since") LocalDateTime since, Sort sort);

@Query("select u from User u where u.createDate > :since")
Page<User> findRecentUsers(@Param("since") LocalDateTime since, Pageable pageable);
```

##### @Query 네이티브 쿼리
- jpql 아닌 sql을 실행
```text
@Query(
    value = "select * from user u where u.create_date >= date_sub(now(), interval 1 day)",
    nativeQuery = true)
List<User> findRecentUsers();

@Query(
    value = "select max(create_date) from user",
    nativeQuery = true)
LocalDateTime selectLastCreateDate();
```

### Specification
- 검색 조건을 생성하는 인터페이스
- 줄여서 스펙이라고 함
- Criteria를 이용해서 검색 조건 생성
```text
@Nullable
Predicate toPredicate(Root<T> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
```

- repository에서 Specification을 이용한 검색 지정
```text
List<User> findAll(Specification<User> spec);
```

- 구현 예시
- UserNameSpecification 클래스 참조
```text
UserNameSpecification spec = new UserNameSpecification("이름");
List<User> users = userRepository.findAll(spec);
```

- 람다로 간결하게 구현
- Specification을 구현한 클래스를 매번 만들기 보단 람다식을 이용해서 스펙 생성
- UserSpecs 클래스 참조
```text
UserNameSpecification spec = UserSpecs.nameLike("이름");
List<User> users = userRepository.findAll(spec);
```

##### 검색 조건 조합
- Specification의 or / and 메소드를 이용해서 조합
```text
Specification<User> nameSpec = UserSpecs.nameLike("이름1");
Specification<User> afterSpec = UserSpecs.createdAfter(LocalDateTime.now().minusHours(1));
Specification<User> compositespec = nameSpec.and(afterSpec);
List<User> users2 = userRepository.findAll(compositespec);

Specification<User> spec3 = UserSpecs.nameLike("이름2")
                .and(UserSpecs.createdAfter(LocalDateTime.now().minusHours(1)));
List<User> users3 = userRepository.findAll(spec3);
```

- 선택적으로 조합
```text
Specification<User> spec = Specification.where(null);

if (keyword != null && !keyword.trim().isEmpty()) {
    spec = spec.and(UserSpecs.nameLike(keyword));
}
if (dateTime != null) {
    spec = spec.and(UserSpecs.createdAfter(dateTime));
}

List<User> users = userRepository.findAll(spec);
```

- SpecBuilder
- if절을 덜 쓰기 위해 SpecBuilder 구현
```text
Specification<User> specs = SpecBuilder.builder(User.class)
                .ifHasText(keyword, str -> UserSpecs.nameLike(str))
                .ifNotNull(dt, value -> UserSpecs.createdAfter(value))
                .toSpec();
```

- Specification + 정렬, 페이징
```text
List<User> findAll(Specification<User> spec, Sort s);
Page<User> findAll(Specification<User> spec, Pageable pageable);
```

### count
- 갯수를 세는 메소드
```text
long count() // 전체 갯수
long countByNameLike(String keyword)
long count(Specification<User> spec)
```