import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import com.microsoft.playwright.*;
import java.util.List;

public class HtmlTreePrinter {

    static final int MAX_DEPTH = 6;

    public static class Config {
        public String firstPageUrl;
        public String loginRequired;
        public String userName;
        public String password;
    }

    public static void main(String[] args) throws Exception {

        String serviceName = args[0];
        String serviceSetting = System.getenv(serviceName + "_SETTING");
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

        System.out.println(config.loginPageUrl);

        page.navigate(config.loginPageUrl);

        String userSelector =
            "input[name='" + config.userNameField + "'], " +
            "input[id='" + config.userNameField + "']";

        String passwordSelector =
            "input[name='" + config.passwordField + "'], " +
            "input[id='" + config.passwordField + "']";

        // ログイン情報を入力
        page.fill(userSelector, config.userName);
        page.fill(passwordSelector, config.password);

        // ログインボタンをクリック
        page.click("button[type='submit']");

        // ログイン完了待機
        page.waitForNavigation();

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