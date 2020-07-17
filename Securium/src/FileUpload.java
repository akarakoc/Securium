import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sqlite.SQLiteDataSource;

public class FileUpload {
	public void getCommandInjection(String URL, String str, byte[] scanID, Long scanDate) {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ALI\\Desktop\\selenium\\chrome\\chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		driver.get(URL);
		driver.findElement(By.xpath("//*[@name='username']")).sendKeys("admin");
		driver.findElement(By.xpath("//*[@name='password']")).sendKeys("password");
		driver.findElement(By.xpath("//*[@name='Login']")).click();
		driver.get(str);
		List<WebElement> inputElements = driver.findElements(By.xpath("//*[@type='file']")); 
		for (WebElement Element : inputElements) {
			try {
					String attrName = Element.getAttribute("name");
					if(!driver.getPageSource().contains("password_new") && !driver.getPageSource().contains("password_conf")){
						System.out.println(attrName);
						driver.findElement(By.xpath("//*[@name='"+attrName+"']")).sendKeys("C:\\Users\\ALI\\eclipse-java-workspace\\Selenium\\bin\\SecurityTest.php");
						driver.findElement(By.xpath("//*[@type='submit']")).click();
					}
					String uploadURL = URL+"/hackable/uploads/SecurityTest.php";
					driver.get(uploadURL);
					if(driver.getPageSource().contains("SecuriumFileUpload")) {
						System.out.println(attrName);
						SQLiteDataSource ds = null;

				        try {
				            ds = new SQLiteDataSource();
				            ds.setUrl("jdbc:sqlite:test.db");
				        } catch ( Exception e ) {
				            e.printStackTrace();
				            System.exit(0);
				        }
				        
				        String queryInsertURL = "INSERT INTO vulnList ( baseURL, fullURL, parameter, vulnerability, scanID, scanDate ) VALUES ( '"+URL+"','"+str+"','"+attrName+"','Arbitrary File Upload','"+scanID+"','"+scanDate+"')";
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
