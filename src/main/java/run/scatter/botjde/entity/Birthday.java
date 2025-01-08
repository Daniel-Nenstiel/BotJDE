package run.scatter.botjde.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Birthday {
  private User user;
  private LocalDate date;
}
