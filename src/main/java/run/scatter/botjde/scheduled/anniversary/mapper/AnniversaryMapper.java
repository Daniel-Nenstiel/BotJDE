package run.scatter.botjde.scheduled.anniversary.mapper;

import org.apache.ibatis.annotations.Mapper;
import run.scatter.botjde.entity.Anniversary;

import java.util.List;

@Mapper
public interface AnniversaryMapper {
  List<Anniversary> getTodaysAnniversaries();
  List<Anniversary> getTodaysAnniversariesForServer(String serverId);
}
