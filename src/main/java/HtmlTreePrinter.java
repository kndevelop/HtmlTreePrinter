import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import HtmlTreePrinter.Config;


@Slf4j
public class HtmlTreePrinter {

    public static void main(String[] args) throws Exception {

        String siteName = args[0];

        Config loginConfig = new ObjectMapper().readValue(
            HtmlTreePrinter.class.getResourceAsStream("/config/json/PINT_LOGIN.json"),
            Config.class
        );

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(true)
            );

            BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                    .setUserAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36"
                    )
            );
            Page page = context.newPage();

            OperationService operationService = new OperationService();
            operationService.scraping(page, siteName ,loginConfig);
            browser.close();
        }
    }

}