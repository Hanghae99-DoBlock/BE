package com.sparta.doblock.security.repository;

import com.sparta.doblock.security.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
