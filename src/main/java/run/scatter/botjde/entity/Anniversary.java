package run.scatter.botjde.entity;

import lombok.Data;

import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Anniversary {
  List<User> users;
  LocalDate Date;

  public String getFormattedNames() {
    if(users==null|| users.isEmpty()) return "";
    else if(users.size()==1) { return users.get(0).getName(); }
    else if(users.size()==2){
      return users.stream()
          .map(User::getName)
          .collect(Collectors.joining(" and "));
    }
    else{
      String commaString = users.stream()
          .map(User::getName)
          .collect(Collectors.joining(", "));

      int finalComma = commaString.lastIndexOf(",");

      //Remove last comma and replace with ' and' turning a, b, c into a, b, and c
      return commaString.substring(0, finalComma)
          + " and"
          + commaString.substring(finalComma+1);
    }
  }
}
