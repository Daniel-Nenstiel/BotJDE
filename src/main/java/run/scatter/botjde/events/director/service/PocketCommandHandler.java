package run.scatter.botjde.events.director.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.pocket.PocketCard;
import run.scatter.botjde.entity.pocket.PocketSheet;
import run.scatter.botjde.entity.pocket.PocketSheetConfig;
import run.scatter.botjde.integrations.google.sheet.PocketSheetService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PocketCommandHandler {

  private final AppConfig appConfig;
  private final PocketSheetService pocketSheetService;

  public String handleCommand(String content, String author) {
    try {
      // Split the content into command parts
      String[] parts = content.split(" ");
      if (parts.length < 3) {
        return "Invalid command format. Use `!pocket <action> <sheetName>`.";
      }

      String action = parts[1]; // e.g., "unowned"
      String sheetName = parts[2]; // e.g., "DJ"

      switch (action.toLowerCase()) {
        case "unowned": {
          return handleUnownedCommand(sheetName);
        }

        // Add more cases for additional commands (e.g., owned, stats, etc.)
        default:
          return "Unknown action: " + action;
      }

    } catch (Exception ex) {
      log.error("Error handling command", ex);
      return String.format("Make DJ fix it: %s", ex.getMessage());
    }
  }

  private String handleUnownedCommand(String sheetName) {
    // Find the specified sheet configuration by name
    PocketSheetConfig.SheetUrl targetSheet = appConfig.getPocketSheets().stream()
        .flatMap(config -> config.getUrls().stream())
        .filter(sheetUrl -> sheetUrl.getName().equalsIgnoreCase(sheetName))
        .findFirst()
        .orElse(null);

    if (targetSheet == null) {
      return String.format("Sheet with name '%s' not found.", sheetName);
    }

    try {
      // Check the specified sheet for unowned cards
      List<PocketCard> unownedCards = checkSheetForOwnership(targetSheet.getUrl());

      if (unownedCards.isEmpty()) {
        return String.format("Congratulations! All cards in '%s' are owned.", sheetName);
      }

      // Format the list of unowned cards
      String unownedCardList = unownedCards.stream()
          .map(card -> String.format("%s (%s) from %s", card.getCardName(), card.getCardId(), card.getPack()))
          .collect(Collectors.joining("\n"));

      return String.format("Unowned cards in '%s':\n%s", sheetName, unownedCardList);

    } catch (Exception e) {
      log.error("Error processing sheet: {}", sheetName, e);
      return String.format("An error occurred while processing sheet '%s'.", sheetName);
    }
  }

  /**
   * Checks a single sheet for owned and unowned cards.
   *
   * @param sheetUrl The URL of the Google Sheet to check.
   * @return A list of unowned PocketCards.
   * @throws Exception if an error occurs while processing the sheet.
   */
  private List<PocketCard> checkSheetForOwnership(String sheetUrl) throws Exception {
    // Parse the sheet
    PocketSheet pocketSheet = pocketSheetService.parsePocketSheet(sheetUrl);

    if (pocketSheet == null || pocketSheet.getCards().isEmpty()) {
      log.warn("No cards found in sheet: {}", sheetUrl);
      return List.of();
    }
    log.info(pocketSheet.toString());
    // Filter and return unowned cards
    return pocketSheet.getCards().stream()
        .filter(card -> !card.isOwned())
        .collect(Collectors.toList());
  }
}
