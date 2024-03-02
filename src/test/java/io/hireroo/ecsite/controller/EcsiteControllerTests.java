package io.hireroo.ecsite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.entity.Item;
import io.hireroo.ecsite.entity.User;
import io.hireroo.ecsite.exception.InsufficientItemStockException;
import io.hireroo.ecsite.exception.InsufficientUserBalanceException;
import io.hireroo.ecsite.exception.ItemNotFoundException;
import io.hireroo.ecsite.exception.UserNotFoundException;
import io.hireroo.ecsite.service.EcsiteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EcsiteController.class)
public class EcsiteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EcsiteService ecsiteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getItem_whenItemExists_returnsItem() throws Exception {
        Item mockItem = new Item();
        mockItem.setId("ITM12345");
        given(ecsiteService.getItem("ITM12345")).willReturn(mockItem);

        mockMvc.perform(get("/api/item/ITM12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ITM12345"));
    }

    @Test
    public void getItem_whenItemDoesNotExist_returnsNotFound() throws Exception {
        given(ecsiteService.getItem("ITM12345")).willReturn(null);

        mockMvc.perform(get("/api/item/ITM12345"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUser_whenUserExists_returnsUser() throws Exception {
        User mockUser = new User();
        mockUser.setId("USR1234");
        given(ecsiteService.getUser("USR1234")).willReturn(mockUser);

        mockMvc.perform(get("/api/user/USR1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("USR1234"));
    }

    @Test
    public void getUser_whenUserDoesNotExist_returnsNotFound() throws Exception {
        given(ecsiteService.getUser("USR1234")).willReturn(null);

        mockMvc.perform(get("/api/user/USR1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createItem_whenNewItem_returnsItem() throws Exception {
        CreateItem createItem = new CreateItem();
        Item newItem = new Item();
        given(ecsiteService.createItem(any(CreateItem.class))).willReturn(newItem);

        mockMvc.perform(post("/api/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItem)))
                .andExpect(status().isOk());
    }

    @Test
    public void createUser_whenNewUser_returnsUser() throws Exception {
        CreateUser createUser = new CreateUser();
        User newUser = new User();
        given(ecsiteService.createUser(any(CreateUser.class))).willReturn(newUser);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteItem_whenItemExists_performsDeletion() throws Exception {
        String itemId = "ITM12345";
        Item mockItem = new Item();
        mockItem.setId(itemId);
        given(ecsiteService.getItem("ITM12345")).willReturn(mockItem);
        doNothing().when(ecsiteService).deleteItem(itemId);


        mockMvc.perform(delete("/api/item/" + itemId))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_whenUserExists_performsDeletion() throws Exception {
        String userId = "USR1234";
        User mockUser = new User();
        mockUser.setId(userId);
        given(ecsiteService.getUser("USR1234")).willReturn(mockUser);
        doNothing().when(ecsiteService).deleteUser(userId);

        mockMvc.perform(delete("/api/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    public void createOrder_whenStockInsufficient_returnsBadRequest() throws Exception {
        CreateOrder createOrder = new CreateOrder();
        given(ecsiteService.createOrder(any(CreateOrder.class)))
                .willThrow(new InsufficientItemStockException("Insufficient stock"));

        mockMvc.perform(post("/api/item/ITM12345/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrder_whenUserBalanceInsufficient_returnsBadRequest() throws Exception {
        CreateOrder createOrder = new CreateOrder();
        given(ecsiteService.createOrder(any(CreateOrder.class)))
                .willThrow(new InsufficientUserBalanceException("Insufficient balance"));

        mockMvc.perform(post("/api/item/ITM12345/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrder_whenUserNotFound_throwsUserNotFoundException() throws Exception {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setItemId("ITM12345");
        createOrder.setUserId("USR1234");
        createOrder.setQuantity(2);
        given(ecsiteService.createOrder(createOrder))
                .willThrow(new UserNotFoundException("User USR1234 not found"));

        mockMvc.perform(post("/api/item/ITM12345/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrder)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createOrder_whenItemNotFound_throwsItemNotFoundException() throws Exception {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setItemId("ITM12345");
        createOrder.setUserId("USR1234");
        createOrder.setQuantity(2);

        given(ecsiteService.createOrder(createOrder))
                .willThrow(new ItemNotFoundException("Item ITM12345 not found"));

        mockMvc.perform(post("/api/item/ITM12345/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrder)))
                .andExpect(status().isNotFound());
    }
}
