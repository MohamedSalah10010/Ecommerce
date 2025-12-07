package com.learn.ecommerce.repository;

import com.learn.ecommerce.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepo  extends CrudRepository<Address,Long> {
}
