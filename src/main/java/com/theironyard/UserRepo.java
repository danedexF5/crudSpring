package com.theironyard;


import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
    User findOneUserByName(String name);

}
