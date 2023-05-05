package com.srjons.templatejpa.batch;

import com.srjons.templatejpa.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i)
            throws SQLException {
        User student=new User();
        student.setUserId(resultSet.getInt("user_id"));
        student.setUsername(resultSet.getString("username"));
        student.setPassword(resultSet.getString("password"));
        student.setEmail(resultSet.getString("email"));
        return student;
    }
}
