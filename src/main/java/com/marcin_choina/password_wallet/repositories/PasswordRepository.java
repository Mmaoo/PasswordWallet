package com.marcin_choina.password_wallet.repositories;

import com.marcin_choina.password_wallet.entities.Password;
import org.springframework.data.repository.CrudRepository;

public interface PasswordRepository extends CrudRepository<Password,Integer> {
}
