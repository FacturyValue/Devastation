package com.data.cboard.service.Impl;

import com.data.cboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void create(String name, Integer age) {
                jdbcTemplate.update("INSERT  INTO  USER(NAME ,AGE)  VALUES (?,?)" ,name,age );
    }

    @Override
    public void deleteByName(String name) {
                jdbcTemplate.update("DELETE  FROM  USER  WHERE  NAME = ?",name);
    }

    @Override
    public Integer getAllUsers() {
        return jdbcTemplate.queryForObject("select count(1) from USER",Integer.class);
    }

    @Override
    public void deleteAllUsers() {
            jdbcTemplate.update("DELETE  FROM  USER ");
    }
}
