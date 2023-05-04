package com.srjons.templatejpa.batch;

import com.srjons.templatejpa.entity.User;
import org.springframework.batch.item.ItemProcessor;

public class UserProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User item) throws Exception {
        System.out.println(item.getUserId() + " is processed");
        return item;
    }
}
