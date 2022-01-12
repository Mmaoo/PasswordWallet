package com.marcin_choina.password_wallet.repositories;

import com.marcin_choina.password_wallet.entities.Login;
import com.marcin_choina.password_wallet.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LoginRepository extends CrudRepository<Login,Integer> {
    List<Login> findByUserOrderByDateAsc(User user);
}
