package run.scatter.botjde.mappers;

import org.junit.jupiter.api.Test;
import run.scatter.botjde.entity.pocket.PocketCard;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PocketCardMapperTest {

  private final PocketCardMapper pocketCardMapper = new PocketCardMapper();

  @Test
  void testMapRowToCard() {
    // Define the column mapping
    Map<String, Integer> columnMap = Map.of(
        "Owned", 0,
        "Card ID", 1,
        "Card", 2,
        "Pack", 3,
        "Rarity", 4,
        "#", 7
    );

    // Define the row data
    List<Object> row = List.of("TRUE", "A1a 001", "Exeggcute", "Mew", "♢", "", "", "3");

    // Map the row to a PocketCard
    PocketCard card = pocketCardMapper.mapRowToCard(row, columnMap);

    // Assertions
    assertNotNull(card, "The card should not be null");
    assertTrue(card.isOwned(), "The card should be marked as owned");
    assertEquals("A1a 001", card.getCardId(), "Card ID should match");
    assertEquals("Exeggcute", card.getCardName(), "Card name should match");
    assertEquals("Mew", card.getPack(), "Pack should match");
    assertEquals("♢", card.getRarity(), "Rarity should match");
    assertEquals(3, card.getCount(), "Count should match");
  }
}
