package com.amigoscode.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

  // db
  private static List<Customer> customers;

  static {
    customers = new ArrayList<>();
    Customer alex = new Customer(
        1,
        "Alex",
        "Alex@gmail.com",
        21
    );
    Customer jamila = new Customer(
        2,
        "Jamila",
        "Jamila@gmail.com",
        19
    );
    customers.add(alex);
    customers.add(jamila);
  }

  @Override
  public List<Customer> selectAllCustomers() {
    return customers;
  }

  @Override
  public Optional<Customer> selectCustomerById(Integer id) {
    return customers.stream()
        .filter(customer -> customer.getId().equals(id))
        .findFirst();
  }

  @Override
  public void insertCustomer(Customer customer) {
    customers.add(customer);
  }

  @Override
  public boolean existsPersonWithEmail(String email) {
    return customers.stream()
        .anyMatch(customer -> customer.getEmail().equals(email));
  }

  @Override
  public void deleteCustomerById(Integer id) {
    customers.stream()
        .filter(customer -> customer.getId().equals(id))
        .findFirst()
        .ifPresent(customers::remove);
  }

  @Override
  public boolean existsPersonWithId(Integer id) {
    return customers.stream()
        .anyMatch(customer -> customer.getId().equals(id));
  }

  @Override
  public void updateCustomer(Customer customer) {
    customers.add(customer);
  }
}
