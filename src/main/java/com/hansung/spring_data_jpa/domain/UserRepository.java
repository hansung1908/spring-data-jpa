package com.hansung.spring_data_jpa.domain;

import org.springframework.data.repository.Repository;

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
}

