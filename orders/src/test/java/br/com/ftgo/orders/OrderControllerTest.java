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
        order.setPaymentType("credit_card");
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
                .jsonPath("$['errors']['card.number']").isEqualTo("must not be empty")
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

    @Test
    void listNoMatchRestaurantFilter() {
        client.get()
                .uri("/orders?restaurantId=mcdonalds")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("[]");
    }

    @Test
    void listNoMatchStatusFilter() {
        client.get()
                .uri("/orders?status=PENDING")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("[]");
    }

    @Test
    void listInvalidStatusFilter() {
        client.get()
                .uri("/orders?status=TRANSCENDED")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void listEmptyNoFilters() {
        client.get()
                .uri("/orders")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("[]");
    }

    @Test
    void listNoFilters() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        Order order1 = new Order();
        order1.setCustomerId(1L);
        order1.setRestaurantId("mcdonalds");

        ordersRepository.save(order1).block();

        Order order2 = new Order();
        order2.setCustomerId(1L);
        order2.setRestaurantId("mcdonalds");

        ordersRepository.save(order2).block();

        client.get()
                .uri("/orders")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("[0]").isNotEmpty()
                .jsonPath("[1]").isNotEmpty();
    }

    @Test
    void listFilterRestaurant() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        Restaurant pizzahut = new Restaurant();
        pizzahut.setId("pizzahut");
        pizzahut.setName("PizzaHut");

        restaurantsRepository.save(pizzahut).block();

        Order order1 = new Order();
        order1.setCustomerId(1L);
        order1.setRestaurantId("mcdonalds");

        ordersRepository.save(order1).block();

        Order order2 = new Order();
        order2.setCustomerId(1L);
        order2.setRestaurantId("pizzahut");

        ordersRepository.save(order2).block();

        client.get()
                .uri("/orders?restaurantId=pizzahut")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("[0]").isNotEmpty()
                .jsonPath("[1]").doesNotExist();
    }

    @Test
    void listCustomerFilter()
    {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("testing 2");

        customersRepository.save(customer2).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        Order order1 = new Order();
        order1.setCustomerId(1L);
        order1.setRestaurantId("mcdonalds");

        ordersRepository.save(order1).block();

        Order order2 = new Order();
        order2.setCustomerId(2L);
        order2.setRestaurantId("mcdonalds");

        ordersRepository.save(order2).block();

        client.get()
                .uri("/orders?customerId=2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("[0]").isNotEmpty()
                .jsonPath("[1]").doesNotExist();
    }

    @Test
    void listFilterStatus() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        Order order1 = new Order();
        order1.setCustomerId(1L);
        order1.setRestaurantId("mcdonalds");
        order1.setStatus(OrderStatus.CANCELLED);

        ordersRepository.save(order1).block();

        Order order2 = new Order();
        order2.setCustomerId(1L);
        order2.setRestaurantId("mcdonalds");
        order2.setStatus(OrderStatus.COMPLETED);

        ordersRepository.save(order2).block();

        client.get()
                .uri("/orders?status=COMPLETED")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("[0]").isNotEmpty()
                .jsonPath("[1]").doesNotExist();
    }

    @Test
    void listPagination() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("testing");

        customersRepository.save(customer).block();

        Restaurant restaurant = new Restaurant();
        restaurant.setId("mcdonalds");
        restaurant.setName("McDonald's");

        restaurantsRepository.save(restaurant).block();

        Order order1 = new Order();
        order1.setId("order1");
        order1.setCustomerId(1L);
        order1.setRestaurantId("mcdonalds");
        order1.setStatus(OrderStatus.CANCELLED);

        ordersRepository.save(order1).block();

        Order order2 = new Order();
        order2.setId("order2");
        order2.setCustomerId(1L);
        order2.setRestaurantId("mcdonalds");
        order2.setStatus(OrderStatus.PENDING);

        ordersRepository.save(order2).block();

        Order order3 = new Order();
        order3.setId("order3");
        order3.setCustomerId(1L);
        order3.setRestaurantId("mcdonalds");
        order3.setStatus(OrderStatus.REFUNDED);

        ordersRepository.save(order3).block();

        Order order4 = new Order();
        order4.setId("order4");
        order4.setCustomerId(1L);
        order4.setRestaurantId("mcdonalds");
        order4.setStatus(OrderStatus.DELIVERY);

        ordersRepository.save(order4).block();

        Order order5 = new Order();
        order5.setId("order5");
        order5.setCustomerId(1L);
        order5.setRestaurantId("mcdonalds");
        order5.setStatus(OrderStatus.COMPLETED);

        ordersRepository.save(order5).block();

        System.out.println(ordersRepository.count().block());

        client.get()
                .uri("/orders?perPage=3")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("[0]").isNotEmpty()
                .jsonPath("[1]").isNotEmpty()
                .jsonPath("[2]").isNotEmpty()
                .jsonPath("[3]").doesNotExist();
    }
}
