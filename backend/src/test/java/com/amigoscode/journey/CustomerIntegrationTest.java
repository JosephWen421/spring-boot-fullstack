package com.amigoscode.journey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.amigoscode.customer.CustomerDTO;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.CustomerUpdateRequest;
import com.amigoscode.customer.Gender;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  private static final Random RANDOM = new Random();
  private static final String CUSTOMER_PATH = "/api/v1/customers";

  @Test
  void canRegisterCustomer() {
    // Create a registration request
    Faker faker = new Faker();
    Name fakerName = faker.name();
    String name = fakerName.fullName();
    String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
    int age = RANDOM.nextInt(1, 100);
    Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

    CustomerRegistrationRequest request = new CustomerRegistrationRequest(
        name, email, "password", age, gender
    );

    // Send a post request
    String jwtToken = webTestClient.post()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), CustomerRegistrationRequest.class)
        .exchange()
        .expectStatus()
        .isOk()
        .returnResult(Void.class)
        .getResponseHeaders()
        .get(AUTHORIZATION)
        .get(0);

    // Get all customers
    List<CustomerDTO> allCustomers = webTestClient.get()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
        })
        .returnResult()
        .getResponseBody();

    // Make sure customer is present
    int id = allCustomers.stream()
        .filter(customer -> customer.email().equals(email))
        .map(CustomerDTO::id)
        .findFirst()
        .orElseThrow();

    CustomerDTO expectedCustomer = new CustomerDTO(
        id, name, email, gender, age, List.of("ROLE_USER"), email
    );

    assertThat(allCustomers)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
        .contains(expectedCustomer);

    // Get customer by id
    webTestClient.get()
        .uri(CUSTOMER_PATH + "/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
        })
        .isEqualTo(expectedCustomer);
  }

  @Test
  void canDeleteCustomer() {
    // Create a registration request
    Faker faker = new Faker();
    Name fakerName = faker.name();
    String name = fakerName.fullName();
    String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
    int age = RANDOM.nextInt(1, 100);
    Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

    CustomerRegistrationRequest request = new CustomerRegistrationRequest(
        name, email, "password", age, gender
    );

    CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
        name, email + ".uk", "password", age, gender
    );

    // Send a post request
    webTestClient.post()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), CustomerRegistrationRequest.class)
        .exchange()
        .expectStatus()
        .isOk();

    String jwtToken = webTestClient.post()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request2), CustomerRegistrationRequest.class)
        .exchange()
        .expectStatus()
        .isOk()
        .returnResult(Void.class)
        .getResponseHeaders()
        .get(AUTHORIZATION)
        .get(0);

    // Get all customers
    List<CustomerDTO> allCustomers = webTestClient.get()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
        })
        .returnResult()
        .getResponseBody();

    int id = allCustomers.stream()
        .filter(customer -> customer.email().equals(email))
        .map(CustomerDTO::id)
        .findFirst()
        .orElseThrow();

    // Customer 2 delete customer 1
    webTestClient.delete()
        .uri(CUSTOMER_PATH + "/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isOk();

    // Customer 2 get customer 1
    webTestClient.get()
        .uri(CUSTOMER_PATH + "/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void canUpdateCustomer() {
    // Create a registration request
    Faker faker = new Faker();
    Name fakerName = faker.name();
    String name = fakerName.fullName();
    String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
    int age = RANDOM.nextInt(1, 100);
    Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

    CustomerRegistrationRequest request = new CustomerRegistrationRequest(
        name, email, "password", age, gender
    );

    // Send a post request
    String jwtToken = webTestClient.post()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), CustomerRegistrationRequest.class)
        .exchange()
        .expectStatus()
        .isOk().returnResult(Void.class)
        .getResponseHeaders()
        .get(AUTHORIZATION)
        .get(0);

    // Get all customers
    List<CustomerDTO> allCustomers = webTestClient.get()
        .uri(CUSTOMER_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
        })
        .returnResult()
        .getResponseBody();

    int id = allCustomers.stream()
        .filter(customer -> customer.email().equals(email))
        .map(CustomerDTO::id)
        .findFirst()
        .orElseThrow();

    // Get customer by id
    String newName = "Ali";
    CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
        newName, null, null, gender
    );

    webTestClient.put()
        .uri(CUSTOMER_PATH + "/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
        .exchange()
        .expectStatus()
        .isOk();

    // Get customer by id
    CustomerDTO updateCustomer = webTestClient.get()
        .uri(CUSTOMER_PATH + "/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(CustomerDTO.class)
        .returnResult()
        .getResponseBody();

    CustomerDTO expectedCustomer = new CustomerDTO(
        id, newName, email, gender, age, List.of("ROLE_USER"), email
    );

    assertThat(updateCustomer).isEqualTo(expectedCustomer);
  }
}
