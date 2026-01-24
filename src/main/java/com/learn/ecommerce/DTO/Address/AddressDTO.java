package com.learn.ecommerce.DTO.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressDTO {
		private String addressLine1;
		private String addressLine2;
		private String city;
		private String country;



}