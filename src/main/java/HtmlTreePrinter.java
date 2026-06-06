import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Paths;
import HtmlTreePrinter.Config;


@Slf4j
public class HtmlTreePrinter {

    public static void main(String[] args) throws Exception {

        String siteName = args[0];

        Config loginConfig = new ObjectMapper().readValue(
            Paths.get("src/main/resources/config/json/" + siteName + "_LOGIN.json").toFile(),
            Config.class
        );
        
        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(true)
            );
            Page page = browser.newPage();
            
            OperationService operationService = new OperationService();
            operationService.scraping(page, loginConfig);
            browser.close();

        }
    }

}