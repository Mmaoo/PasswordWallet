package com.marcin_choina.password_wallet.repositories;

import com.marcin_choina.password_wallet.entities.SharedPassword;
import org.springframework.data.repository.CrudRepository;

public interface SharedPasswordRepository extends CrudRepository<SharedPassword,Integer> {
}
