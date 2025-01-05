package run.scatter.botjde.scheduled.birthday.dao;

import run.scatter.botjde.scheduled.entity.Birthday;

import java.time.LocalDate;
import java.util.ArrayList;

public interface BirthdayDao {
  ArrayList<Birthday> getBirthdays(LocalDate date);
}
