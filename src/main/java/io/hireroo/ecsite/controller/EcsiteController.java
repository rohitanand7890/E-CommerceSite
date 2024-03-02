package io.hireroo.ecsite.controller;

import io.hireroo.ecsite.dto.CreateItem;
import io.hireroo.ecsite.dto.CreateOrder;
import io.hireroo.ecsite.dto.CreateUser;
import io.hireroo.ecsite.entity.Item;
import io.hireroo.ecsite.entity.ResponseMessage;
import io.hireroo.ecsite.entity.User;
import io.hireroo.ecsite.exception.InsufficientItemStockException;
import io.hireroo.ecsite.exception.InsufficientUserBalanceException;
import io.hireroo.ecsite.exception.ItemNotFoundException;
import io.hireroo.ecsite.exception.UserNotFoundException;
import io.hireroo.ecsite.service.EcsiteService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping(path = "/api")
public class EcsiteController {
    @Autowired
    EcsiteService ecsiteService;

    @GetMapping(path = "/item/{id}")
    @ResponseBody
    public Item getItem(@PathVariable String id, HttpServletResponse response) {
        Item item = ecsiteService.getItem(id);
        if (item == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return item;
    }

    @GetMapping(path = "/user/{id}")
    @ResponseBody
    public User getUser(@PathVariable String id, HttpServletResponse response) {
        User user = ecsiteService.getUser(id);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return user;
    }

    @PostMapping(path = "/item")
    @ResponseBody
    public Item createItem(@RequestBody CreateItem createItem, HttpServletResponse response) {
        Item item = ecsiteService.getItem(createItem.getId());
        if (item != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return ecsiteService.createItem(createItem);
    }

    @PostMapping(path = "/user")
    @ResponseBody
    public User createUser(@RequestBody CreateUser createUser, HttpServletResponse response) {
        User user = ecsiteService.getUser(createUser.getId());
        if (user != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return ecsiteService.createUser(createUser);
    }

    @DeleteMapping(path = "/item/{id}")
    @ResponseBody
    public void deleteItem(@PathVariable String id, HttpServletResponse response) {
        Item item = ecsiteService.getItem(id);
        if (item == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        ecsiteService.deleteItem(id);
    }

    @DeleteMapping(path = "/user/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable String id, HttpServletResponse response) {
        User user = ecsiteService.getUser(id);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        ecsiteService.deleteUser(id);
    }

    @PostMapping(path = "/item/{item_id}/buy")
    @ResponseBody
    public ResponseMessage createOrder(
            @PathVariable("item_id") @Pattern(regexp = "^[A-Za-z0-9_]{1,8}$", message = "Item id must be max 8 characters") String itemId,
            @RequestBody @Valid CreateOrder createOrder
    ) throws InsufficientItemStockException, InsufficientUserBalanceException, UserNotFoundException, ItemNotFoundException {
        User user = ecsiteService.getUser(createOrder.getUserId());
        Item item = ecsiteService.getItem(itemId);

        // Validate if User with provided id exists
        if (user == null) {
            throw new UserNotFoundException("User with id :"+createOrder.getUserId()+" not found");
        }
        // Validate if Item with id exists
        if (item == null) {
            throw new ItemNotFoundException("Item with id :"+itemId+" not found");
        }

        createOrder.setItemId(itemId);
        return ecsiteService.createOrder(createOrder);
    }
}
