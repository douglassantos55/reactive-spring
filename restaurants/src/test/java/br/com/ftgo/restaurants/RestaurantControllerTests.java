package br.com.ftgo.restaurants;

import br.com.ftgo.restaurants.entity.MenuItem;
import br.com.ftgo.restaurants.entity.Restaurant;
import br.com.ftgo.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
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
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", "kfc");
        builder.part("description", "kentuky fried chicken");

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("errors.menu").isEqualTo("must not be empty");
    }

    @Test
    void createWithMenu() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", "mcdonalds");
        builder.part("menu[0].name", "bigmac");
        builder.part("menu[0].price", "5.25");

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("name").isEqualTo("mcdonalds")
                .jsonPath("description").isEqualTo(null)
                .jsonPath("menu[0].name").isEqualTo("bigmac")
                .jsonPath("menu[0].price").isEqualTo(5.25);
    }

    @Test
    void createMenuWithoutName() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", "PizzaHut");
        builder.part("menu[0].price", "0.35");

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("{\"errors\":{\"menu.0.name\":\"must not be empty\"}}");
    }

    @Test
    void createMenuWithoutPrice() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", "PizzaHut");
        builder.part("menu[0].name", "mini pizza");

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("name").isEqualTo("PizzaHut")
                .jsonPath("menu[0].name").isEqualTo("mini pizza")
                .jsonPath("menu[0].price").isEqualTo(0.0);
    }

    @Test
    void createWithLogo() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", "McDonalds");
        builder.part("menu[0].name", "burger");
        builder.part("menu[0].price", "30.5");
        builder.part("logoFile", new ClassPathResource("/test.txt", RestaurantControllerTests.class));

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("logo")
                .isNotEmpty();
    }

    @Test
    void createWithoutLogo() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", "McDonalds");
        builder.part("menu[0].name", "mcburger");
        builder.part("menu[0].price", "50.5");

        client.post()
                .uri("/restaurants")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("name").isEqualTo("McDonalds")
                .jsonPath("logo").isEqualTo(null);
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

    @Test
    void updateInvalid() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("pizzeria");

        MenuItem pizza = new MenuItem();
        pizza.setName("generic pizza");
        pizza.setPrice(0.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);

        restaurant.setMenu(menu);

        client.put()
                .uri("/restaurants/15253")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(restaurant)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateNotFound() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("generic restaurant");

        MenuItem item = new MenuItem();
        item.setName("generic food");
        item.setPrice(0.1);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(item);

        restaurant.setMenu(menu);

        client.put()
                .uri("/restaurants/64a0b15c1f41eb7cc6d707d9")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(restaurant)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateWithoutMenu() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        Restaurant updated = new Restaurant();
        updated.setName("updated");
        updated.setDescription("no more mama mia");

        client.put()
                .uri("/restaurants/" + restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updated)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("{\"errors\":{\"menu\":\"must not be empty\"}}");
    }

    @Test
    void updateMenuWithoutName() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        Restaurant updated = new Restaurant();
        updated.setName("updated");
        updated.setDescription("no more mama mia");

        MenuItem nameless = new MenuItem();
        nameless.setPrice(10.7);

        ArrayList<MenuItem> updatedMenu = new ArrayList<>();
        updatedMenu.add(nameless);
        updated.setMenu(updatedMenu);

        client.put()
                .uri("/restaurants/" + restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updated)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("{\"errors\":{\"menu.0.name\":\"must not be empty\"}}");
    }

    @Test
    void updateEverything() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        Restaurant updated = new Restaurant();
        updated.setName("updated");
        updated.setBlocked(true);
        updated.setPhone("5 555 032273");
        updated.setAddress("narnia");
        updated.setWorkingHours("Monday to Friday - 19:00 to 23:00");
        updated.setDescription("no more mama mia");

        MenuItem nameless = new MenuItem();
        nameless.setName("not nameless");
        nameless.setPrice(10.7);

        MenuItem chicken = new MenuItem();
        chicken.setName("chicken");
        chicken.setPrice(7.55);

        ArrayList<MenuItem> updatedMenu = new ArrayList<>();
        updatedMenu.add(nameless);
        updatedMenu.add(chicken);
        updated.setMenu(updatedMenu);

        client.put()
                .uri("/restaurants/" + restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updated)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{" +
                        "\"name\":\"updated\"," +
                        "\"phone\":\"5 555 032273\"," +
                        "\"address\":\"narnia\"," +
                        "\"blocked\":true," +
                        "\"workingHours\":\"Monday to Friday - 19:00 to 23:00\"," +
                        "\"description\":\"no more mama mia\"," +
                        "\"menu\":[" +
                            "{\"name\":\"not nameless\",\"price\":10.7}, " +
                            "{\"name\":\"chicken\",\"price\":7.55}" +
                        "]" +
                "}");
    }

    @Test
    void getInvalid() {
        client.get()
                .uri("/restaurants/1515151")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getNonExistent() {
        client.get()
                .uri("/restaurants/64a0d538c2f01b2e0e6d849b")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getDeleted() {
        Restaurant restaurant = new Restaurant();
        restaurant.setDeletedAt(Instant.now());
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        client.get()
                .uri("/restaurants/" + restaurant.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getBlocked() {
        Restaurant restaurant = new Restaurant();
        restaurant.setBlocked(true);
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        client.get()
                .uri("/restaurants/" + restaurant.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void get() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        MenuItem nugget = new MenuItem();
        nugget.setName("nugget");
        nugget.setPrice(10.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);
        menu.add(nugget);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        client.get()
                .uri("/restaurants/" + restaurant.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{\"name\":\"Mama mia\",\"menu\":[{\"name\":\"mama pizza\",\"price\":20.7},{\"name\":\"nugget\",\"price\":10.7}]}");
    }

    @Test
    void deleteInvalid() {
        client.delete()
                .uri("/restaurants/15230")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteNonExistent() {
        client.delete()
                .uri("/restaurants/64a1bd217485272172fccb7d")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void delete() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Mama mia");

        MenuItem pizza = new MenuItem();
        pizza.setName("mama pizza");
        pizza.setPrice(20.7);

        MenuItem nugget = new MenuItem();
        nugget.setName("nugget");
        nugget.setPrice(10.7);

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(pizza);
        menu.add(nugget);

        restaurant.setMenu(menu);
        restaurant = repository.save(restaurant).block();

        client.delete()
                .uri("/restaurants/" + restaurant.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
