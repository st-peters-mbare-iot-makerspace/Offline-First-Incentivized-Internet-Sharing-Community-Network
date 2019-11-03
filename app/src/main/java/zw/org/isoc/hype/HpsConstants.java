package zw.org.isoc.hype;


import java.util.ArrayList;
import java.util.Arrays;

public class HpsConstants
{
    static public final String APP_IDENTIFIER = "f09811e0";
    static public final String ACCESS_TOKEN = "92de273009ac0ef3";
    static public final String HASH_ALGORITHM = "SHA-1";
    static public final int HASH_ALGORITHM_DIGEST_LENGTH = 20;
    static public final String ENCODING_STANDARD = "UTF-8";
    static public final String NOTIFICATIONS_CHANNEL = "Mbare";
    static public final String NOTIFICATIONS_TITLE = "Mbare";
    static public final String LOG_PREFIX = " :: HpsApplication :: ";
    static public final ArrayList<String> STANDARD_HYPE_SERVICES = new ArrayList<>(Arrays.asList(
            "hype-jobs", "hype-sports", "hype-news", "hype-weather", "hype-music", "hype-movies"));
    static public final int REQUEST_ACCESS_COARSE_LOCATION_ID = 0;
}
