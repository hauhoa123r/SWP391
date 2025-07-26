package org.project.admin.mapper;

import javax.annotation.processing.Generated;
import org.project.admin.dto.request.ProductRequest;
import org.project.admin.dto.response.ProductResponse;
import org.project.admin.entity.Product;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T23:41:20+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product product = new Product();

        product.setProductType( request.getProductType() );
        product.setName( request.getName() );
        product.setDescription( request.getDescription() );
        product.setPrice( request.getPrice() );
        product.setUnit( request.getUnit() );
        product.setProductStatus( request.getProductStatus() );
        if ( request.getStockQuantities() != null ) {
            product.setStockQuantities( request.getStockQuantities() );
        }
        product.setImageUrl( request.getImageUrl() );
        product.setLabel( request.getLabel() );

        return product;
    }

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProductId( product.getProductId() );
        productResponse.setProductType( product.getProductType() );
        productResponse.setName( product.getName() );
        productResponse.setDescription( product.getDescription() );
        productResponse.setPrice( product.getPrice() );
        productResponse.setUnit( product.getUnit() );
        productResponse.setProductStatus( product.getProductStatus() );
        productResponse.setStockQuantities( product.getStockQuantities() );
        productResponse.setImageUrl( product.getImageUrl() );
        productResponse.setLabel( product.getLabel() );

        return productResponse;
    }

    @Override
    public void updateProductFromRequest(ProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        if ( request.getProductType() != null ) {
            product.setProductType( request.getProductType() );
        }
        if ( request.getName() != null ) {
            product.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            product.setDescription( request.getDescription() );
        }
        if ( request.getPrice() != null ) {
            product.setPrice( request.getPrice() );
        }
        if ( request.getUnit() != null ) {
            product.setUnit( request.getUnit() );
        }
        if ( request.getProductStatus() != null ) {
            product.setProductStatus( request.getProductStatus() );
        }
        if ( request.getStockQuantities() != null ) {
            product.setStockQuantities( request.getStockQuantities() );
        }
        if ( request.getImageUrl() != null ) {
            product.setImageUrl( request.getImageUrl() );
        }
        if ( request.getLabel() != null ) {
            product.setLabel( request.getLabel() );
        }
    }
}
