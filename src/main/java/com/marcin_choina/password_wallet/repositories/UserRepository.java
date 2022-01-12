package com.marcin_choina.password_wallet.repositories;

import com.marcin_choina.password_wallet.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {
    public User findByLogin(String login);
    public User findByEmail(String email);
}
