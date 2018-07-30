package com.mauropelucchi.git_spark;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The top-100 contributors of Apache Spark
 * 
 * Read Spark Contributors JSON File and get info from the users
 * 
 * @author mauropelucchi
 *
 */
public class App {
	public static void main(String[] args) {
		try {
			FileWriter writer = new FileWriter("spark_contributors.csv");
			writer.append("\"id\"|\"user\"|\"name\"|\"homeLocation\"|\"worksFor\"|\"total\"\n");
			
			String contributors_json = ReadFile("spark_contributors.json"); // download @ 27 July 2018
			// parse json file
			JsonParser jparser = new JsonParser();
			JsonArray obj = jparser.parse(contributors_json).getAsJsonArray();
			for(JsonElement ele : obj) {
				String id = ele.getAsJsonObject().get("author").getAsJsonObject().get("id").getAsString();
				String user = ele.getAsJsonObject().get("author").getAsJsonObject().get("login").getAsString();
				System.out.println("---> " + id + " - " + user);
				String workFor = "";
				String name = "";
				String homeLocation = "";
				long total = 0l;

				Document doc = Jsoup.connect("https://github.com/" + user).timeout(3000).get();
				if(doc.select("[itemprop=\"worksFor\"]").size() > 0) {
					workFor = doc.select("[itemprop=\"worksFor\"]").text();
				}
				if(doc.select("[itemprop=\"homeLocation\"]").size() > 0) {
					homeLocation = doc.select("[itemprop=\"homeLocation\"]").text();
				}
				name = doc.select("[itemprop=\"name\"]").text();
				total = ele.getAsJsonObject().get("total").getAsLong();
				
				writer.append("\""+ id + "\"|\""+ user + "\"|\""+ name + "\"|\"" + homeLocation +"\"|\"" + workFor +"\"|" + total + "\n");
				Thread.sleep(100);
				writer.flush();
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String ReadFile(String filePath) {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}
}
