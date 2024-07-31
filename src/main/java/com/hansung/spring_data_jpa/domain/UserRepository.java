package com.hansung.spring_data_jpa.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


// T (User) : 엔티티 타입
// ID (String) : 엔티티의 식벽자 타입
public interface UserRepository extends Repository<User, String> {
    Optional<User> findById(String email);

    // void save(User user);
    User save(User user);

    void delete(User user);

    List<User> findByNameLike(String keyword);

    List<User> findByNameLikeOrderByNameDesc(String keyword);

    List<User> findByNameLikeOrderByNameAscEmailDesc(String keyword);

    List<User> findByNameLike(String keyword, Sort sort);

    List<User> findByNameLike(String keyword, Pageable pageable);

    Page<User> findByEmailLike(String keyword, Pageable pageable);

    @Query("select u from User u where u.createDate > :since order by u.createDate desc")
    List<User> findRecentUsers(@Param("since") LocalDateTime since);

    @Query("select u from User u where u.createDate > :since")
    List<User> findRecentUsers(@Param("since") LocalDateTime since, Sort sort);

    @Query("select u from User u where u.createDate > :since")
    Page<User> findRecentUsers(@Param("since") LocalDateTime since, Pageable pageable);

    List<User> findAll(Specification<User> spec);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

    long countByNameLike(String keyword);

    long count(Specification<User> spec);

    @Query(
            value = "select * from user u where u.create_date >= date_sub(now(), interval 1 day)",
            nativeQuery = true)
    List<User> findRecentUsers();

    @Query(
            value = "select max(create_date) from user",
            nativeQuery = true)
    LocalDateTime selectLastCreateDate();

    Optional<User> findByName(String name);
}

