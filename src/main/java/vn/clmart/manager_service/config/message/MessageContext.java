package vn.clmart.manager_service.config.message;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Log4j2
public class MessageContext{
    private static MessageContext instance;
    private static final String BUNDLE_NAME = "message";
    private ResourceBundleMessageSource fileResourceSource = new ResourceBundleMessageSource();
    private StaticMessageSource messageSource = new StaticMessageSource();
    private Map<Locale, Set<String>> localeCompanies = new ConcurrentHashMap();
    private static final String COMPANY_KEY = "cid";
    private static final String SEPARATE_KEY = ".";

    public MessageContext() {
        this.fileResourceSource.setBasenames(new String[]{"message"});
        this.fileResourceSource.setDefaultEncoding("UTF-8");
        this.messageSource.setParentMessageSource(this.fileResourceSource);
    }

    public Map<Locale, Set<String>> getLocaleCompanies() {
        return this.localeCompanies;
    }

    public static void register(String tag, Map<String, String> language) {
        Locale locale = Locale.forLanguageTag(tag);
        getInstance().localeCompanies.put(locale, language.keySet());
        getInstance().messageSource.addMessages(language, locale);
        getInstance().messageSource.setParentMessageSource(getInstance().fileResourceSource);
    }

    public static void register(Map<String, Map<String, String>> languages) {
        languages.forEach((tag, language) -> {
            register(tag, language);
        });
    }

    public static MessageContext getInstance() {
        if (instance == null) {
            instance = new MessageContext();
        }

        return instance;
    }

    public static String getMessage(String key) {
        return getMessage(key, (Object[])null, LocaleContextHolder.getLocale());
    }

    private static HttpServletRequest getRequest() {
        try {
            RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
            if (attribs != null) {
                HttpServletRequest request = ((ServletRequestAttributes)attribs).getRequest();
                return request;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return null;
    }

    public static String getMessage(String key, Object[] args) {
        return getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key, Locale locale) {
        return getMessage(key, (Object[])null, locale);
    }

    public static String getMessage(String key, Object[] args, Locale locale) {
        try {
            String cid = getRequest().getHeader("cid");
            if (cid != null) {
                StringBuffer code = new StringBuffer();
                code.append(cid).append(".").append(key);
                Set<String> keys = (Set)getInstance().getLocaleCompanies().get(locale == null ? LocaleContextHolder.getLocale() : locale);
                if (keys != null && keys.contains(code.toString())) {
                    key = code.toString();
                }
            }

            return getInstance().getMessageSource() != null ? getInstance().getMessageSource().getMessage(key, args, locale) : key;
        } catch (Exception var6) {
            return key;
        }
    }

    public StaticMessageSource getMessageSource() {
        return this.messageSource;
    }
}
