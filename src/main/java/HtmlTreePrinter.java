import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import com.microsoft.playwright.*;
import java.util.List;

public class HtmlTreePrinter {

    // 設定
    static final int MAX_DEPTH = 6;

    public static void main(String[] args) throws Exception {
        String url = args[0];

        // Playwrightを使用してページを取得
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate(url);
            String html = page.content();
            browser.close();

            Document doc = Jsoup.parse(html);

            // ルートから開始
            printNode(doc, "", true, 0);
        }
    }

    static void printNode(Node node, String prefix, boolean isLast, int depth) {
        if (depth > MAX_DEPTH) return;

        // 不要ノード除外
        if (node instanceof Element el) {
            String tag = el.tagName();
            if (tag.equals("script") || tag.equals("style") || tag.equals("meta")) {
                return;
            }
        }

        // テキストノード処理
        if (node.nodeName().equals("#text")) {
            String text = node.toString().trim();
            if (text.isEmpty()) return;
            if (text.length() > 30) text = text.substring(0, 30) + "...";

            String connector = isLast ? "└─ " : "├─ ";
            System.out.println(prefix + connector + "#text: " + text);
            return;
        }

        // 表示
        String connector = isLast ? "└─ " : "├─ ";
        System.out.println(prefix + connector + formatNode(node));

        // 子ノード処理
        List<Node> children = node.childNodes();
        for (int i = 0; i < children.size(); i++) {
            boolean last = (i == children.size() - 1);
            String newPrefix = prefix + (isLast ? "   " : "│  ");
            printNode(children.get(i), newPrefix, last, depth + 1);
        }
    }

    static String formatNode(Node node) {
        if (node instanceof Element el) {
            String tag = el.tagName();

            String id = el.id().isEmpty() ? "" : "#" + el.id();

            String cls = el.className().isEmpty()
                    ? ""
                    : "." + el.className().replace(" ", ".");

            return tag + id + cls;
        }

        return node.nodeName();
    }
}