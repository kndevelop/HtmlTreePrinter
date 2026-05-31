import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import com.microsoft.playwright.*;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HtmlTreePrinter {

    static final int MAX_DEPTH = 6;

    @Data
    public static class Config {
        public String loginPageUrl;
        public String userNameField;
        public String passwordField;
        public String userName;
        public String password;
    }

    public static void main(String[] args) throws Exception {

        String serviceSetting = System.getenv("CONFIG_JSON");
        System.out.println("CONFIG_JSON: " + serviceSetting);
        Config config = new ObjectMapper().readValue(serviceSetting, Config.class);

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(true)
            );

            Page page = browser.newPage();

            login(page, config);

/*
            page.navigate(config.targetUrl);

            // 初期ロード待機
            page.waitForLoadState();

            // -----------------------------
            // 「選考・企業概要」タブをクリック
            // -----------------------------
            page.locator("button:has-text('選考・企業概要')").click();

            // HTML取得
            String html = page.content();

            */

            browser.close();

            //Document doc = Jsoup.parse(html);
            //printNode(doc, "", true, 0);

        }
    }

    static void login(Page page, Config config) throws Exception {

        log.info("ログインページURL: {}", config.getLoginPageUrl());

        page.navigate(config.getLoginPageUrl());

        String userSelector =
            "input[name='" + config.getUserNameField() + "'], " +
            "input[id='" + config.getUserNameField() + "']";

        String passwordSelector =
            "input[name='" + config.getPasswordField() + "'], " +
            "input[id='" + config.getPasswordField() + "']";

        // ログイン情報を入力
        page.fill(userSelector, config.getUserName());
        page.fill(passwordSelector, config.getPassword());

        // ログインボタンをクリック
        page.click("button[type='submit']");

        // ログイン完了待機
        page.waitForNavigation(() -> {});

        System.out.println("ログイン後URL: " + page.url());

    }

    static void printNode(Node node, String prefix, boolean isLast, int depth) {

        if (depth > MAX_DEPTH) return;

        if (node instanceof Element el) {
            String tag = el.tagName();

            if (tag.equals("script")
                    || tag.equals("style")
                    || tag.equals("meta")) {
                return;
            }
        }

        if (node.nodeName().equals("#text")) {

            String text = node.toString().trim();

            if (text.isEmpty()) return;

            if (text.length() > 30) {
                text = text.substring(0, 30) + "...";
            }

            String connector = isLast ? "└─ " : "├─ ";

            System.out.println(prefix + connector + "#text: " + text);

            return;
        }

        String connector = isLast ? "└─ " : "├─ ";

        System.out.println(prefix + connector + formatNode(node));

        List<Node> children = node.childNodes();

        for (int i = 0; i < children.size(); i++) {

            boolean last = (i == children.size() - 1);

            String newPrefix =
                    prefix + (isLast ? "   " : "│  ");

            printNode(children.get(i), newPrefix, last, depth + 1);
        }
    }

    static String formatNode(Node node) {

        if (node instanceof Element el) {

            String tag = el.tagName();

            String id =
                    el.id().isEmpty()
                            ? ""
                            : "#" + el.id();

            String cls =
                    el.className().isEmpty()
                            ? ""
                            : "." + el.className().replace(" ", ".");

            return tag + id + cls;
        }

        return node.nodeName();
    }
}