package br.com.ftgo.restaurants;

import br.com.ftgo.restaurants.entity.MenuItem;
import br.com.ftgo.restaurants.entity.Restaurant;
import br.com.ftgo.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;

@SpringBootTest
public class RestaurantControllerTests {
    private WebTestClient client;

    @Autowired
    private RestaurantRepository repository;

    @BeforeEach
    void setUp(ReactiveWebApplicationContext context) {
        repository.deleteAll().subscribe();
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void createWithoutMenu() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("kfc");
        restaurant.setDescription("kentuky fried chicken");

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(restaurant)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("{\"errors\":{\"menu\":\"must not be empty\"}}");
    }

    @Test
    void createWithMenu() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("mcdonalds");

        MenuItem bigMac = new MenuItem();
        bigMac.setName("bigmac");
        bigMac.setPrice(5.25);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(bigMac);
        restaurant.setMenu(menu);

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(restaurant)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json("{\"name\":\"mcdonalds\",\"description\":null,\"menu\":[{\"name\":\"bigmac\",\"price\":5.25}]}");
    }

    @Test
    void createMenuWithoutName() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("PizzaHut");

        MenuItem miniPizza = new MenuItem();
        miniPizza.setPrice(0.35);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(miniPizza);
        restaurant.setMenu(menu);

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(restaurant)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("{\"errors\":{\"menu.0.name\":\"must not be empty\"}}");
    }

    @Test
    void createMenuWithoutPrice() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("PizzaHut");

        MenuItem miniPizza = new MenuItem();
        miniPizza.setName("mini pizza");

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(miniPizza);
        restaurant.setMenu(menu);

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(restaurant)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json("{\"name\":\"PizzaHut\",\"menu\":[{\"name\":\"mini pizza\",\"price\":0}]}");
    }

    @Test
    void list() {
        Restaurant tamago = new Restaurant();
        tamago.setName("Tamago");

        MenuItem tamagoyaki = new MenuItem();
        tamagoyaki.setName("tamagoyaki");

        ArrayList<MenuItem> tamagoMenu = new ArrayList<>();
        tamagoMenu.add(tamagoyaki);
        tamago.setMenu(tamagoMenu);

        Restaurant blocked = new Restaurant();
        blocked.setBlocked(true);
        blocked.setName("blocked");

        ArrayList<MenuItem> blockedMenu = new ArrayList<>();
        blockedMenu.add(tamagoyaki);
        blocked.setMenu(blockedMenu);

        Restaurant deleted = new Restaurant();
        deleted.setBlocked(true);
        deleted.setName("blocked");

        ArrayList<MenuItem> deletedMenu = new ArrayList<>();
        deletedMenu.add(tamagoyaki);
        deleted.setMenu(deletedMenu);

        ArrayList<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(tamago);
        restaurants.add(blocked);
        restaurants.add(deleted);

        repository.saveAll(restaurants).subscribe();

        client.get()
                .uri("/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[{\"name\":\"Tamago\",\"menu\":[{\"name\":\"tamagoyaki\",\"price\":0}]}]");
    }
}
