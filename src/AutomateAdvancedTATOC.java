import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;


public class AutomateAdvancedTATOC {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		
		System.setProperty("webdriver.chrome.driver", "/home/qainfotech/Downloads/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("http://10.0.1.86/tatoc");
        driver.findElement(By.linkText("Advanced Course")).click();
        
        WebElement hoverElement = driver.findElement(By.className("m2"));
        Actions builder = new Actions(driver);
        builder.moveToElement(hoverElement).build().perform();
        List<WebElement> elements =   driver.findElements(By.className("menuitem"));
        for(WebElement element : elements) {
        	builder.moveToElement(element).build().perform();
        	if(element.getText().equals("Go Next")) {
        		element.click();
                break;        		
        	} 	
        }
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://10.0.1.86:3306/tatoc","tatocuser","tatoc01");  
        Statement stmt = conn.createStatement();  
        String symbol = driver.findElement(By.id("symboldisplay")).getText();
        ResultSet rs;
        rs = stmt.executeQuery("select id from identity where symbol="+"'"+symbol+"'");
        int id = 0;
        while(rs.next())  
        	id = rs.getInt(1);
        rs = stmt.executeQuery("select name,passkey from credentials where id="+id);
        String name = "";
        String passkey = "";
        while(rs.next()) {
        	name = rs.getString(1);
        	passkey  = rs.getString(2);
        }
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("passkey")).sendKeys(passkey);
        driver.findElement(By.id("submit")).click();
        
        ((JavascriptExecutor)driver).executeScript("window.played = true;");
        driver.findElement(By.linkText("Proceed")).click();
        
        String[] session = driver.findElement(By.id("session_id")).getText().split(":");
        String sessionId = session[session.length-1].trim();

        String url = "http://10.0.1.86/tatoc/advanced/rest/service/token/"+sessionId;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Chrome/67.0.3396.99");
        int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
        JSONObject jobj = new JSONObject(response.toString());
        System.out.println(jobj);
        String token = (String) jobj.get("token");
        System.out.println(token);
        System.out.println("session id="+sessionId);
        url = "http://10.0.1.86/tatoc/advanced/rest/service/register";
        obj = new URL(url);
        
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Chrome/67.0.3396.99");
        
    
        connection.setDoOutput(true);
        String input = "id="+sessionId+",signature="+token+",allow_access=1";

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(input);
		wr.flush();
		wr.close();
		responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
//		connection.disconnect();
        driver.findElement(By.linkText("Proceed")).click();
   
        
        
        /*driver.findElement(By.linkText("Download File")).click();
        Thread.sleep(1000);
        int i = 0;
        Scanner s = new Scanner(new File("/home/qainfotech/Downloads/file_handle_test.dat"));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()){
        	++i;
            list.add(s.next());
        }
        s.close();
        String key = list.get(i-1);
        System.out.println(key);
        
        driver.findElement(By.id("signature")).sendKeys(key);
        driver.findElement(By.className("submit")).click();*/

		/*HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", "Chrome/67.0.3396.99");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("id", sessionId));
		urlParameters.add(new BasicNameValuePair("signature", token));
		urlParameters.add(new BasicNameValuePair("allow_access", "1"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		client.execute(post);
		driver.findElement(By.linkText("Proceed")).click();*/

		/*HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + 
                                    response.getStatusLine().getStatusCode());
*/

        
        
        
        
        
        
        
        
        
        
        
        
	}

}
