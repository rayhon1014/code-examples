package com.cf.util;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: raymond hon
 * Date: 9/13/12
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(FileUtil.class);

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	public static List<String> readFileFromClassPath(String urlFile) {
		List<String> lines = new ArrayList<String>();
		try {
			///inputUrl.txt
			BufferedReader br = new BufferedReader(new InputStreamReader(FileUtil.class.getClassLoader().getResourceAsStream(urlFile)));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
		} catch (Exception e) {//Catch exception if any
			LOGGER.error(e);
		}
		return lines;
	}

	public static String readResource(String resPath) throws IOException {
		StringBuilder sb        = new StringBuilder();
		BufferedReader reader   = null;

		try {
			Resource resource = resourceLoader.getResource(resPath);
			reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				sb.append(line+"\n");
			}
		} catch(IOException ex) {
			throw ex;
		} finally {
			if(reader!=null)
				reader.close();
		}

		return sb.toString();
	}

	public static InputStream getResourceStream(String resourceName) {
		InputStream stream = FileUtil.class.getClassLoader().getResourceAsStream(resourceName);
		return stream;
	}

	public static Reader getResourceReader(String resourceName) {
		return new InputStreamReader(FileUtil.getResourceStream(resourceName));
	}

	public static void writeToCsvFile(String filename, String[] headers, List<String[]> data) throws Exception {
		CSVWriter writer = new CSVWriter(new FileWriter(filename), ',', CSVWriter.NO_QUOTE_CHARACTER);

		// feed in your array (or convert your data to an array)
		writer.writeNext(headers);
		for (String[] entries : data) {
			writer.writeNext(entries);
		}
		writer.close();
	}

	public static long writeToFile(String data, String filePath) throws Exception {
		File file = new File(filePath);
		boolean isDirCreated = file.getParentFile().mkdirs();

		OutputStream os = new FileOutputStream(filePath);
		os.write(data.getBytes());
		os.close();

		return file.length();
	}

	public static void createCsvFrmJsonArray(JSONArray dataJsonArray, String fileName) {
		String csv = null;
		Date date = new Date();
		String createdDate = new SimpleDateFormat("yyyyMMdd").format(date);
		File csvFileName = new File(fileName + createdDate + ".csv");
		try {
			csv = CDL.toString(dataJsonArray).replaceAll("\'", "");
			FileUtils.writeStringToFile(csvFileName, csv, true);
		} catch (JSONException e) {
			LOGGER.info(e);
		} catch (IOException e) {
			LOGGER.info(e);
		}
	}

	public static InputStream readFile (String filePath) throws FileNotFoundException{
		File file = new File(filePath);

		// Attempt to read from external first
		if (file.exists() && file.isFile()) {
			return new FileInputStream(file);
		}

		return FileUtil.class.getClassLoader().getResourceAsStream(filePath);
	}

	public static char getFieldSeparator(String filePath) {
		File file = new File(filePath);

		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist");
		}

		char delimiter = '\t'; // use tab as default

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String firstLine = reader.readLine();

			if (!StringUtils.isEmpty(firstLine)) {
				if (firstLine.contains("\t")) {
					delimiter = '\t';
				} else if (firstLine.contains("|")) {
					delimiter = '|';
				} else if (firstLine.contains(";")) {
					delimiter = ';';
				} else if (firstLine.contains(",")) {
					delimiter = ',';
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("There was no file found", e);
		} catch (IOException e) {
			throw new RuntimeException("There was a problem reading the file", e);
		}

		return delimiter;
	}
}
