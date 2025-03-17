package run.scatter.botjde.integrations.google.sheet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.scatter.botjde.entity.pocket.PocketCard;
import run.scatter.botjde.entity.pocket.PocketSheet;
import run.scatter.botjde.mappers.PocketCardMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PocketSheetService {

  @Autowired
  private final GoogleSheetService googleSheetService;

  @Autowired
  private final PocketCardMapper pocketCardMapper;

  /**
   * Parses a Google Sheet into a PocketSheet object.
   *
   * @param sheetUrl The URL of the Google Sheet.
   * @return A PocketSheet object containing all the parsed PocketCards.
   * @throws GeneralSecurityException If authentication fails.
   * @throws IOException              If there is an error accessing or parsing the sheet.
   */
  public PocketSheet parsePocketSheet(String sheetUrl) throws GeneralSecurityException, IOException {
    log.info("Starting to parse PocketSheet from URL: {}", sheetUrl);

    // Extract spreadsheet ID
    String spreadsheetId = googleSheetService.extractSpreadsheetId(sheetUrl);
    log.debug("Extracted spreadsheet ID: {} from URL: {}", spreadsheetId, sheetUrl);

    // Fetch sheet data using the default range
    List<List<Object>> rows = googleSheetService.getSheetData(spreadsheetId, GoogleSheetService.DEFAULT_RANGE);
    if (rows == null || rows.isEmpty()) {
      log.warn("No rows found in the sheet. URL: {}", sheetUrl);
      throw new IOException("Sheet is empty or inaccessible: " + sheetUrl);
    }

    log.info("Retrieved {} rows from spreadsheet ID: {}", rows.size(), spreadsheetId);

    // Handle unnamed columns specific to PocketSheet
    Map<String, Integer> columnMap = processHeaderRow(rows.get(0));
    log.debug("Generated column-to-index map: {} for sheet: {}", columnMap, sheetUrl);

    // Map rows to PocketCard objects
    List<PocketCard> cards = rows.stream()
        .skip(1) // Skip the header row
        .map(row -> {
          try {
            return pocketCardMapper.mapRowToCard(row, columnMap);
          } catch (Exception e) {
            log.error("Error mapping row to PocketCard. Row: {}, Sheet URL: {}", row, sheetUrl, e);
            return null; // Skip invalid rows
          }
        })
        .filter(card -> card != null) // Exclude null cards
        .toList();

    log.info("Parsed {} PocketCard(s) from sheet: {}", cards.size(), sheetUrl);

    return new PocketSheet(sheetUrl, cards);
  }

  /**
   * Processes the header row to generate a column-to-index map.
   * Assigns default names for unnamed columns where needed.
   *
   * @param headerRow The header row of the sheet.
   * @return A map of column names to their indices.
   */
  private Map<String, Integer> processHeaderRow(List<Object> headerRow) {
    log.info("Processing header row for PocketSheet: {}", headerRow);

    return IntStream.range(0, headerRow.size())
        .boxed()
        .collect(Collectors.toMap(
            index -> {
              String header = headerRow.get(index) != null ? headerRow.get(index).toString().trim() : "";
              if (header.isEmpty()) {
                if (index == 0) {
                  header = "Owned"; // Assign default name for the first column
                  log.warn("Unnamed first column detected. Renamed to '{}'.", header);
                } else {
                  header = "Unnamed_" + (index + 1); // Assign generic name for other unnamed columns
                  log.warn("Unnamed column detected at index {}. Renamed to '{}'.", index, header);
                }
              }
              return header;
            },
            Function.identity(),
            (existing, replacement) -> existing // Prevent duplicate keys
        ));
  }
}
