import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class test {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String URL = "http://127.0.0.1";
		String str = "http://127.0.0.1/vulnerabilities/xss_s/";

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Long scanDate = timestamp.getTime();
        String scanIDInitiator = scanDate+URL;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] scanID = digest.digest(scanIDInitiator.getBytes(StandardCharsets.UTF_8));
		
        XSSStored storedXSS = new XSSStored();
    	storedXSS.getXSS(URL, str, scanID, scanDate);

	}
}
