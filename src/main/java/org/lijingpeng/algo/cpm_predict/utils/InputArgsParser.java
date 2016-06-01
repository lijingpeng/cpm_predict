package org.lijingpeng.algo.cpm_predict.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class InputArgsParser {
    
    private static Log log = LogFactory.getLog(InputArgsParser.class);

    protected static Map<String, String> mArgKVMap = 
        new HashMap<String, String>();

    public static String getArgValue(String key) {
        return mArgKVMap.get(key);
    }

    public static void parseArgs(String[] args) {
        int argNum = args.length;

        if (argNum != Constants.ARG_VALID_NUM) {
            log.error("Invalid arg num!");
        }

        for (int i = 0; i < Constants.ARG_VALID_NUM; i++) {
            String arg = args[i].trim();

            String[] kv = arg.split("=");

            if (kv.length != 2) {
                log.error("Invalid input arg pattern, must be key=value!");
                System.exit(-1);
            }

            String key   = kv[0].trim();
            String value = kv[1].trim();

            mArgKVMap.put(key, value);
        }

        if (!validateArgs()) {
            log.error("Validating args fails");
            System.exit(-1);
        }

        return;
    }

    private static boolean validateArgs() {
        boolean isValid = true;

        String bizdate = mArgKVMap.get(
                Constants.ARG_KEY_BIZDATE).trim();

        isValid = isValid && isValidDate(bizdate, 
                Constants.ARG_VALID_DATE_FORMAT);

        return isValid;
    }

    private static boolean isValidQuantile(String quantile) {
        boolean valid = true;

        int tmp = Integer.valueOf(quantile);

        if (tmp <= 0 || tmp > 100) {
            log.error("Invalid quantile, must be (0, 100]");

            valid = false;
        }

        return valid;
    }

    private static boolean isValidDate(String date, String dateFormat) {
        boolean valid = false;

        SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        format.setLenient(false);

        try {
            format.parse(date);
            valid = true;
        }
        catch (ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return valid;
    }
}
