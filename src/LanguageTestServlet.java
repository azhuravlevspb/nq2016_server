import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.*;

/**
 * Created by Anton on 12.01.2016.
 */
public class LanguageTestServlet extends HttpServlet {
    private final String TOKEN = "************";
    private final String ANSWER_MESSAGE = "{\n  \"QuestAnswer\": \"********\"\n}";
    private final String WRONG_ANSWER = "{\n  \"error\": \"Wrong answer\"\n}";
    private final String TIME_IS_OVER = "{\n  \"error\": \"Time is over\"\n}";


    private final WordList list = new WordList();
    static Connection connection;

    static {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/neoquest", "postgres", "********");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getQueryString());
        if (req.getParameter("token") != null) {
            if (req.getParameter("token").equals(TOKEN)) {
                String hash = req.getParameter("hash");
                if (hash == null) {

                    try {

                        Statement statement = connection.createStatement();
                        ResultSet rs = statement.executeQuery("SELECT * FROM easy_words ORDER BY random() LIMIT 1");
                        rs.next();
                        Word word = new Word(rs.getString("word"), rs.getString("translate"), 0);
                        resp.setHeader("hash", word.hash);
                        System.out.println("First - " + word.hash);
                        ImageIO.write(word.generateImage(), "png", new File("1.png"));
                        ImageIO.write(word.generateImage(), "png", resp.getOutputStream());
                        list.add(word);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    Word lastWord = list.get(hash);

                    if (lastWord != null) {
                        System.out.println("Next - " + lastWord.counter);

                        System.out.println(URLDecoder.decode(req.getParameter("answer")) + "   " + lastWord.answer);
                        double c = comapreStr(URLDecoder.decode(req.getParameter("answer"), "UTF-8"), lastWord.answer);
                        System.out.println(c*(double)100 + "%");
                        if (c < (double)0.7) {
                            System.out.println("Wrong answer");
                            resp.setContentType("application/json");
                            resp.getWriter().write(WRONG_ANSWER);
                            return;
                        }

                        if (lastWord.counter == 4) {
                            resp.setContentType("application/json");
                            resp.getWriter().write(ANSWER_MESSAGE);
                            return;
                        }
                        try {
                            Statement statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery("SELECT * FROM easy_words ORDER BY random() LIMIT 1");
                            rs.next();
                            Word word = new Word(rs.getString("word"), rs.getString("translate"), lastWord.counter + 1);
                            resp.setHeader("hash", word.hash);
                            list.add(word);
                            System.out.println("New word - " + rs.getString("translate"));
                            ImageIO.write(word.generateImage(), "png", resp.getOutputStream());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Time is over");
                        for (Word w : list.list) {
                            System.out.print(w.answer + " ");
                        }
                        System.out.println("");
                        resp.setContentType("application/json");
                        resp.getWriter().write(TIME_IS_OVER);
                        return;
                    }
                }
            }
        }
    }

    public double comapreStr(String a, String b) {
        int m = 0;
        for (int i = 0; i < a.length() && i < b.length(); i++) {
            if (a.toUpperCase().charAt(i) == b.toUpperCase().charAt(i)) {
                m += 2;
            }
        }
        return (double)m / (double)(a.length() + b.length());
    }
}
