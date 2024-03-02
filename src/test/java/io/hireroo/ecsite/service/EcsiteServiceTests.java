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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EcsiteServiceTests {

    @Mock
    private EcsiteMapper ecsiteMapper;

    @InjectMocks
    private EcsiteService ecsiteService;

    @Test
    void getItem_whenExists_returnsItem() {
        Item expectedItem = new Item();
        when(ecsiteMapper.getItem("ITM12345")).thenReturn(expectedItem);

        Item result = ecsiteService.getItem("ITM12345");

        assertEquals(expectedItem, result);
        verify(ecsiteMapper).getItem("ITM12345");
    }

    @Test
    void getUser_whenExists_returnsUser() {
        User expectedUser = new User();
        when(ecsiteMapper.getUser("USR123")).thenReturn(expectedUser);

        User result = ecsiteService.getUser("USR123");

        assertEquals(expectedUser, result);
        verify(ecsiteMapper).getUser("USR123");
    }

    @Test
    void createItem_createsAndReturnsItem() {
        CreateItem createItem = new CreateItem();
        createItem.setId("ITM12345");
        Item expectedItem = new Item();
        when(ecsiteMapper.getItem(createItem.getId())).thenReturn(expectedItem);

        Item result = ecsiteService.createItem(createItem);

        assertEquals(expectedItem, result);
        verify(ecsiteMapper).createItem(createItem);
    }

    @Test
    void createUser_createsAndReturnsUser() {
        CreateUser createUser = new CreateUser();
        createUser.setId("USR123");
        User expectedUser = new User();
        when(ecsiteMapper.getUser(createUser.getId())).thenReturn(expectedUser);

        User result = ecsiteService.createUser(createUser);

        assertEquals(expectedUser, result);
        verify(ecsiteMapper).createUser(createUser);
    }

    @Test
    void deleteItem_whenItemExists_performsDeletion() {
        ecsiteService.deleteItem("ITM12345");
        verify(ecsiteMapper).deleteItem("ITM12345");
    }

    @Test
    void deleteUser_whenUserExists_performsDeletion() {
        ecsiteService.deleteUser("USR123");
        verify(ecsiteMapper).deleteUser("USR123");
    }

    @Test
    void createOrder_withSufficientStockAndBalance_createsOrder() throws Exception {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setItemId("ITM12345");
        createOrder.setUserId("USR123");
        createOrder.setQuantity(2);

        Item item = new Item();
        item.setPrice(100);
        item.setStock(10);

        User user = new User();
        user.setSavings(500);

        when(ecsiteMapper.getItem("ITM12345")).thenReturn(item);
        when(ecsiteMapper.getUser("USR123")).thenReturn(user);

        ResponseMessage response = ecsiteService.createOrder(createOrder);

        assertEquals("success", response.getMessage());
        verify(ecsiteMapper).updateUserSavings(anyInt(), eq("USR123"));
        verify(ecsiteMapper).updateItemStock(anyInt(), eq("ITM12345"));
        verify(ecsiteMapper).createOrder(createOrder);
    }

    @Test
    void createOrder_withInsufficientStock_throwsException() {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setItemId("ITM12345");
        createOrder.setUserId("USR123");
        createOrder.setQuantity(20);

        Item item = new Item();
        item.setPrice(100);
        item.setStock(10);

        User user = new User();
        user.setSavings(2000);

        when(ecsiteMapper.getItem("ITM12345")).thenReturn(item);
        when(ecsiteMapper.getUser("USR123")).thenReturn(user);

        assertThrows(InsufficientItemStockException.class, () -> ecsiteService.createOrder(createOrder));
    }

    @Test
    void createOrder_withInsufficientBalance_throwsException() {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setItemId("ITM12345");
        createOrder.setUserId("USR123");
        createOrder.setQuantity(2);

        Item item = new Item();
        item.setPrice(500);
        item.setStock(20);

        User user = new User();
        user.setSavings(200);

        when(ecsiteMapper.getItem("ITM12345")).thenReturn(item);
        when(ecsiteMapper.getUser("USR123")).thenReturn(user);

        assertThrows(InsufficientUserBalanceException.class, () -> ecsiteService.createOrder(createOrder));
    }
}
