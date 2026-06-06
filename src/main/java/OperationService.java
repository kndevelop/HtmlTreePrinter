import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Paths;
import HtmlTreePrinter.Config;
import HtmlTreePrinter.Step;

@Slf4j
public class OperationService {

    static void scraping(Page page, String siteName, Config config) throws Exception {
        for (Step step : config.getSteps()) {
            switch (step.getType()) {
                case "navigate":
                    navigate(page, step.getUrl());
                    break;

                case "input":
                    input(page, siteName,
                        step.getEnv(), 
                        step.getValue(), 
                        step.getSelector());
                    break;

                case "click":
                    click(page, step.getSelector());
                    break;

                case "extract":
                    extract(page, step.getName(), step.getSelector());
                    break;

                case "wait":
                    waitFor(page, step.getMilliseconds());
                    break;

                case "screenshot":
                    screenshot(page, step.getFilename());
                    break;
            }
        }
    }

    static void navigate(Page page, String url) {
        page.navigate(url);
    }

    static void input(
        Page page,
        String siteName,
        String env,
        String value,
        String selector
    ) {
        page.locator(selector)
            .fill(env != null
                  ? System.getenv(siteName + "_" + env)
                  : value);
    }

    static void click(Page page, String selector) {
        page.locator(selector)
            .click();
    }

    static void extract(Page page, String name, String selector) {
        String text = page.locator(selector)
            .textContent();
        log.info("Extracted text: {}", text);
        System.out.println(name + "=" + text);
    }

    static void waitFor(Page page, Long milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    static void screenshot(Page page, String filename) {
        page.screenshot(
            new Page.ScreenshotOptions()
                .setPath(Paths.get(filename))
        );
    }
}