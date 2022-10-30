package vn.clmart.manager_service.config.font;


import com.lowagie.text.Font;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Color;
import java.util.Properties;
import java.util.Set;

public final class FontFactory {
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES = "Times";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    private static FontFactoryImp fontImp = new FontFactoryImp();
    public static String defaultEncoding = "UTF8";
    public static boolean defaultEmbedding = false;

    private FontFactory() {
    }

    public static Font getFont(String fontname, String encoding, boolean embedded, float size, int style, Color color) {
        return fontImp.getFont(fontname, encoding, embedded, size, style, color);
    }

    public static Font getFont(String fontname, String encoding, boolean embedded, float size, int style, Color color, boolean cached) {
        return fontImp.getFont(fontname, encoding, embedded, size, style, color, cached);
    }

    public static Font getFont(Properties attributes) {
        fontImp.defaultEmbedding = defaultEmbedding;
        fontImp.defaultEncoding = defaultEncoding;
        return fontImp.getFont(attributes);
    }

    public static Font getFont(String fontname, String encoding, boolean embedded, float size, int style) {
        return getFont(fontname, encoding, embedded, size, style, (Color)null);
    }

    public static Font getFont(String fontname, String encoding, boolean embedded, float size) {
        return getFont(fontname, encoding, embedded, size, -1, (Color)null);
    }

    public static Font getFont(String fontname, String encoding, boolean embedded) {
        return getFont(fontname, encoding, embedded, -1.0F, -1, (Color)null);
    }

    public static Font getFont(String fontname, String encoding, float size, int style, Color color) {
        return getFont(fontname, encoding, defaultEmbedding, size, style, color);
    }

    public static Font getFont(String fontname, String encoding, float size, int style) {
        return getFont(fontname, encoding, defaultEmbedding, size, style, (Color)null);
    }

    public static Font getFont(String fontname, String encoding, float size) {
        return getFont(fontname, encoding, defaultEmbedding, size, -1, (Color)null);
    }

    public static Font getFont(String fontname, String encoding) {
        return getFont(fontname, encoding, defaultEmbedding, -1.0F, -1, (Color)null);
    }

    public static Font getFont(String fontname, float size, int style, Color color) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, size, style, color);
    }

    public static Font getFont(String fontname, float size, Color color) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, size, -1, color);
    }

    public static Font getFont(String fontname, float size, int style) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, size, style, (Color)null);
    }

    public static Font getFont(String fontname, float size) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, size, -1, (Color)null);
    }

    public static Font getFont(String fontname) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, -1.0F, -1, (Color)null);
    }

    public void registerFamily(String familyName, String fullName, String path) {
        fontImp.registerFamily(familyName, fullName, path);
    }

    public static void register(String path) {
        register(path, (String)null);
    }

    public static void register(String path, String alias) {
        fontImp.register(path, alias);
    }

    public static int registerDirectory(String dir) {
        return fontImp.registerDirectory(dir);
    }

    public static int registerDirectory(String dir, boolean scanSubdirectories) {
        return fontImp.registerDirectory(dir, scanSubdirectories);
    }

    public static int registerDirectories() {
        return fontImp.registerDirectories();
    }

    public static Set<String> getRegisteredFonts() {
        return fontImp.getRegisteredFonts();
    }

    public static Set<String> getRegisteredFamilies() {
        return fontImp.getRegisteredFamilies();
    }

    public static boolean contains(String fontname) {
        return fontImp.isRegistered(fontname);
    }

    public static boolean isRegistered(String fontname) {
        return fontImp.isRegistered(fontname);
    }

    public static FontFactoryImp getFontImp() {
        return fontImp;
    }
}
