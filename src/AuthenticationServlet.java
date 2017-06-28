import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Anton on 12.01.2016.
 */
public class AuthenticationServlet extends HttpServlet {
    public static final String USERNAME = "********";
    private final String PASSWORD = "*********";
    private final String PHONE = "+79999999999";

    private final String ACCESS_TOKEN = "{\n  \"access_token\": \"******\"\n}";
    private final String ERROR_MESSAGE = "{\n  \"error\": \"Wrong credentials\"\n}";
    private static BufferedImage PHOTO;

    static {
        try {
            PHOTO = ImageIO.read(new File("img\\cat.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (req.getHeader("username").equals(USERNAME)) {
            if (req.getHeader("password").equals(PASSWORD)) {
                if (req.getHeader("phone").equals(PHONE)) {
                    if (checkImage(req.getInputStream())) {
                        resp.getWriter().write(ACCESS_TOKEN);
                        return;
                    }
                }
            }
        }
        resp.getWriter().write(ERROR_MESSAGE);
    }

    private boolean checkImage(InputStream is) {
        try {
            //byte[] bytes = toByteArray(is);
            BufferedImage image = ImageIO.read(is);
            if (equalsImages(image, PHOTO)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }
        return baos.toByteArray();
    }

    private boolean equalsImages(BufferedImage a, BufferedImage b) {
        int width = a.getWidth();
        int height = a.getHeight();
        if (width != b.getWidth() || height != b.getHeight()) {
            return false;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++){
                if(a.getRGB(i, j) != b.getRGB(i, j)){
                    return false;
                }
            }
        }
        return true;
    }
}
