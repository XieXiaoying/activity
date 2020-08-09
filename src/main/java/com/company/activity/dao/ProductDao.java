package com.company.activity.dao;

import com.company.activity.model.ProductModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductDao {

    @Select("select p.*,ap.stock_count, ap.start_date, ap.end_date,ap.current_price from activity_products ap left join products p on ap.products_id = p.id")
    List<ProductModel> getProductList();

    @Select("select p.*,ap.stock_count, ap.start_date, ap.end_date,ap.current_price from activity_products ap left join products p on ap.products_id = p.id where p.id = #{id}")
    ProductModel getProductById(long id);

    @Update("update activity_products set stock_count = stock_count - 1 where products_id = #{productsId} and stock_count > 0")
    int reduceStock(ProductModel product);
}
