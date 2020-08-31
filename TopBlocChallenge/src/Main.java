import java.sql.Connection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Main 
{
	public static void main(String[] args)
	{	
		String dbPath = ".\\Resources\\testing.db";
		String studentInfoPath = ".\\Resources\\Student Info.xlsx";
		String testScoresPath = ".\\Resources\\Test Scores.xlsx";
		String retakeScorePath = ".\\Resources\\Test Retake Scores.xlsx";

		Connection conn;
		conn = DataMigrator.Connect(dbPath);
		
		DataMigrator.CreateTable(conn, studentInfoPath, "StudentInfo");	
		DataMigrator.CreateTable(conn, testScoresPath, "TestScores");
		DataMigrator.CreateTable(conn, retakeScorePath, "RetakeScores");
		
		int averageScore = DataParser.FindAverageScore(conn);
		String[] femaleCompSciMajorIds = DataParser.FindFemaleComputerScienceMajors(conn);
			
		DataMigrator.Disconnect(conn);
		
		CreateJSONRequest(averageScore, femaleCompSciMajorIds);
	}
	
	public static void CreateJSONRequest(int avScore, String[] fMajors)
	{
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("http://54.90.99.192:5000/challenge");
		JSONObject result = new JSONObject();
		try
		{			
            String bodyContent = new JSONObject()
            							.put("id", "ian@enbt.net")
            							.put("name",  "Ian Beatty")
            							.put("average", avScore)
            							.put("studentIds", fMajors).toString();

			
            StringEntity requestBody = new StringEntity(bodyContent);
            
            request.setEntity(requestBody);
            request.setHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(request);
            
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            result.put("status", response.getStatusLine().getStatusCode());
            result.put("bodyContent", new JSONObject(responseString));
        }
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			request.releaseConnection();
		}
	}
}
