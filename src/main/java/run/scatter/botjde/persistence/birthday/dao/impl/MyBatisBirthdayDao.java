package run.scatter.botjde.persistence.birthday.dao.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import run.scatter.botjde.persistence.birthday.dao.BirthdayDao;
import run.scatter.botjde.mappers.BirthdayMapper;
import run.scatter.botjde.entity.Birthday;

import java.time.LocalDate;
import java.util.List;

@Repository("myBatisBirthdayDao")
@RequiredArgsConstructor
public class MyBatisBirthdayDao implements BirthdayDao {
  @NonNull
  private final BirthdayMapper mapper;

  public List<Birthday> getBirthdays(LocalDate date) {
    return mapper.getBirthdaysByDate(date);
  }

  public List<Birthday> getTodaysBirthdays() {
    return mapper.getTodaysBirthdays();
  }

  public List<Birthday> getTodaysBirthdaysForServer(String serverId) {
    return mapper.getTodaysBirthdaysForServer(serverId);
  }
}
