package run.scatter.botjde.entity.pocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PocketCard {
  private boolean owned;
  private String cardId;
  private String cardName;
  private String pack;
  private String rarity;
  private int count;
}
