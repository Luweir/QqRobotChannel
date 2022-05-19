import java.text.MessageFormat;

public class TestPython {
    public static void main(String[] args) {
        String s = "123";
        System.out.printf("123%s \n", s);
        System.out.println(MessageFormat.format("123{0}", "123"));
    }
}
