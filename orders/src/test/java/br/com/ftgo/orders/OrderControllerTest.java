package br.com.ftgo.orders;

import br.com.ftgo.orders.dto.CardInformation;
import br.com.ftgo.orders.dto.OrderDTO;
import br.com.ftgo.orders.entity.*;
import br.com.ftgo.orders.repository.CustomersRepository;
import br.com.ftgo.orders.repository.OrdersRepository;
import br.com.ftgo.orders.repository.RestaurantsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;

@SpringBootTest
public class OrderControllerTest {
    private WebTestClient client;

    @Autowired
    private RestaurantsRepository restaurantsRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @BeforeEach
    public void setUp(ReactiveWebApplicationContext context) {
        ordersRepository.deleteAll().block();
        customersRepository.deleteAll().block();
        restaurantsRepository.deleteAll().block();
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void createNoCustomer() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setDescription("item");
        item.setPrice(15);
        item.setQty(10);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("errors.customerId").isEqualTo("must not be null")
                .jsonPath("errors.restaurantId").doesNotExist();
    }

    @Test
    void createNoRestaurant() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        OrderItem item = new OrderItem();
        item.setDescription("item");
        item.setPrice(15);
        item.setQty(10);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("errors.restaurantId").isEqualTo("must not be empty")
                .jsonPath("errors.customerId").doesNotExist();
    }

    @Test
    void createInvalidRestaurant() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        OrderItem item = new OrderItem();
        item.setDescription("item");
        item.setPrice(15);
        item.setQty(10);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId("aoeu");
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("errors.restaurantId").isEqualTo("does not exist")
                .jsonPath("errors.customerId").doesNotExist();
    }

    @Test
    void createInvalidCustomer() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setDescription("item");
        item.setPrice(15);
        item.setQty(10);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(1L);
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("errors.customerId").isEqualTo("does not exist")
                .jsonPath("errors.restaurantId").doesNotExist();
    }

    @Test
    void createNoItems() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("pix");

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("errors.items").isEqualTo("must not be empty")
                .jsonPath("errors.restaurantId").doesNotExist()
                .jsonPath("errors.customerId").doesNotExist();
    }

    @Test
    void createInvalidQty() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setDescription("item");
        item.setPrice(15);
        item.setQty(-1);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$['errors']['items.0.qty']").isEqualTo("must be greater than or equal to 0")
                .jsonPath("errors.restaurantId").doesNotExist()
                .jsonPath("errors.customerId").doesNotExist();
    }

    @Test
    @AutoConfigureWebTestClient(timeout = "6000")
    void createInvalidCardNumber() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setQty(1);
        item.setPrice(30.0);
        item.setDescription("item");

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("cc");
        order.setCard(new CardInformation("4129-9939-1834-8256", "Owen Hansen", "03/2023", "987"));
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$['errors']['card.number']").isEqualTo("invalid credit card number")
                .jsonPath("$['errors']['card.holderName']").doesNotExist()
                .jsonPath("$['errors']['card.cvv']").doesNotExist()
                .jsonPath("$['errors']['card.expDate']").doesNotExist();
    }

    @Test
    void createInvalidDescription() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setQty(1);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$['errors']['items.0.description']").isEqualTo("must not be empty")
                .jsonPath("errors.restaurantId").doesNotExist()
                .jsonPath("errors.customerId").doesNotExist();
    }

    @Test
    void createNoCreditCard() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setQty(1);
        item.setPrice(30.0);
        item.setDescription("item");

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("cc");
        order.setCard(new CardInformation("", "", "aoeu", ""));
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$['errors']['card.number']").isEqualTo("invalid credit card number")
                .jsonPath("$['errors']['card.holderName']").isEqualTo("must not be empty")
                .jsonPath("$['errors']['card.cvv']").isEqualTo("size must be between 3 and 4")
                .jsonPath("$['errors']['card.expDate']").isEqualTo("Expiration date must be MM/YYYY");
    }

    @Test
    void createCreditCard() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setQty(1);
        item.setPrice(30.0);
        item.setDescription("item");

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("cc");
        order.setCard(new CardInformation("4129993918348255", "Owen Hansen", "03/2023", "987"));
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("paymentType").isEqualTo("cc")
                .jsonPath("customer").isNotEmpty()
                .jsonPath("restaurant").isNotEmpty();
    }

    @Test
    void create() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        OrderItem item = new OrderItem();
        item.setQty(1);
        item.setPrice(305);
        item.setDescription("item");

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(item);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(restaurant.getId());
        order.setPaymentType("pix");
        order.setItems(items);

        client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("errors").doesNotExist()
                .jsonPath("customer.id").isEqualTo(1L)
                .jsonPath("restaurant.id").isEqualTo("mcdonalds")
                .jsonPath("status").isEqualTo("PENDING")
                .jsonPath("items.*.description").isEqualTo("item")
                .jsonPath("items.*.price").isEqualTo(305.0)
                .jsonPath("items.*.qty").isEqualTo(1)
                .jsonPath("createdAt").isNotEmpty()
                .jsonPath("updatedAt").isNotEmpty();
    }

    @Test
    void cancelNotFound() {
        client.delete()
                .uri("/orders/aoeu")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void cancelCancelled() {
        Order order = new Order();
        order.setId("order1");
        order.setStatus(OrderStatus.CANCELLED);

        ordersRepository.save(order).block();

        client.delete()
                .uri("/orders/" + order.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void cancel() {
        Order order = new Order();
        order.setId("order1");
        order.setStatus(OrderStatus.PENDING);

        ordersRepository.save(order).block();

        client.delete()
                .uri("/orders/" + order.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getNotFound() {
        client.get()
                .uri("/orders/someid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void get() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        Order order = new Order();
        order.setId("order1");
        order.setCustomerId(1L);
        order.setRestaurantId("mcdonalds");
        order.setStatus(OrderStatus.REFUNDED);

        ordersRepository.save(order).block();

        client.get()
                .uri("/orders/" + order.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("status").isEqualTo("REFUNDED")
                .jsonPath("customer").isNotEmpty()
                .jsonPath("restaurant").isNotEmpty();
    }
}
