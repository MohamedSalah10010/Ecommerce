package com.learn.ecommerce.repository.JpaQueryLogic;

import com.learn.ecommerce.entity.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<@NotNull Product> isNotDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<@NotNull Product> hasCategory(String category) {
        return (root, query, cb) ->
                category == null
                        ? null
                        : cb.equal(root.get("category").get("name"), category);
    }

    public static Specification<@NotNull Product> priceBetween(
            Double min, Double max
    ) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("price"), min);

            return cb.between(root.get("price"), min, max);
        };
    }
}