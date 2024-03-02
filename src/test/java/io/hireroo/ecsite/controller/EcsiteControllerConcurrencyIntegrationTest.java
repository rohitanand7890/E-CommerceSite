package io.hireroo.ecsite.controller;

import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.entity.Item;
import io.hireroo.ecsite.entity.User;
import io.hireroo.ecsite.repository.EcsiteMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class EcsiteControllerConcurrencyIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EcsiteMapper ecsiteMapper;

    @Test
    @Order(1)
    public void createUser_whenCreatingMultipleUsers_thenUsersAreCreated() {
        //Create 9 Test Users
        for (int i = 1; i <= 9; i++) {
            CreateUser createUser = new CreateUser();
            createUser.setId("USER_ID" + i);
            createUser.setSavings(1000);
            createUser.setName("User Name " + i);

            String url1 = "http://localhost:" + port + "/api/user";
            ResponseEntity<String> response = restTemplate.postForEntity(url1, createUser, String.class);

            assertEquals(200, response.getStatusCode().value());
        }
    }

    @Test
    @Order(2)
    public void createItem_whenCreatingItemWithStock_thenItemIsCreated() {
        // Create test Item with Stock quantity 1
        CreateItem createItem = new CreateItem();
        createItem.setId("ITEM1234");
        createItem.setName("Macbook");
        createItem.setDescription("This is Apple's 2023 Flagship M3 Pro Variant");
        createItem.setStock(1);
        createItem.setPrice(200);

        String url2 = "http://localhost:" + port + "/api/item";
        ResponseEntity<String> response = restTemplate.postForEntity(url2, createItem, String.class);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(3)
    public void createOrder_whenConcurrentOrderRequestsForLimitedStock_thenOnlyOneOrderIsCreated() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(9);

        for (int i = 1; i <= 9; i++) {
            final int finalI = i;
            new Thread(() -> {
                CreateOrder createOrder = new CreateOrder();
                createOrder.setUserId("USER_ID" + finalI);
                createOrder.setQuantity(1);
                createOrder.setItemId("ITEM1234");

                makeCreateOrderRequest(createOrder, latch);
            }, "Thread-" + i).start();
        }

        latch.await();

        Integer orderCount = ecsiteMapper.getOrderCountByItemId("ITEM1234");
        assertEquals(1, orderCount);
    }


    private void makeCreateOrderRequest(CreateOrder createOrder, CountDownLatch latch) {
        try {
            String url = "http://localhost:" + port + "/api/item/" + createOrder.getItemId() + "/buy";
            ResponseEntity<String> response = restTemplate.postForEntity(url, createOrder, String.class);

            // Just to log the Order creation response
            System.out.println("Order creation response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    @Test
    @Order(4)
    public void deleteAllOrders_whenOrdersExist_thenOrdersAreDeleted() {
        ecsiteMapper.deleteAllOrders();

        // Verify that orders are deleted.
        assertEquals(0, ecsiteMapper.getOrderCountByItemId("ITEM1234"));
    }

    @Test
    @Order(5)
    public void deleteItem_whenItemExists_thenItemIsDeleted() {
        String itemId = "ITEM1234";
        String url = "http://localhost:" + port + "/api/item/" + itemId;
        restTemplate.delete(url);

        // Check if the Item with id: ITEM1234 still exists, should return 404
        int responseStatusCode = restTemplate.getForEntity(url, Item.class, String.class).getStatusCode().value();
        assertEquals(404, responseStatusCode);
    }

    @Test
    @Order(6)
    public void deleteUsers_whenUsersExist_thenUsersAreDeleted() {
        for (int i = 1; i <= 9; i++) {
            String userId = "USER_ID" + i;
            String url = "http://localhost:" + port + "/api/user/" + userId;
            restTemplate.delete(url);

            // Check if the User still exists, should return 404
            int responseStatusCode = restTemplate.getForEntity(url, User.class, String.class).getStatusCode().value();
            assertEquals(404, responseStatusCode);
        }
    }
}
