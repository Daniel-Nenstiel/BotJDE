package run.scatter.botjde.scheduled.birthday.mapper;

import org.apache.ibatis.annotations.Mapper;
import run.scatter.botjde.scheduled.entity.Birthday;

import java.time.LocalDate;
import java.util.ArrayList;

@Mapper
public interface BirthdayMapper {
  ArrayList<Birthday> getBirthdays(LocalDate date);
}
