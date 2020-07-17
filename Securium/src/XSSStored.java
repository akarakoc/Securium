import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sqlite.SQLiteDataSource;

public class XSSStored {


	
	public void getXSS(String URL, String str, byte[] scanID, Long scanDate) {
		
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ALI\\Desktop\\selenium\\chrome\\chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		
		driver.get(URL);
		driver.findElement(By.xpath("//*[@name='username']")).sendKeys("admin");
		driver.findElement(By.xpath("//*[@name='password']")).sendKeys("password");
		driver.findElement(By.xpath("//*[@name='Login']")).click();
		driver.get(str);
		
		List<WebElement> inputElements = driver.findElements(By.xpath("//*[@type='text']")); 
		
		for (WebElement Element : inputElements) {
			
			String attrName = Element.getAttribute("name");
			try {
				if(!driver.getPageSource().contains("password_new") && !driver.getPageSource().contains("password_conf")){
					driver.findElement(By.xpath("//*[@name='"+attrName+"']")).sendKeys("test");
					driver.findElement(By.cssSelector("textarea")).sendKeys("<script>alert(\"SecuriumSTORE\");</script>");
					driver.findElement(By.xpath("//*[@name='btnSign']")).click();
				}	
			}catch(Exception e) {}
			
			try {
				Alert alert = driver.switchTo().alert();
				String alertMessage= driver.switchTo().alert().getText();
				System.out.println(alertMessage);
				alert.accept();
				
				driver.get(str);
				alert.accept();

				driver.findElement(By.xpath("//*[@name='btnClear']")).click();
				driver.switchTo().alert().accept();
		
	
					if(alertMessage.contains("SecuriumSTORE")) {
						
						System.out.println(attrName);
						SQLiteDataSource ds = null;

				        try {
				        	
				        	
				            ds = new SQLiteDataSource();
				            ds.setUrl("jdbc:sqlite:test.db");
				            
				            
				        } catch ( Exception e ) {
				        	
				            e.printStackTrace();
				            System.exit(0);
				            
				        }
				        
				        String queryInsertURL = "INSERT INTO vulnList ( baseURL, fullURL, parameter, vulnerability, scanID, scanDate ) VALUES ( '"+URL+"','"+str+"','"+attrName+"','Stored XSS','"+scanID+"','"+scanDate+"')";
			        	
				        
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
