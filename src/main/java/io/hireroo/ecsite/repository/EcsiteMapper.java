package io.hireroo.ecsite.repository;

import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.entity.Item;
import io.hireroo.ecsite.entity.Order;
import io.hireroo.ecsite.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EcsiteMapper {
    Item getItem(String itemId);
    User getUser(String userId);
    void createItem(CreateItem item);
    void createUser(CreateUser user);
    void deleteItem(String itemId);
    void deleteUser(String userId);
    void createOrder(CreateOrder order);
    void updateUserSavings(Integer savings, String userId);
    void updateItemStock(Integer stock, String itemId);
    Integer getOrderCountByItemId(String itemId);
    void deleteAllOrders();
}
