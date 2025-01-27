package run.scatter.botjde.persistence.anniversary.dao;

import run.scatter.botjde.entity.Anniversary;

import java.util.List;

public interface AnniversaryDao {
  List<Anniversary> getTodaysAnniversaries();
}
