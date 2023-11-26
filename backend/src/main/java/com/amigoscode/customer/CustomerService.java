package com.amigoscode.customer;

import com.amigoscode.Exception.DuplicateResourceException;
import com.amigoscode.Exception.RequestValidationException;
import com.amigoscode.Exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private final CustomerDao customerDao;
  private final CustomerDTOMapper customerDTOMapper;
  private final PasswordEncoder passwordEncoder;

  public CustomerService(@Qualifier("jdbc") CustomerDao customerDao,
      CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
    this.customerDao = customerDao;
    this.customerDTOMapper = customerDTOMapper;
    this.passwordEncoder = passwordEncoder;
  }

  public List<CustomerDTO> getAllCustomers() {
    return customerDao.selectAllCustomers()
        .stream()
        .map(customerDTOMapper)
        .collect(Collectors.toList());
  }

  public CustomerDTO getCustomer(Integer id) {
    return customerDao.selectCustomerById(id)
        .map(customerDTOMapper)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Customer with id [%s] not found".formatted(id)
        ));
  }

  public void addCustomer(
      CustomerRegistrationRequest customerRegistrationRequest) {
    // Check if email exist
    String email = customerRegistrationRequest.email();
    if (customerDao.existsPersonWithEmail(email)) {
      throw new DuplicateResourceException(
          "Email already taken"
      );
    }

    // Add
    Customer customer = new Customer(
        customerRegistrationRequest.name(),
        customerRegistrationRequest.email(),
        passwordEncoder.encode(customerRegistrationRequest.password()),
        customerRegistrationRequest.age(),
        customerRegistrationRequest.gender()
    );
    customerDao.insertCustomer(customer);
  }

  public void deleteCustomer(Integer customerId) {
    if (!customerDao.existsPersonWithId(customerId)) {
      throw new ResourceNotFoundException(
          "Customer with id [%s] not found".formatted(customerId)
      );
    }
    customerDao.deleteCustomerById(customerId);
  }

  public void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {
    // TODO: for JPA use .getReferenceById(customerId) as it does does not bring object into memory and instead a reference
    Customer customer = customerDao.selectCustomerById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Customer with id [%s] not found".formatted(customerId)
        ));

    boolean changes = false;

    if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
      customer.setName(updateRequest.name());
      changes = true;
    }

    if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
      customer.setAge(updateRequest.age());
      changes = true;
    }

    if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
      if (customerDao.existsPersonWithEmail(updateRequest.email())) {
        throw new DuplicateResourceException(
            "email already taken"
        );
      }
      customer.setEmail(updateRequest.email());
      changes = true;
    }

    if (updateRequest.gender() != null && !updateRequest.gender().equals(customer.getGender())) {
      customer.setGender(updateRequest.gender());
      changes = true;
    }

    if (!changes) {
      throw new RequestValidationException("no data changes found");
    }

    customerDao.updateCustomer(customer);
  }
}
