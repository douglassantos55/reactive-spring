package br.com.ftgo.restaurants;

import br.com.ftgo.restaurants.entity.MenuItem;
import br.com.ftgo.restaurants.entity.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;

@SpringBootTest
public class RestaurantControllerTests {
    private WebTestClient client;

    @BeforeEach
    void setUp(ReactiveWebApplicationContext context) {
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
}
