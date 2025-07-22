package run.scatter.botjde.scheduled.birthday.dao;

import run.scatter.botjde.entity.Birthday;

import java.time.LocalDate;
import java.util.List;

public interface BirthdayDao {
  List<Birthday> getBirthdays(LocalDate date);
  List<Birthday> getTodaysBirthdays();
  List<Birthday> getTodaysBirthdaysForServer(String serverId);
}
