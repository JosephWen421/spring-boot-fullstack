package com.amigoscode.customer;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

  private final JdbcTemplate jdbcTemplate;
  private final CustomerRowMapper customerRowMapper;

  public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate,
      CustomerRowMapper customerRowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.customerRowMapper = customerRowMapper;
  }

  @Override
  public List<Customer> selectAllCustomers() {
    var sql = """
        SELECT id, name, email, age, gender
        FROM customer
        """;
    List<Customer> query = jdbcTemplate.query(sql, customerRowMapper);
    return query;
  }

  @Override
  public Optional<Customer> selectCustomerById(Integer id) {
    var sql = """
        SELECT id, name, email, age, gender
        FROM customer
        WHERE id = ?
        """;
    return jdbcTemplate.query(sql, customerRowMapper, id)
        .stream()
        .findFirst();
  }

  @Override
  public void insertCustomer(Customer customer) {
    var sql = """
        INSERT INTO customer (name, email, age, gender)
        VALUES (?, ?, ?, ?)
        """;
    int result = jdbcTemplate.update(
        sql,
        customer.getName(),
        customer.getEmail(),
        customer.getAge(),
        customer.getGender().name()
    );
    System.out.println("jdbcTemplate.update = " + result);
  }

  @Override
  public boolean existsPersonWithEmail(String email) {
    var sql = """
        SELECT count(id)
        FROM customer
        WHERE email = ?
        """;
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
    return count != null && count > 0;
  }

  @Override
  public boolean existsPersonWithId(Integer id) {
    var sql = """
        SELECT count(id)
        FROM customer
        WHERE id = ?
        """;
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != null && count > 0;
  }

  @Override
  public void deleteCustomerById(Integer id) {
    var sql = """
        DELETE FROM customer
        WHERE id = ?
        """;
    int result = jdbcTemplate.update(sql, id);
    System.out.println("deleteCustomerById result = " + result);
  }

  @Override
  public void updateCustomer(Customer update) {
    if (update.getName() != null) {
      var sql = """
          UPDATE customer
          SET name = ?
          WHERE id = ?
          """;
      int result = jdbcTemplate.update(
          sql,
          update.getName(),
          update.getId()
      );
      System.out.println("update customer name result = " + result);
    }

    if (update.getAge() != null) {
      var sql = """
          UPDATE customer
          SET age = ?
          WHERE id = ?
          """;
      int result = jdbcTemplate.update(
          sql,
          update.getAge(),
          update.getId()
      );
      System.out.println("update customer age result = " + result);
    }

    if (update.getEmail() != null) {
      var sql = """
          UPDATE customer
          SET email = ?
          WHERE id = ?
          """;
      int result = jdbcTemplate.update(
          sql,
          update.getEmail(),
          update.getId()
      );
      System.out.println("update customer email result = " + result);
    }

    if (update.getGender() != null) {
      var sql = """
          UPDATE customer
          SET gender = ?
          WHERE id = ?
          """;
      int result = jdbcTemplate.update(
          sql,
          update.getGender().toString(),
          update.getId()
      );
      System.out.println("update customer gender result = " + result);
    }
  }
}
