package com.amigoscode.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amigoscode.Exception.DuplicateResourceException;
import com.amigoscode.Exception.RequestValidationException;
import com.amigoscode.Exception.ResourceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerDao customerDao;
  @Mock
  private PasswordEncoder passwordEncoder;
  private CustomerService underTest;
  private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

  @BeforeEach
  void setUp() {
    underTest = new CustomerService(customerDao, customerDTOMapper, passwordEncoder);
  }

  @Test
  void getAllCustomers() {
    // When
    underTest.getAllCustomers();

    // Then
    verify(customerDao).selectAllCustomers();
  }

  @Test
  void getCustomer() {
    // Given
    int id = 1;
    Customer customer = new Customer(
        1, "Alex", "alex@gamil.com", "password", 19, Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerDTO expect = customerDTOMapper.apply(customer);

    // When
    CustomerDTO actual = underTest.getCustomer(id);

    // Then
    assertThat(actual).isEqualTo(expect);
  }

  @Test
  void willThrowWhenGetCustomerReturnsEmptyOptional() {
    // Given
    int id = 1;
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

    // When
    // Then
    assertThatThrownBy(() -> underTest.getCustomer(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Customer with id [%s] not found".formatted(id));
  }

  @Test
  void addCustomer() {
    // Given
    String email = "alex@gamil.com";
    when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
    String password = "password";
    String passwordHash = "fsfdsgf";
    when(passwordEncoder.encode(password)).thenReturn(passwordHash);

    // When
    CustomerRegistrationRequest request = new CustomerRegistrationRequest(
        "Alex",
        email,
        password, 19,
        Gender.MALE
    );
    underTest.addCustomer(request);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
        Customer.class
    );
    verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
    Customer capturedCustomer = customerArgumentCaptor.getValue();
    assertThat(capturedCustomer.getId()).isNull();
    assertThat(capturedCustomer.getName()).isEqualTo(request.name());
    assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
    assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
  }

  @Test
  void willThrowWhenEmailExistsWhileAddingCustomer() {
    // Given
    String email = "alex@gamil.com";
    when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
    CustomerRegistrationRequest request = new CustomerRegistrationRequest(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );

    // When
    assertThatThrownBy(() -> underTest.addCustomer(request))
        .isInstanceOf(DuplicateResourceException.class)
        .hasMessage("Email already taken");

    // Then
    verify(customerDao, never()).insertCustomer(any());
  }

  @Test
  void deleteCustomer() {
    // Given
    int id = 1;
    when(customerDao.existsPersonWithId(id)).thenReturn(true);

    // When
    underTest.deleteCustomer(id);

    // Then
    verify(customerDao).deleteCustomerById(id);
  }

  @Test
  void willThrowWhenDeleteCustomerWhileIdNotExists() {
    // Given
    int id = 1;
    when(customerDao.existsPersonWithId(id)).thenReturn(false);

    // When
    assertThatThrownBy(() -> underTest.deleteCustomer(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Customer with id [%s] not found".formatted(id));

    // Then
    verify(customerDao, never()).deleteCustomerById(id);
  }

  @Test
  void canUpdateAllCustomersProperties() {
    // Given
    int id = 1;
    String email = "alex@gamil.com";
    Customer customer = new Customer(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    String newEmail = "alexAndrew@gmail.com";
    CustomerUpdateRequest request = new CustomerUpdateRequest(
        "Alex andrew",
        newEmail,
        23,
        Gender.MALE
    );
    when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

    // When
    underTest.updateCustomer(id, request);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
        Customer.class
    );
    verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
    Customer captorCustomer = customerArgumentCaptor.getValue();

    assertThat(captorCustomer.getName()).isEqualTo(request.name());
    assertThat(captorCustomer.getEmail()).isEqualTo(request.email());
    assertThat(captorCustomer.getAge()).isEqualTo(request.age());
  }

  @Test
  void canUpdateOnlyCustomerName() {
    // Given
    int id = 1;
    String email = "alex@gamil.com";
    Customer customer = new Customer(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest request = new CustomerUpdateRequest(
        "Alex andrew",
        null,
        null,
        Gender.MALE
    );

    // When
    underTest.updateCustomer(id, request);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
        Customer.class
    );
    verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
    Customer captorCustomer = customerArgumentCaptor.getValue();

    assertThat(captorCustomer.getName()).isEqualTo(request.name());
    assertThat(captorCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(captorCustomer.getAge()).isEqualTo(customer.getAge());
  }

  @Test
  void canUpdateOnlyCustomerEmail() {
    // Given
    int id = 1;
    String email = "alex@gamil.com";
    Customer customer = new Customer(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    String newEmail = "alexAndrew@gmail.com";
    CustomerUpdateRequest request = new CustomerUpdateRequest(
        null,
        newEmail,
        null,
        Gender.MALE
    );
    when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

    // When
    underTest.updateCustomer(id, request);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
        Customer.class
    );
    verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
    Customer captorCustomer = customerArgumentCaptor.getValue();

    assertThat(captorCustomer.getName()).isEqualTo(customer.getName());
    assertThat(captorCustomer.getEmail()).isEqualTo(request.email());
    assertThat(captorCustomer.getAge()).isEqualTo(customer.getAge());
  }

  @Test
  void canUpdateOnlyCustomerAge() {
    // Given
    int id = 1;
    String email = "alex@gamil.com";
    Customer customer = new Customer(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest request = new CustomerUpdateRequest(
        null,
        null,
        23,
        Gender.MALE
    );

    // When
    underTest.updateCustomer(id, request);

    // Then
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
        Customer.class
    );
    verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
    Customer captorCustomer = customerArgumentCaptor.getValue();

    assertThat(captorCustomer.getName()).isEqualTo(customer.getName());
    assertThat(captorCustomer.getEmail()).isEqualTo(customer.getEmail());
    assertThat(captorCustomer.getAge()).isEqualTo(request.age());
  }

  @Test
  void willThrowWhenTryToUpdateCustomerWhenEmailAlreadyTaken() {
    // Given
    int id = 1;
    String email = "alex@gamil.com";
    Customer customer = new Customer(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    String newEmail = "alexAndrew@gmail.com";
    CustomerUpdateRequest request = new CustomerUpdateRequest(
        null,
        newEmail,
        null,
        Gender.MALE
    );
    when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

    // When
    assertThatThrownBy(() -> underTest.updateCustomer(id, request))
        .isInstanceOf(DuplicateResourceException.class)
        .hasMessage("email already taken");

    // Then
    verify(customerDao, never()).updateCustomer(any());
  }

  @Test
  void willThrowWhenCustomerUpdateHasNoChanges() {
    // Given
    int id = 1;
    String email = "alex@gamil.com";
    Customer customer = new Customer(
        "Alex",
        email,
        "password", 19,
        Gender.MALE
    );
    when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

    CustomerUpdateRequest request = new CustomerUpdateRequest(
        customer.getName(),
        customer.getEmail(),
        customer.getAge(),
        Gender.MALE
    );

    // When
    assertThatThrownBy(() -> underTest.updateCustomer(id, request))
        .isInstanceOf(RequestValidationException.class)
        .hasMessage("no data changes found");

    // Then
    verify(customerDao, never()).updateCustomer(any());
  }
}