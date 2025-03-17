package run.scatter.botjde.entity.pocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PocketPlayer {
  private String playerName; // Name of the player
  private List<PocketSheet> pocketSheets; // List of sheets owned by the player
}
