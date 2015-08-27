package su.whs.call.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by featZima on 30.08.2014.
 */
public class Strings {
    private final static Pattern pattern = Pattern.compile("\\{.*?\\}");
    private String stringToReplace;

    private Strings(String stringToReplace) {
        this.stringToReplace = stringToReplace;
    }

    public static Strings create(String stringToReplace) {
        return new Strings(stringToReplace);
    }

    public Strings setParam(String param, String value) {
        if (stringToReplace.contains("{" + param + "}"))
            stringToReplace = stringToReplace.replace("{" + param + "}", value);
        else
            throw new ParamNotExistsException(param);

        return this;
    }

    public String make() {
        final Matcher matcher = pattern.matcher(stringToReplace);
        if (matcher.find()) {
            throw new SomeParamsNotSetException(stringToReplace);
        } else {
            return stringToReplace;
        }
    }

    public class ParamNotExistsException extends RuntimeException {

        public ParamNotExistsException(String param) {
            super("Param {" + param + "} not exists");
        }
    }

    public class SomeParamsNotSetException extends RuntimeException {

        public SomeParamsNotSetException(String str) {
            super("Some params not set " + str);
        }
    }

}
