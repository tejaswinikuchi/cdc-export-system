package com.example.cdc.repository;

import com.example.cdc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIsDeletedFalse();

    List<User> findByUpdatedAtAfterAndIsDeletedFalse(OffsetDateTime timestamp);

    List<User> findByUpdatedAtAfter(OffsetDateTime timestamp);
}