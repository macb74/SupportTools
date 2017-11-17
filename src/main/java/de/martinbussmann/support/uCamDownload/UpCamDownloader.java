package de.martinbussmann.support.uCamDownload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpCamDownloader {
	
    private static String HOST;
    private static long START;
    private static long END;
    private static String LOCATION;
    
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private CloseableHttpClient client;
	
	public static void main( String[] args )
    {
    	Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("START", "20171114230000");
		arguments.put("END",   "20171114234400");
		arguments.put("HOST", "192.168.178.22");
		arguments.put("LOCATION", ".");
    	new UpCamDownloader(arguments);
    }
	
    public UpCamDownloader(Map<String, String> arguments) {
    	if(arguments.get("START").substring(0, 2).equals("20")) {
    		arguments.put("START", arguments.get("START").substring(2));
    	}

    	if(arguments.get("END").substring(0, 2).equals("20")) {
    		arguments.put("END", arguments.get("END").substring(2));
    	}
    	
    	LOCATION = arguments.get("LOCATION");
    	START = Long.valueOf(arguments.get("START"));
    	END   = Long.valueOf(arguments.get("END"));
    	HOST  = arguments.get("HOST");
    	    	    	
		boolean run = true;
	    while(run) {
    		try {

	    		client = openConnection();
		    	List<String> remoteFiles = getFileList("http://" + HOST + "/sd/");
				List<String> localFiles = getLocalFiles();
		    	List<String> validFiles = getValidFiles(remoteFiles, localFiles);
		    	downloadFiles(validFiles, "http://" + HOST);
				client.close();
				
				Thread.sleep(30000);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		if(END > getCurrentTimeStamp()) { run = false; }
	    }
	    
	}
	
    private void downloadFiles(List<String> validFiles, String url) {
    	
    	for ( String file : validFiles ) {
    	
	        File outFile = new File(getFileName(file));
	    		
			try {
				CloseableHttpResponse response = client.execute(new HttpGet(url + file));
				
				try {
					
					int statusCode = response.getStatusLine().getStatusCode();
					
					log.info("getting file: {} ", url + file);
					log.debug("Response Code {}: ", statusCode);
				
					if(statusCode == 200) {
						HttpEntity entity = response.getEntity();
			    	
			    	    if (entity != null) {
			    	    	FileOutputStream outstream = new FileOutputStream(outFile);
			    	    	entity.writeTo(outstream);
			
			    	    }
			    	}
					
				} finally {
					log.info("done");
				    response.close();
				}
				
	        } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
    	}
	    	
	}

	private List<String> getLocalFiles() {
    	List<String> localFiles = new ArrayList<String>();;
    	List<Path> localPaths = null;
    	try {
    		localPaths = Files.walk(Paths.get(LOCATION), 1)
			.filter(p -> p.getFileName().toString().endsWith(".avi"))
			.filter(Files::isRegularFile)
			.collect(Collectors.toList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for ( Path path : localPaths ) {
    		localFiles.add(path.getFileName().toString());
    		//log.info("local files: {}", path.getFileName().toString());
    	}
    	
		return localFiles;
	}

	private List<String> getValidFiles(List<String> remoteFiles, List<String> localFiles) {
		String s = null;
		String end = null;
		Long start = null;
		
		String regex = "P(.*)_(.*).avi$";
		Pattern pattern = Pattern.compile(regex);
		
		List<String> validFiles = new ArrayList<String>();
		
		for ( String file : remoteFiles ) {
		
			Matcher matcher = pattern.matcher(file);
			while (matcher.find()) {
				s = matcher.group(1);
				s = s.replaceAll("_", "");
				start = Long.valueOf(s);
				
				end = matcher.group(2);

				if(start > START && !end.equals("999999") && start < END && !localFiles.contains(getFileName(file))) {				
					validFiles.add(file);
					log.info("valid File: {}", file);
				}
			}
			
		}
		
		return validFiles;
		
	}

	private String getFileName(String path) {
		String file = null;
		String regex = ".*(P.*.avi)$";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			file = matcher.group(1);
		}
		
		return file;
	}

	private List<String> getFileList(String url) {
		
    	List<String> files = new ArrayList<String>();
    	
    	List<String> folders = getFiles(url);
    	for ( String folder : folders ) {
    		List<String> f = getFiles("http://" + HOST + folder + "record000/");
    		files.addAll(f);
    	}
    	
    	return files;
	}

	private List<String> getFiles(String url) {
		List<String> files = new ArrayList<String>();
		
		try {
			String regex = "^<tr>.*href=\"(.*)\"";
			Pattern pattern = Pattern.compile(regex);

			CloseableHttpResponse response = client.execute(new HttpGet(url));
			try {
				
				int statusCode = response.getStatusLine().getStatusCode();

				log.info("Sending 'GET' request to URL: {} ", url);
				log.debug("Response Code {}: ", statusCode);
				
				if(statusCode == 200) {
					HttpEntity entity = response.getEntity();
					
					BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
					String inputLine;
			
					while ((inputLine = in.readLine()) != null) {

						Matcher matcher = pattern.matcher(inputLine);
						while (matcher.find()) {
							files.add(matcher.group(1));
							//log.info("getting File: {}", matcher.group(1));
						}
						
					}
					in.close();
				}
				
			} finally {
			    response.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return files;
	}


	public static Long getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMddHHmmss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return Long.valueOf(strDate);
	}
	
	
	private CloseableHttpClient openConnection() {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");
		provider.setCredentials(AuthScope.ANY, credentials);

		CloseableHttpClient client = HttpClients.custom()
				.setDefaultCredentialsProvider(provider)
				.build();
		
//		HttpClient client = HttpClientBuilder.create()
//				.setDefaultCredentialsProvider(provider)
//				.build();
		
		return client;
	}
	    
}
