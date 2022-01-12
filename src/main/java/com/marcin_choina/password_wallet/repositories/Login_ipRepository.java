package com.marcin_choina.password_wallet.repositories;

import com.marcin_choina.password_wallet.entities.Login_ip;
import com.marcin_choina.password_wallet.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface Login_ipRepository extends CrudRepository<Login_ip,Integer> {
    Login_ip findByUserAndIp(User user, String ip);
}
