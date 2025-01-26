package run.scatter.botjde.config;

import discord4j.common.util.Snowflake;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class SnowflakeConverter implements Converter<Object, Snowflake> {

  @Override
  public Snowflake convert(@Nullable Object source) {
    if (source == null) {
      throw new IllegalArgumentException("Cannot convert null to Snowflake");
    }

    if (source instanceof String stringSource) {
      return Snowflake.of(stringSource);
    } else if (source instanceof Long longSource) {
      return Snowflake.of(Long.toString(longSource));
    } else {
      throw new IllegalArgumentException("Invalid type for Snowflake conversion: " + source.getClass().getName());
    }
  }
}
