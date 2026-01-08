package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.ProductDTO.*;
import com.learn.ecommerce.entity.Category;
import com.learn.ecommerce.entity.Inventory;
import com.learn.ecommerce.entity.Product;
import com.learn.ecommerce.exceptionhandler.CategoryNotFoundException;
import com.learn.ecommerce.exceptionhandler.ProductNotFoundException;
import com.learn.ecommerce.repository.CategoryRepo;
import com.learn.ecommerce.repository.InventoryRepo;
import com.learn.ecommerce.repository.JpaQueryLogic.ProductSpecification;
import com.learn.ecommerce.repository.ProductRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class ProductService {

    private ProductRepo productRepo;
    private InventoryRepo inventoryRepo;
    private CategoryRepo categoryRepo;
    public ProductService(ProductRepo productRepo, InventoryRepo inventoryRepo, CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

//    public Collection<ProductDTO> getProducts() {
//        Collection<ProductDTO> productsDTO = new ArrayList<ProductDTO>();
//        Collection<Product> products = (Collection<Product>) productRepo.findAll();
//        for (Product product : products) {
//            if (product.isDeleted())
//                continue;
//
//            productsDTO.add(new ProductDTO().builder()
//                    .price(product.getPrice())
//                    .name(product.getName())
//                    .shortDescription(product.getShortDescription())
//                    .longDescription(product.getLongDescription())
//                    .inventory(product.getInventory())
//                    .build());
//        }
//        return productsDTO;
//    }


        public Page<ProductDTO> getProducts(

                Double priceMin,
                Double priceMax,
                String sortBy,
                String direction,
                Pageable pageable
                                    ) {

            Specification<Product> spec = Specification
                    .where(ProductSpecification.isNotDeleted())
//                    .and(ProductSpecification.hasCategory(category))
                    .and(ProductSpecification.priceBetween(priceMin, priceMax));

            Sort sort = Sort.unsorted();

            if (sortBy != null) {
                sort = Sort.by(
                        "desc".equalsIgnoreCase(direction)
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC,
                        sortBy
                );
            }

            Pageable finalPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort
            );

            return productRepo.findAll(spec, finalPageable)
                    .map(product -> ProductDTO.builder()
                            .name(product.getName())
                            .price(product.getPrice())
                            .shortDescription(product.getShortDescription())
                            .build());
        }



    public ProductDTO getProduct(Long id) {
        Product product = productRepo.findById(id).get();
//
//        productRepo.findById(id)
//                .filter(p -> !p.isDeleted())
//                .orElseThrow(ProductNotFoundException::new);
        if (product == null || product.isDeleted())
            throw new ProductNotFoundException();

        return new ProductDTO().builder()
                .price(product.getPrice())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .quantity(product.getInventory().getQuantity())
                .categoryName(product.getCategory().getName())
                .categoryId(product.getCategory().getId())
                .build();
    }

    @Transactional
    public ProductStatusDTO addProduct(AddProductDTO productBody) {
        Product product = new Product();
        product.setName(productBody.getName());
        product.setShortDescription(productBody.getShortDescription());
        product.setLongDescription(productBody.getLongDescription());
        product.setPrice(productBody.getPrice());

        if(productBody.getCategoryId() != null){
            Category category =categoryRepo.findById(
                    productBody.getCategoryId()
            ).orElseThrow(() -> new CategoryNotFoundException()
            );

            product.setCategory(category);
        }
        Product savedProduct = productRepo.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(productBody.getQuantity());

        inventoryRepo.save(inventory);

        return new ProductStatusDTO().builder()
                .statusMessage("Product added successfully")
                .productId(savedProduct.getId())
                .productName(savedProduct.getName())
                .build();
    }

    @Transactional
    public ProductStatusDTO editProduct(Long id, AddProductDTO productBody) {
        Optional<Product> productOptional = productRepo.findById(id);
        if (productOptional.isPresent() && !productOptional.get().isDeleted()) {
            Product product = productOptional.get();
            if (!productBody.getName().isEmpty())
                product.setName(productBody.getName());

            if (productBody.getShortDescription()!= null)
                product.setShortDescription(productBody.getShortDescription());

            if (productBody.getLongDescription()!= null)
                product.setLongDescription(productBody.getLongDescription());

            if (productBody.getPrice()!= null)
                product.setPrice(productBody.getPrice());

            if (productBody.getQuantity()!= null)
                product.getInventory().setQuantity(productBody.getQuantity());

            if (productBody.getCategoryId()!=null) {
                Category category = categoryRepo.findById(
                        productBody.getCategoryId()
                ).orElseThrow(() -> new CategoryNotFoundException()
                );

                product.setCategory(category);
            }
            productRepo.save(product);

            return new ProductStatusDTO().builder()
                    .statusMessage("Product Edited successfully")
                    .productId(product.getId())
                    .productName(product.getName())
                    .build();
        }
        throw new ProductNotFoundException();


    }

    @Transactional
    public ProductStatusDTO deleteProduct(Long id) {
        productRepo.findById(id).ifPresentOrElse(product -> {
            product.setDeleted(true);
            productRepo.save(product);
        }, () -> {
            throw new ProductNotFoundException();
        });
        return new ProductStatusDTO().builder()
                .statusMessage("Product Deleted successfully")
                .productId(id)
                .build();
    }



    public List<ProductDTO> searchProducts(String query) {
        List<ProductDTO> productsDTO = new ArrayList<ProductDTO>();
        List<Product> products = productRepo.searchByName(query);
        for (Product product : products) {
            if (product.isDeleted())
                continue;

            productsDTO.add(new ProductDTO().builder()
                    .price(product.getPrice())
                    .name(product.getName())
                    .shortDescription(product.getShortDescription())
                    .longDescription(product.getLongDescription())
                    .quantity(product.getInventory().getQuantity())
                    .categoryId(product.getCategory().getId())
                    .categoryName(product.getCategory().getName())
                    .build());
        }
        return productsDTO;
    }

    @Transactional
    public ProductCategoryDTO updateProductCategory(Long productId,AddProductCategoryDTO body) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        if(product.isDeleted()) {throw new ProductNotFoundException();}
        Category category = categoryRepo.findById(body.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException());
        if(category.isDeleted()) {throw new CategoryNotFoundException();}
        product.setCategory(category);
        productRepo.save(product);
        return new ProductCategoryDTO().builder()
                .productName(product.getName())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .productId(product.getId())
                .build();

    }

}

