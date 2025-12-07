package com.learn.ecommerce.repository;


import com.learn.ecommerce.model.LocalUser;
import com.learn.ecommerce.model.WebOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface WebOrderRepo extends CrudRepository<WebOrder,Long> {

    Collection<WebOrder> findByUser(LocalUser user);
}
