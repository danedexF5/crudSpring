package com.theironyard;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CrudSpringController {

        @Autowired
        AlcoholRepo drinks;
        @Autowired
        UserRepo users;

        //this starts method
        @PostConstruct
        public void init() throws InvalidKeySpecException, NoSuchAlgorithmException, PasswordStorage.CannotPerformOperationException {
            User user = users.findOneUserByName("Bossman");
            if(user == null){
                user = new User();
                user.name="Bossman";

                //not sure what is going on here
                user.password = PasswordStorage.createHash("letmein");

                users.save(user);
            }
        }

        @RequestMapping("/")
        public String home(HttpSession session, Model model, String type, Integer calories, String search, String showMine){
            //get session
            String username = (String) session.getAttribute("username");

            //if u doesnt exist
            /*if (username == null){
                return "login";
            }*/
            model.addAttribute("username", username);

            List<Alcohol> drinkList = null;

            if (showMine != null){
                drinkList = (users.findOneUserByName(username).drinks);
            }

            else if (search!= null){
                drinkList = (drinks.searchByName(search));
            }
            else if (type !=null && calories!=null){
                drinkList = (drinks.findByTypeDrinkAndCalories(type, calories));
            }
            else if (type != null){
                drinkList = (drinks.findByTypeDrinkOrderByName(type));
            }
            else {
                drinkList = new ArrayList<Alcohol>();

                drinks.findAll().forEach(drinkList::add);
            }

            for(Alcohol a: drinkList){
                if(a.user.name.equals(username)){
                    a.editable = true;
                }
            }

            model.addAttribute("drinks", drinkList);

            return "home";
        }

        @RequestMapping("addDrink")
        public String addDrink(HttpSession session, String drinkName, String anotherDrinkType, Integer drinkCalories) throws Exception {

            String userName = (String) session.getAttribute("username");
            if (userName == null){

                throw new Exception("You are not logged into your account");
            }
            //find user by name
            User user = users.findOneUserByName(userName);

            //new drink
            Alcohol drink = new Alcohol();

            //parameters
            drink.name = drinkName;
            drink.typeDrink = anotherDrinkType;
            drink.calories = drinkCalories;
            drink.user = user;

            //save into the repo
            drinks.save(drink);

            //go back to root
            return "redirect:/";
        }

        @RequestMapping(path = "editDrinks", method = RequestMethod.POST)
        public String editDrinks(HttpSession session, int id, String name, String type, int calories) throws Exception {

            if (session.getAttribute("username")==null){

                throw new Exception("Not logged in!");
            }

            //find a drink by id
            Alcohol drink = drinks.findOne(id);
            if (!drink.user.name.equals(session.getAttribute("username"))){
                throw new Exception("Not a valid password!");
            }

            drink.name = name;

            drink.typeDrink = type;

            drink.calories = calories;

            //save into the repo
            drinks.save(drink);

            //return to root
            return "redirect:/";
        }

    @RequestMapping(path = "editDrinks", method = RequestMethod.GET)
    public String editDrinks(HttpSession session, int id, Model model) throws Exception {
        if (session.getAttribute("username")==null){

            throw new Exception("Not logged in!");
        }

        Alcohol drink = drinks.findOne(id);
        if (!drink.user.name.equals(session.getAttribute("username"))){
            throw new Exception("Not a valid password!");
        }
        model.addAttribute("username", session.getAttribute("username"));
       model.addAttribute("drink", drink);

        return "edit";

    }

    @RequestMapping(path = "deleteDrinks", method = RequestMethod.GET)
    public String deleteDrinks(HttpSession session, int id, Model model) throws Exception{

        if (session.getAttribute("username")==null){

            throw new Exception("Not logged in!");
        }

        Alcohol drink = drinks.findOne(id);
        if (!drink.user.name.equals(session.getAttribute("username"))){
            throw new Exception("Not a valid password!");
        }

        drinks.delete(drink);

        return "redirect:/";

    }

        @RequestMapping("login")
        public String login(HttpSession session, String u, String password) throws Exception {
            // HttpSession session = request.getSession();
            session.setAttribute("username", u);

            User user = users.findOneUserByName(u);
            if(user == null){

                //create new user
                user = new User();

                user.name = u;

                //password java file
                user.password = PasswordStorage.createHash(password);

                //save user to repo
                users.save(user);

                //if password doesn't pass
            } else if (!PasswordStorage.verifyPassword(password, user.password)){
                throw new Exception("Incorrect password. Please try again");
            }

            //return to root
            return "redirect:/";
        }
        @RequestMapping("logout")
        public String logout(HttpServletRequest request){

            HttpSession session = request.getSession();

            session.invalidate();

            return "redirect:/";
        }


    }


    //Choose something you'd like to "track" in a web app.
        //User authentication
        //If not logged in, show a login form at the top (it can double as your create account form).
        //If logged in, display the u and a logout button at the top.
        //Passwords MUST be stored securely.
        //Don't allow unauthenticated users to hit routes they shouldn't hit.
        //All data must be stored and manipulated using Spring Data (Hibernate).
        //Create: If logged in, display a form to create a new entry.
        //Read: Whether logged in or not, list whatever entries were created by the users.
        //Update: If logged in, show an edit link next to the entries created by that user.
        //Delete: If logged in, show a delete button next to the entries created by that user.
        //Deploy the app to Heroku
        //List<Alcohol> findByTypeDrink(String type);
        //List<Alcohol> findByTypeDrinkAndCaloriesIsLessThanEqual(String type, Integer calories);
        //List<Alcohol> findByTypeDrink(String type);