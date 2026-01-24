package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.WebOrder;
import com.learn.ecommerce.enums.OrderStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebOrderRepo extends JpaRepository<@NotNull WebOrder, @NotNull Long> {

    List<WebOrder> findByUser(LocalUser user);
    List<WebOrder> findByUserId(Long userId);
	List<WebOrder> findByUserAndIsDeleted(LocalUser user, boolean isDeleted);
	@NotNull Optional<WebOrder> findById(Long id);
	Optional<WebOrder> findByIdAndIsDeleted(Long id, boolean isDeleted);

    Optional<WebOrder> findByIdAndUserId(Long orderId, Long userId);

    List<WebOrder> findByOrderStatus(OrderStatus orderStatus);
    List<WebOrder> findByUserIdAndIsDeletedFalse(Long userId);
}