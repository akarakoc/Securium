import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.sqlite.SQLiteDataSource;

public class SecurityTester {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		//TODO Auto-generated method stub
		//Create driver object
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ALI\\Desktop\\selenium\\chrome\\chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		String URL = "http://127.0.0.1";
		driver.get(URL);
		driver.findElement(By.xpath("//*[@name='username']")).sendKeys("admin");
		driver.findElement(By.xpath("//*[@name='password']")).sendKeys("password");
		driver.findElement(By.xpath("//*[@name='Login']")).click();
		System.out.println(driver.getTitle());
	    List<WebElement> elements =  driver.findElements(By.xpath("//*[@href]"));
	    Stack<String> WebPages = new Stack<String>();
	    List<String> AllWebPages = new ArrayList<String>();
	    List<String> DiscoveredWebPages = new ArrayList<String>();
		Pattern REGEX = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
		Pattern REGEX_HALF = Pattern.compile("^(?!www\\.|(?:http|ftp)s?://|[A-Za-z]:\\\\|//).*");
		Pattern REGEX_IP = Pattern.compile("^((?:http:\\/\\/)|(?:https:\\/\\/))(www.)?((?:[a-zA-Z0-9]+\\.[a-z]{3})|(?:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(?::\\d+)?))([\\/a-zA-Z0-9\\.]*)$");
		Pattern REGEX_FULL = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Long scanDate = timestamp.getTime();
        String scanIDInitiator = scanDate+URL;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] scanID = digest.digest(scanIDInitiator.getBytes(StandardCharsets.UTF_8));
        
		for(WebElement el:elements) {
			String noParam = el.getAttribute("href");
			try {
			     noParam = el.getAttribute("href").split("\\?")[0];
			}catch(Exception e) {}
			Matcher matcher = REGEX.matcher(noParam);
			Matcher matcher_half = REGEX_HALF.matcher(noParam);
			Matcher matcher_ip = REGEX_IP.matcher(noParam);
			Matcher matcher_full = REGEX_FULL.matcher(noParam);
			
			if (matcher.find() || matcher_ip.find() || matcher_half.find() || matcher_full.find()) {
				if(AllWebPages.contains(el.getAttribute("href").replace("#",""))){
					continue;
				}
				else if(el.getAttribute("href").contains(URL.split("/")[2])) {
		        	String pageName = el.getAttribute("href").replace("#","");
		        	System.out.println(pageName);
		        	WebPages.push(pageName);
		        	AllWebPages.add(pageName);
				}else if(!el.getAttribute("href").contains(URL.split("/")[2])) {
					String pageName = el.getAttribute("href").replace("#","");
		        	System.out.println(pageName);
		        	if(!DiscoveredWebPages.contains(pageName)){
		        		DiscoveredWebPages.add(pageName);
		        	}			
				}
			}
			
        }
		
		
		List<String> finalList = findURLs(WebPages,AllWebPages,URL,scanID,scanDate,DiscoveredWebPages);
		for(String str: finalList) {
			System.out.println(str);
			SQLiteDataSource ds = null;

	        try {
	            ds = new SQLiteDataSource();
	            ds.setUrl("jdbc:sqlite:test.db");
	        } catch ( Exception e ) {
	            e.printStackTrace();
	            System.exit(0);
	        }
	        
			String queryInsertURL = "INSERT INTO urlList ( baseURL, fullURL, scanID, scanDate ) VALUES ( '"+URL+"','"+str+"','"+scanID+"','"+scanDate+"')";
        	try ( Connection conn = ds.getConnection();
        		    Statement stmt = conn.createStatement(); ) {
        		    stmt.executeUpdate( queryInsertURL );
        		    conn.close();

        		    
        		} catch ( SQLException e ) {
        		    e.printStackTrace();
        		    System.exit( 0 );
        		}
        	
	        if (!str.contains("setup.php")) {
	        	
	        	SQLInjection scanSQLInjection = new SQLInjection();
	        	scanSQLInjection.getSQLInjection(URL,str,scanID,scanDate);
	        	
	        	Injection codeInjection = new Injection();
	        	codeInjection.getCommandInjection(URL, str, scanID, scanDate);
	        	
	        	LFIRFI fileInclusion = new LFIRFI();
	        	fileInclusion.getFileInclusion(URL, str, scanID, scanDate);
	        	
	        	XSSDOM dombasedXSS = new XSSDOM();
	        	dombasedXSS.getXSS(URL, str, scanID, scanDate);
	        	
	        	XSSReflective reflectedXSS = new XSSReflective();
	        	reflectedXSS.getXSS(URL, str, scanID, scanDate);
	        	
	        	XSSStored storedXSS = new XSSStored();
	        	storedXSS.getXSS(URL, str, scanID, scanDate);
	        	
	        	FileUpload uploadFile = new FileUpload();
	        	uploadFile.getCommandInjection(URL, str, scanID, scanDate);
	        }
		}
				
	    driver.quit();
	    			
	}
	
	
	
	//CRAWLER FUNCTION
	static List<String> findURLs(Stack<String> WebPages, List<String> AllWebPages, String URL, byte[] scanID, Long scanDate, List<String> DiscoveredWebPages) throws NoSuchAlgorithmException {
		Pattern REGEX = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
		Pattern REGEX_HALF = Pattern.compile("^(?!www\\\\.|(?:http|ftp)s?://|[A-Za-z]:\\\\\\\\|//).*");
		Pattern REGEX_IP = Pattern.compile("^((?:http:\\/\\/)|(?:https:\\/\\/))(www.)?((?:[a-zA-Z0-9]+\\.[a-z]{3})|(?:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(?::\\d+)?))([\\/a-zA-Z0-9\\.]*)$");
		
		ChromeDriver driver = new ChromeDriver();
		if (WebPages.size() > 0) {
			String pageName = WebPages.pop();
			driver.get(URL);
			driver.findElement(By.xpath("//*[@name='username']")).sendKeys("admin");
			driver.findElement(By.xpath("//*[@name='password']")).sendKeys("password");
			driver.findElement(By.xpath("//*[@name='Login']")).click();
			System.out.println("Executing :::" + pageName);
			driver.get(pageName);
			List<WebElement> webElements =  driver.findElements(By.xpath("//*[@href]"));
			for(WebElement we:webElements) {
				Matcher matcher = REGEX.matcher(we.getAttribute("href"));
				Matcher matcher_half = REGEX_HALF.matcher(we.getAttribute("href"));
				Matcher matcher_ip = REGEX_IP.matcher(we.getAttribute("href"));
				if(AllWebPages.contains(we.getAttribute("href"))){
					continue;
				}
				else if (matcher.find() || matcher_half.find() || matcher_ip.find()) {
					if(we.getAttribute("href").contains(URL.split("/")[2])) {
						String webpageName = we.getAttribute("href").replace("#","");
						WebPages.push(webpageName);
						AllWebPages.add(webpageName);
						System.out.println(webpageName);			        	
					}
				}else if(!we.getAttribute("href").contains(URL.split("/")[2])) {
					String webpageName = we.getAttribute("href").replace("#","");
		        	System.out.println(webpageName);
		        	if(!DiscoveredWebPages.contains(webpageName)){
		        		DiscoveredWebPages.add(webpageName);
		        	}	
					
				}
			}
			driver.quit();
			while (WebPages.size() > 0) {
				try {
					findURLs(WebPages,AllWebPages,URL,scanID,scanDate,DiscoveredWebPages);
					driver.quit();
				}catch(Exception e){
					driver.quit();
					continue;
				}		    	
			}
			
		}
		
		for(String str: DiscoveredWebPages) {
			System.out.println(str);
			SQLiteDataSource ds = null;

	        try {
	            ds = new SQLiteDataSource();
	            ds.setUrl("jdbc:sqlite:test.db");
	        } catch ( Exception e ) {
	            e.printStackTrace();
	            System.exit(0);
	        }
	        Integer tot = 0;
	        String querySelectURL = "SELECT COUNT(*) AS total FROM discurlList WHERE URL = '"+str+"'";
	        try ( Connection conn = ds.getConnection();
        		    Statement stmts = conn.createStatement(); ) {
	        		ResultSet rs = stmts.executeQuery(querySelectURL);
	        		tot = rs.getInt("total");
        		    conn.close();

        		    
        		} catch ( SQLException e ) {
        		    e.printStackTrace();
        		    System.exit( 0 );
        	}
	        
	        if(tot == 0) {
    			String queryInsertURL = "INSERT INTO discurlList ( URL, scanID, scanDate ) VALUES ( '"+str+"','"+scanID+"','"+scanDate+"')";
    			
            	try ( Connection conn2 = ds.getConnection();
            		    Statement stmt = conn2.createStatement(); ) {
            		    stmt.executeUpdate( queryInsertURL );
            		    conn2.close();
            		    
            		} catch ( SQLException e ) {
            		    e.printStackTrace();
            		    System.exit( 0 );
            		}	
    		}
	        
		}
		
		return AllWebPages;
	}

	public static Pattern REGEX_URL = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
	public static Pattern REGEX_DIRECTORY = Pattern.compile("^/|(/[a-zA-Z0-9_-]+)+$");
	public static Pattern REGEX_EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	public static Pattern REGEX_PHONE = Pattern.compile("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}");
		
}
