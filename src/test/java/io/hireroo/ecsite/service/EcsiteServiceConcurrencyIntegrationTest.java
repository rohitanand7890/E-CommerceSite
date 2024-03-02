package io.hireroo.ecsite.service;

import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.exception.InsufficientItemStockException;
import io.hireroo.ecsite.exception.InsufficientUserBalanceException;
import io.hireroo.ecsite.repository.EcsiteMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class EcsiteServiceConcurrencyIntegrationTest {

    @Autowired
    private EcsiteMapper ecsiteMapper;

    @Autowired
    private EcsiteService ecsiteService;

    @AfterAll
    public void cleanup_afterTests() {
        ecsiteMapper.deleteAllOrders();
    }

    @Test
    @Order(1)
    public void createItem_whenNewItem_thenItemIsCreated() {
        CreateItem createItem = new CreateItem();
        createItem.setId("ITEM1234");
        createItem.setName("Macbook");
        createItem.setDescription("This is Apple's 2023 Flagship M3 Pro Variant");
        createItem.setStock(1);
        createItem.setPrice(200);

        ecsiteService.createItem(createItem);

        // Verify the item got created
        assertNotNull(ecsiteService.getItem("ITEM1234"));
    }

    @Test
    @Order(2)
    public void createUser_whenCreatingMultipleUsers_thenUsersAreCreated() {
        for (int i = 1; i <= 9; i++) {
            CreateUser createUser = new CreateUser();
            createUser.setId("USER_ID" + i);
            createUser.setSavings(1000);
            createUser.setName("User Name " + i);

            ecsiteService.createUser(createUser);

            // Verify each user got created
            assertNotNull(ecsiteService.getUser("USER_ID" + i));
        }
    }

    @Test
    @Order(3)
    public void createOrder_whenConcurrentOrderRequests_thenOnlyOneOrderIsSuccessful() throws Exception {
        CountDownLatch latch = new CountDownLatch(9);

        for (int i = 1; i <= 9; i++) {
            CreateOrder createOrder = new CreateOrder();
            createOrder.setUserId("USER_ID" + i);
            createOrder.setQuantity(1);
            createOrder.setItemId("ITEM1234");

            Thread thread = new Thread(() -> {
                makeCreateOrderRequest(createOrder, latch);
            }, "Thread-" + i);
            thread.start();
        }

        latch.await();

        Integer orderCount = ecsiteMapper.getOrderCountByItemId("ITEM1234");
        assertEquals(1, orderCount);
    }

    private void makeCreateOrderRequest(CreateOrder createOrder, CountDownLatch latch) {
        try {
            ecsiteService.createOrder(createOrder);
            System.out.println("Order creation success for " + Thread.currentThread().getName());
        } catch (InsufficientItemStockException | InsufficientUserBalanceException e) {
            System.out.println("Order creation failed for " + Thread.currentThread().getName() + ": " + e.getMessage());
        } finally {
            latch.countDown();
        }
    }

    @Test
    @Order(4)
    public void deleteUser_whenDeletingMultipleUsers_thenUsersAreDeleted() {
        for (int i = 1; i <= 9; i++) {
            ecsiteService.deleteUser("USER_ID" + i);

            // Verify each user got deleted
            assertNull(ecsiteService.getUser("USER_ID" + i));
        }
    }

    @Test
    @Order(5)
    public void deleteItem_whenItemExists_thenItemIsDeleted() {
        ecsiteService.deleteItem("ITEM1234");

        // Verify the item got deleted
        assertNull(ecsiteService.getItem("ITEM1234"));
    }
}
