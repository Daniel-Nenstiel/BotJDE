package run.scatter.botjde.entity.pocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PocketSheet {
  private String sheetName; // Name of the sheet (tab)
  private List<PocketCard> cards; // List of cards in the sheet
}
