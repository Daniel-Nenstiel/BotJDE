package run.scatter.botjde.integrations.google.auth;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  public Sheets getSheetsClient() throws GeneralSecurityException, IOException {
    // Load credentials from the file
    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/google/credentials.json"))
        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

    // Build and return the Sheets client
    return new Sheets.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JSON_FACTORY,
        new HttpCredentialsAdapter(credentials)
    ).setApplicationName("BotJDE").build();
  }
}
