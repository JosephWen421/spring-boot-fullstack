package com.amigoscode;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestcontainersTest extends AbstractTestContainers {

  @Test
  void canStartPostgresDB() {
    assertThat(postgreSQLContainer.isRunning()).isTrue();
    assertThat(postgreSQLContainer.isCreated()).isTrue();
  }
}
