package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.WebOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WebOrderRepo extends CrudRepository<WebOrder,Long> {

    Collection<WebOrder> findByUser(LocalUser user);
    List<WebOrder> findByUserId(Long userId);

    Optional<WebOrder> findByIdAndUserId(Long orderId, Long userId);

    List<WebOrder> findByOrderStatus(String status);
    List<WebOrder> findByUserIdAndIsDeletedFalse(Long userId);
}
