package run.scatter.botjde.integrations.google.sheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.scatter.botjde.integrations.google.auth.GoogleAuthService;

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
public class GoogleSheetService {

  private final GoogleAuthService authService;
  public static final String DEFAULT_RANGE = "A1:Z";

  /**
   * Retrieves the names of all sheets in the specified spreadsheet.
   *
   * @param spreadsheetId The ID of the spreadsheet.
   * @return A list of sheet names.
   * @throws GeneralSecurityException If authentication fails.
   * @throws IOException              If there's an error accessing the spreadsheet.
   */
  public List<String> getSheetNames(String spreadsheetId) throws GeneralSecurityException, IOException {
    log.info("Fetching sheet names for spreadsheet ID: {}", spreadsheetId);

    Sheets sheetsClient = authService.getSheetsClient();
    List<String> sheetNames = sheetsClient.spreadsheets()
        .get(spreadsheetId)
        .execute()
        .getSheets()
        .stream()
        .map(sheet -> sheet.getProperties().getTitle())
        .toList();

    log.info("Retrieved {} sheet(s) from spreadsheet ID: {}", sheetNames.size(), spreadsheetId);
    return sheetNames;
  }

  /**
   * Retrieves data from a specific range in a spreadsheet.
   *
   * @param spreadsheetId The ID of the spreadsheet.
   * @param range         The range to retrieve (e.g., "Sheet1!A1:Z").
   * @return A list of rows, where each row is a list of cell values.
   * @throws GeneralSecurityException If authentication fails.
   * @throws IOException              If there's an error accessing the spreadsheet.
   */
  public List<List<Object>> getSheetData(String spreadsheetId, String range) throws GeneralSecurityException, IOException {
    log.info("Fetching data from range '{}' in spreadsheet ID: {}", range, spreadsheetId);

    Sheets sheetsClient = authService.getSheetsClient();
    ValueRange response = sheetsClient.spreadsheets().values()
        .get(spreadsheetId, range)
        .execute();

    if (response == null || response.getValues() == null) {
      log.warn("No data found in range '{}' for spreadsheet ID: {}", range, spreadsheetId);
      return List.of();
    }

    log.info("Retrieved {} rows from range '{}' in spreadsheet ID: {}", response.getValues().size(), range, spreadsheetId);
    return response.getValues();
  }

  /**
   * Retrieves metadata about a spreadsheet.
   *
   * @param spreadsheetId The ID of the spreadsheet.
   * @return The spreadsheet metadata.
   * @throws GeneralSecurityException If authentication fails.
   * @throws IOException              If there's an error accessing the spreadsheet.
   */
  public Spreadsheet getSpreadsheetMetadata(String spreadsheetId) throws GeneralSecurityException, IOException {
    log.info("Fetching metadata for spreadsheet ID: {}", spreadsheetId);

    Sheets sheetsClient = authService.getSheetsClient();
    Spreadsheet metadata = sheetsClient.spreadsheets().get(spreadsheetId).execute();

    log.info("Retrieved metadata for spreadsheet ID: {}", spreadsheetId);
    return metadata;
  }

  /**
   * Extracts the spreadsheet ID from a Google Sheets URL.
   *
   * @param sheetUrl The URL of the Google Sheet.
   * @return The extracted spreadsheet ID.
   * @throws IllegalArgumentException If the URL is invalid.
   */
  public String extractSpreadsheetId(String sheetUrl) {
    log.info("Extracting spreadsheet ID from URL: {}", sheetUrl);

    String[] parts = sheetUrl.split("/");
    for (int i = 0; i < parts.length; i++) {
      if (parts[i].equals("d") && i + 1 < parts.length) {
        String spreadsheetId = parts[i + 1];
        log.info("Extracted spreadsheet ID: {}", spreadsheetId);
        return spreadsheetId;
      }
    }

    log.error("Failed to extract spreadsheet ID from URL: {}", sheetUrl);
    throw new IllegalArgumentException("Invalid Google Sheet URL: " + sheetUrl);
  }

  /**
   * Generates a column-to-index map from the header row.
   *
   * @param headerRow The header row of the sheet.
   * @return A map of column names to their indices.
   */
  public Map<String, Integer> getColumnIndices(List<Object> headerRow) {
    log.info("Generating column-to-index map from header row: {}", headerRow);

    try {
      Map<String, Integer> columnMap = IntStream.range(0, headerRow.size())
          .boxed()
          .collect(Collectors.toMap(
              index -> headerRow.get(index).toString().trim(),
              Function.identity(),
              (existing, replacement) -> {
                log.warn("Duplicate column detected: '{}'. Keeping first occurrence.", existing);
                return existing;
              }
          ));

      log.info("Generated column-to-index map: {}", columnMap);
      return columnMap;
    } catch (Exception e) {
      log.error("Error generating column-to-index map from header row: {}", headerRow, e);
      throw e;
    }
  }
}
