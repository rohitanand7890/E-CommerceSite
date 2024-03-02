package io.hireroo.ecsite.service;

import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.entity.Item;
import io.hireroo.ecsite.entity.ResponseMessage;
import io.hireroo.ecsite.entity.User;
import io.hireroo.ecsite.exception.InsufficientItemStockException;
import io.hireroo.ecsite.exception.InsufficientUserBalanceException;
import io.hireroo.ecsite.repository.EcsiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        String itemId = createOrder.getItemId();
        String userId = createOrder.getUserId();

        Item item = getItem(itemId);
        User user = getUser(userId);

        Integer orderQuantity = createOrder.getQuantity();
        Integer itemStock = item.getStock();

        boolean isStockSufficient = itemStock >= orderQuantity;
        if(!isStockSufficient) {
            throw new InsufficientItemStockException("Item stock not enough in the inventory, required :"+ orderQuantity +", available only :"+ itemStock);
        }

        var orderAmount = item.getPrice() * orderQuantity;
        Integer userSavings = user.getSavings();
        boolean isUserBalanceEnough = userSavings >= orderAmount;
        if(!isUserBalanceEnough) {
            throw new InsufficientUserBalanceException("User balance insufficient for the purchase amount");
        }

        //Debit order amount from User
        Integer updatedSavings = userSavings - orderAmount;
        ecsiteMapper.updateUserSavings(updatedSavings, userId);

        //Update item Stock
        var updatedStock = itemStock - orderQuantity;
        ecsiteMapper.updateItemStock(updatedStock, itemId);

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
