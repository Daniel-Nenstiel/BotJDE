package run.scatter.botjde.config;

import discord4j.common.util.Snowflake;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SnowflakeConverterTest {

  @Test
  void convert_shouldHandleString() {
    SnowflakeConverter converter = new SnowflakeConverter();
    Snowflake result = converter.convert("157625252170563584");
    assertThat(result).isNotNull();
    assertThat(result.asString()).isEqualTo("157625252170563584");
  }

  @Test
  void convert_shouldHandleLong() {
    SnowflakeConverter converter = new SnowflakeConverter();
    Snowflake result = converter.convert(157625252170563584L);
    assertThat(result).isNotNull();
    assertThat(result.asString()).isEqualTo("157625252170563584");
  }
}
