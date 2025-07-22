package run.scatter.botjde.scheduled.anniversary.dao.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;
import run.scatter.botjde.scheduled.anniversary.mapper.AnniversaryMapper;
import run.scatter.botjde.entity.Anniversary;

import java.util.List;

@Repository("myBatisAnniversaryDao")
@RequiredArgsConstructor
public class MyBatisAnniversaryDao implements AnniversaryDao {
  @NonNull
  private final AnniversaryMapper mapper;

  public List<Anniversary> getTodaysAnniversaries() { return mapper.getTodaysAnniversaries(); }

  public List<Anniversary> getTodaysAnniversariesForServer(String serverId) {
    return mapper.getTodaysAnniversariesForServer(serverId);
  }
}
