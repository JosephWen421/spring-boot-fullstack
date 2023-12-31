package com.amigoscode.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.amigoscode.AbstractTestContainers;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

  private CustomerJDBCDataAccessService underTest;
  private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

  @BeforeEach
  void setUp() {
    underTest = new CustomerJDBCDataAccessService(
        getJdbcTemplate(),
        customerRowMapper
    );
  }

  @Test
  void selectAllCustomers() {
    // Given
    Customer customer = new Customer(
        FAKER.name().fullName(),
        FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    // When
    List<Customer> actual = underTest.selectAllCustomers();

    // Then
    assertThat(actual).isNotEmpty();
  }

  @Test
  void selectCustomerById() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    // When
    Optional<Customer> actual = underTest.selectCustomerById(id);

    // Then
    assertThat(actual).isPresent().hasValueSatisfying(c -> {
      assertThat(c.getId()).isEqualTo(id);
      assertThat(c.getName()).isEqualTo(customer.getName());
      assertThat(c.getEmail()).isEqualTo(customer.getEmail());
      assertThat(c.getAge()).isEqualTo(customer.getAge());
    });
  }

  @Test
  void willReturnEmptyWhenSelectCustomerById() {
    // Given
    int id = -1;

    // When
    Optional<Customer> actual = underTest.selectCustomerById(id);

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void insertCustomer() {
    // Given

    // When

    // Then
  }

  @Test
  void existsPersonWithEmail() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    // When
    boolean actual = underTest.existsPersonWithEmail(email);

    // Then
    assertThat(actual).isTrue();
  }

  @Test
  void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();

    // When
    boolean actual = underTest.existsPersonWithEmail(email);

    // Then
    assertThat(actual).isFalse();
  }

  @Test
  void existsPersonWithId() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    // When
    boolean actual = underTest.existsPersonWithId(id);

    // Then
    assertThat(actual).isTrue();
  }

  @Test
  void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
    Integer id = -1;

    // When
    boolean actual = underTest.existsPersonWithId(id);

    // Then
    assertThat(actual).isFalse();
  }

  @Test
  void deleteCustomerById() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    // When
    underTest.deleteCustomerById(id);

    // Then
    Optional<Customer> actual = underTest.selectCustomerById(id);
    assertThat(actual).isNotPresent();
  }

  @Test
  void updateCustomerName() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    String newName = "foo";

    // When
    Customer update = new Customer();
    update.setId(id);
    update.setName(newName);

    underTest.updateCustomer(update);

    // Then
    Optional<Customer> actual = underTest.selectCustomerById(id);

    assertThat(actual).isPresent().hasValueSatisfying(c -> {
      assertThat(c.getId()).isEqualTo(id);
      assertThat(c.getName()).isEqualTo(newName);
      assertThat(c.getEmail()).isEqualTo(customer.getEmail());
      assertThat(c.getAge()).isEqualTo(customer.getAge());
    });
  }

  @Test
  void updateCustomerEmail() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    String newEmail = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();

    // When
    Customer update = new Customer();
    update.setId(id);
    update.setEmail(newEmail);

    underTest.updateCustomer(update);

    // Then
    Optional<Customer> actual = underTest.selectCustomerById(id);

    assertThat(actual).isPresent().hasValueSatisfying(c -> {
      assertThat(c.getId()).isEqualTo(id);
      assertThat(c.getName()).isEqualTo(customer.getName());
      assertThat(c.getEmail()).isEqualTo(newEmail);
      assertThat(c.getAge()).isEqualTo(customer.getAge());
    });
  }

  @Test
  void updateCustomerAge() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    int newAge = 24;

    // When
    Customer update = new Customer();
    update.setId(id);
    update.setAge(newAge);

    underTest.updateCustomer(update);

    // Then
    Optional<Customer> actual = underTest.selectCustomerById(id);

    assertThat(actual).isPresent().hasValueSatisfying(c -> {
      assertThat(c.getId()).isEqualTo(id);
      assertThat(c.getName()).isEqualTo(customer.getName());
      assertThat(c.getEmail()).isEqualTo(customer.getEmail());
      assertThat(c.getAge()).isEqualTo(newAge);
    });
  }

  @Test
  void willUpdateAllPropertiesCustomer() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    // When
    Customer update = new Customer();
    update.setId(id);
    update.setAge(24);
    update.setEmail(FAKER.internet().emailAddress() + "_" + UUID.randomUUID());
    update.setName("foo");
    update.setGender(Gender.MALE);

    underTest.updateCustomer(update);

    // Then
    Optional<Customer> actual = underTest.selectCustomerById(id);

    assertThat(actual).isPresent().hasValueSatisfying(c -> {
      assertThat(c.getId()).isEqualTo(id);
      assertThat(c.getName()).isEqualTo(update.getName());
      assertThat(c.getEmail()).isEqualTo(update.getEmail());
      assertThat(c.getAge()).isEqualTo(update.getAge());
      assertThat(c.getGender()).isEqualTo(update.getGender());
    });
  }

  @Test
  void willNotUpdateWithNothingToUpdate() {
    // Given
    String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
    Customer customer = new Customer(
        FAKER.name().fullName(),
        email,
        "password", 20,
        Gender.MALE
    );
    underTest.insertCustomer(customer);

    Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(Customer::getId)
        .findFirst()
        .orElseThrow();

    // When
    Customer update = new Customer();
    update.setId(id);

    underTest.updateCustomer(update);

    // Then
    Optional<Customer> actual = underTest.selectCustomerById(id);

    assertThat(actual).isPresent().hasValueSatisfying(c -> {
      assertThat(c.getId()).isEqualTo(id);
      assertThat(c.getName()).isEqualTo(customer.getName());
      assertThat(c.getEmail()).isEqualTo(customer.getEmail());
      assertThat(c.getAge()).isEqualTo(customer.getAge());
    });
  }
}