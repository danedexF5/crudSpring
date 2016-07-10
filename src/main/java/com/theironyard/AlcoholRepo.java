package com.theironyard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlcoholRepo extends CrudRepository<Alcohol, Integer> {


    List<Alcohol> findByTypeDrinkAndCalories(String typeDrink, Integer calories);


    Alcohol findFirstByTypeDrink(String typeDrink);
    int countByTypeDrink(String typeDrink);
    List<Alcohol> findByTypeDrinkOrderByName(String typeDrink);

    //% = wildcard(substitute for 0 or more char's)
    //?1 = first argument
    @Query("SELECT a FROM Alcohol a WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'")
    List<Alcohol> searchByName(String name);
}
