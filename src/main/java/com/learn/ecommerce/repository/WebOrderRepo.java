package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.WebOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface WebOrderRepo extends CrudRepository<WebOrder,Long> {

    Collection<WebOrder> findByUser(LocalUser user);
}
