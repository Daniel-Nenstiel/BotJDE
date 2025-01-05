package run.scatter.botjde.scheduled.birthday.dao.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;
import run.scatter.botjde.scheduled.birthday.mapper.BirthdayMapper;
import run.scatter.botjde.scheduled.entity.Birthday;

import java.time.LocalDate;
import java.util.ArrayList;

@Repository("myBatisBirthdayDao")
@RequiredArgsConstructor
public class MyBatisBirthdayDao implements BirthdayDao {
  @NonNull
  private final BirthdayMapper mapper;

  public ArrayList<Birthday> getBirthdays(LocalDate date){
    return new ArrayList<>();
  };
}
