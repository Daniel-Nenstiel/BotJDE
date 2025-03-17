package run.scatter.botjde.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.scatter.botjde.entity.pocket.PocketCard;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PocketCardMapper {

  /**
   * Maps a single row of data to a PocketCard object.
   *
   * @param row       The row of data from the sheet.
   * @param columnMap A map of column names to their indices.
   * @return A PocketCard object representing the row data.
   */
  public PocketCard mapRowToCard(List<Object> row, Map<String, Integer> columnMap) {
    log.debug("Mapping row to PocketCard: {}", row);

    try {
      boolean owned = getBooleanValue(row, columnMap.getOrDefault("Owned", -1));
      String cardId = getStringValue(row, columnMap.getOrDefault("Card ID", -1));
      String cardName = getStringValue(row, columnMap.getOrDefault("Card", -1));
      String pack = getStringValue(row, columnMap.getOrDefault("Pack", -1));
      String rarity = getStringValue(row, columnMap.getOrDefault("Rarity", -1));
      int count = getIntegerValue(row, columnMap.getOrDefault("#", -1));

      PocketCard card = new PocketCard(owned, cardId, cardName, pack, rarity, count);
      log.debug("Mapped row to PocketCard: {}", card);
      return card;

    } catch (Exception e) {
      log.error("Failed to map row to PocketCard. Row: {}, ColumnMap: {}", row, columnMap, e);
      throw e; // Re-throw the exception to ensure it's handled upstream
    }
  }

  private boolean getBooleanValue(List<Object> row, int index) {
    if (index < 0 || index >= row.size()) {
      log.warn("Invalid index for boolean value: {}", index);
      return false;
    }

    try {
      Object value = row.get(index);
      if (value == null || value.toString().trim().isEmpty()) {
        log.warn("Empty or null value in 'Owned' column at index {}. Marking as unowned.", index);
        return false;
      }
      return Boolean.parseBoolean(value.toString());
    } catch (Exception e) {
      log.error("Error parsing boolean value at index {}: {}", index, row.get(index), e);
      return false;
    }
  }

  private String getStringValue(List<Object> row, int index) {
    if (index < 0 || index >= row.size()) {
      log.warn("Invalid index for string value: {}", index);
      return null;
    }

    try {
      return row.get(index).toString().trim();
    } catch (Exception e) {
      log.error("Error parsing string value at index {}: {}", index, row.get(index), e);
      return null;
    }
  }

  private int getIntegerValue(List<Object> row, int index) {
    if (index < 0 || index >= row.size()) {
      log.warn("Invalid index for integer value: {}", index);
      return 0;
    }

    try {
      return Integer.parseInt(row.get(index).toString().trim());
    } catch (NumberFormatException e) {
      log.warn("Error parsing integer value at index {}: {}. Defaulting to 0.", index, row.get(index));
      return 0;
    }
  }
}
