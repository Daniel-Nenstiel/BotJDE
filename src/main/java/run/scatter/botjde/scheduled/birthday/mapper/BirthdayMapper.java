package run.scatter.botjde.scheduled.birthday.mapper;

import org.apache.ibatis.annotations.Mapper;
import run.scatter.botjde.entity.Birthday;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BirthdayMapper {
  List<Birthday> getBirthdaysByDate(LocalDate date);

  List<Birthday> getTodaysBirthdays();
}
