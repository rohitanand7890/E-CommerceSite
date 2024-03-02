package io.hireroo.ecsite.service;

import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.entity.Item;
import io.hireroo.ecsite.entity.User;
import io.hireroo.ecsite.repository.EcsiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.hireroo.ecsite.entity.ResponseMessage;
import io.hireroo.ecsite.exception.InsufficientItemStockException;
import io.hireroo.ecsite.exception.InsufficientUserBalanceException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EcsiteService {
    @Autowired
    private EcsiteMapper ecsiteMapper;

    public Item getItem(String itemId) {
        return ecsiteMapper.getItem(itemId);
    }

    public User getUser(String userId) {
        return ecsiteMapper.getUser(userId);
    }

    public Item createItem(CreateItem createItem) {
        ecsiteMapper.createItem(createItem);
        return ecsiteMapper.getItem(createItem.getId());
    }

    public User createUser(CreateUser createUser) {
        ecsiteMapper.createUser(createUser);
        return ecsiteMapper.getUser(createUser.getId());
    }

    //Here, timeout can be configured based on db performance
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 6)
    public ResponseMessage createOrder(CreateOrder createOrder) throws InsufficientItemStockException, InsufficientUserBalanceException {

        Item item = getItem(createOrder.getItemId());
        User user = getUser(createOrder.getUserId());
        var orderAmount = item.getPrice() * createOrder.getQuantity();
        boolean isStockSufficient = item.getStock() >= createOrder.getQuantity();
        boolean isUserBalanceEnough = user.getSavings() >= orderAmount;

        if(!isStockSufficient) {
            throw new InsufficientItemStockException("Item stock not enough in the inventory, required :"+createOrder.getQuantity()+", available only :"+item.getStock());
        }
        if(!isUserBalanceEnough) {
            throw new InsufficientUserBalanceException("User balance insufficient for the purchase amount");
        }

        //Debit order amount from User
        Integer updatedSavings = user.getSavings() - orderAmount;
        ecsiteMapper.updateUserSavings(updatedSavings, createOrder.getUserId());

        //Update item Stock
        var updatedStock = item.getStock() - createOrder.getQuantity();
        ecsiteMapper.updateItemStock(updatedStock, createOrder.getItemId());

        //Create an Order
        createOrder.setAmount(orderAmount);
        ecsiteMapper.createOrder(createOrder);

        return new ResponseMessage("success");
    }

    public void deleteItem(String itemId) {
        ecsiteMapper.deleteItem(itemId);
    }

    public void deleteUser(String userId) {
        ecsiteMapper.deleteUser(userId);
    }
}
