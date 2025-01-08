package run.scatter.botjde.scheduled.anniversary.dao;

import run.scatter.botjde.entity.Anniversary;

import java.util.List;

public interface AnniversaryDao {
  List<Anniversary> getTodaysAnniversaries();
}
