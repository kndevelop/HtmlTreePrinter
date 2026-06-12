import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import HtmlTreePrinter.Config;
import java.io.InputStream;

@Slf4j
public class HtmlTreePrinter {

    public static void main(String[] args) throws Exception {

        String siteName = args[0];

        InputStream loginConfigStream = HtmlTreePrinter.class
                .getResourceAsStream(
                    "/config/json/" + siteName + "_LOGIN.json"
                );
        InputStream operateConfigStream = HtmlTreePrinter.class
                .getResourceAsStream(
                    "/config/json/" + siteName + "_OPERATE.json"
                );

        Config loginConfig = (loginConfigStream == null)
            ? null
            : new ObjectMapper().readValue(
                loginConfigStream,
                Config.class
            );

        /*Config operateConfig = new ObjectMapper().readValue(
                operateConfigStream,
                Config.class
            );*/

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

            if(loginConfig != null) {
                operationService.scraping(page, siteName, loginConfig);
            }
            operationService.scraping(page, siteName, operateConfig);

            browser.close();
        }
    }

}
