package run.scatter.botjde.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
    basePackages = "run.scatter.botjde",
    annotationClass = Mapper.class
)
public class MyBatisConfig {

}
