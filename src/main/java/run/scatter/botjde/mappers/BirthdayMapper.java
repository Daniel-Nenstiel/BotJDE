package run.scatter.botjde.mappers;

import org.apache.ibatis.annotations.Mapper;
import run.scatter.botjde.entity.Birthday;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BirthdayMapper {
  List<Birthday> getBirthdaysByDate(LocalDate date);

  List<Birthday> getTodaysBirthdays();

  List<Birthday> getTodaysBirthdaysForServer(String serverId);
}
