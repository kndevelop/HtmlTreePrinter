package HtmlTreePrinter;
import lombok.Data;

@Data
public class Step {
    private String env;
    private String filename;
    private String type;
    private String url;
    private String selector;
    private String value;
    private String name;
    private Long milliseconds;
}
