package run.scatter.botjde.utils;

public enum TimeUnit {
  SECONDS("seconds"),
  MINUTES("minutes"),
  HOURS("hours");

  private final String unit;

  TimeUnit(String unit) {
    this.unit = unit;
  }

  public String getUnit() {
    return unit;
  }

  @Override
  public String toString() {
    return unit;
  }
}



