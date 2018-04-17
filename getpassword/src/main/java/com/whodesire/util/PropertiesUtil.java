package com.whodesire.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

public class PropertiesUtil {

	private Map<String, String> properties;
	private String filePath;
	private Boolean addOnUnavailable;
	private Boolean whiteSpaceAllowed;
	private Boolean updateValueOnExistenceEnabled;

	public PropertiesUtil(String filePath) throws URISyntaxException {

		this.filePath = OneMethod.getFilePath(filePath);

		this.properties = new HashMap<String, String>();
		this.addOnUnavailable = new Boolean(false);
		this.whiteSpaceAllowed = new Boolean(false);
		this.updateValueOnExistenceEnabled = new Boolean(false);
	}

	public Boolean isWhiteSpaceAllowed() {
		return whiteSpaceAllowed;
	}

	public void setWhiteSpaceAllowed(Boolean whiteSpaceAllowed) {
		this.whiteSpaceAllowed = whiteSpaceAllowed;
	}

	public Boolean isUpdateValueOnExistenceEnabled() {
		return updateValueOnExistenceEnabled;
	}

	public void setUpdateValueOnExistenceEnabled(Boolean updateValueOnExistenceEnabled) {
		this.updateValueOnExistenceEnabled = updateValueOnExistenceEnabled;
	}

	private List<String> getFileLines() {
		List<String> lines = Collections.emptyList();
		try {
			lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return lines;
	}

	private final void writeFile(List<String> lines) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		for (String element : lines) {
			out.writeBytes(element + "\n");
		}
		Files.write(Paths.get(filePath), baos.toByteArray());
	}

	private void readAndUpdateProperties() {
		properties.clear();

		List<String> lines = getFileLines();
		Iterator<String> itr = lines.iterator();

		while (itr.hasNext()) {
			String line = itr.next();
			if (line.indexOf("=") != -1) {

				String property = line.substring(0, line.indexOf("="));
				String value = line.substring(line.indexOf("=")+1);

				if (!isWhiteSpaceAllowed())
					properties.put(property.trim(), value.trim());
				else
					properties.put(property, value);
			}
		}
	}

	public Boolean getAddOnUnavailable() {
		return addOnUnavailable;
	}

	public void setAddOnUnavailable(Boolean addOnUnavailable) {
		this.addOnUnavailable = addOnUnavailable;
	}

	public final String getProperty(String property) {
		readAndUpdateProperties();
		return properties.get(property);
	}

	public final List<String> getPropertiesKeys() {
		readAndUpdateProperties();
		return new ArrayList<String>(properties.keySet());
	}

	public final List<String> getPropertiesValues() {
		readAndUpdateProperties();
		return new ArrayList<String>(properties.values());
	}

	public final Map<String, String> getPropertiesMap() {
		readAndUpdateProperties();
		return properties;
	}

	public final void addProperty(final String property, final String value) {
		readAndUpdateProperties();

		if (!properties.containsKey(property)) {
			List<String> lines = getFileLines();
			lines.add(property + "=" + value);

			try {
				writeFile(lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public final void addProperties(final Map<String, String> prop) {
		readAndUpdateProperties();

		List<String> lines = getFileLines();
		Set<Entry<String, String>> entrySet = prop.entrySet();
		Iterator<Entry<String, String>> itr = entrySet.iterator();

		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();

			// eliminate duplicate properties on basis of property name
			if (!properties.containsKey(entry.getKey())){
				lines.add(entry.getKey() + "=" + entry.getValue());
			
			}else if (isUpdateValueOnExistenceEnabled()) {
				String value = properties.get(entry.getKey());
				int index = lines.indexOf(entry.getKey() + "=" + value);
				
				if(!isWhiteSpaceAllowed()){
					lines.set(index, entry.getKey().trim() + "=" + entry.getValue().trim());
				}else{
					lines.set(index, entry.getKey() + "=" + entry.getValue());
				}
			}
		}

		try {
			writeFile(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void setProperty(final String property, final String value) {
		List<String> lines = getFileLines();
		boolean flag = false;

		try {
			single: for (String line : lines) {
				if (line.indexOf("=") != -1) {
					if (!isWhiteSpaceAllowed()) {
						if (line.substring(0, line.indexOf("=")).trim().equals(property)) {
							lines.set(lines.indexOf(line), property + "=" + value);
							flag = true;
							break single;
						}
					} else {
						if (line.substring(0, line.indexOf("=")).equals(property)) {
							lines.set(lines.indexOf(line), property + "=" + value);
							flag = true;
							break single;
						}
					}
				}
			}

			if (!flag && getAddOnUnavailable())
				lines.add(property + "=" + value);

			writeFile(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void setPropertyList(final List<String> propertyList, final List<String> valueList) {

		List<String> lines = getFileLines();

		try {

			for (String list : propertyList) {
				boolean flag = false;

				single: for (String line : lines) {

					String prop = null;

					if (!isWhiteSpaceAllowed()) {
						prop = line.substring(0, line.indexOf("=")).trim();
					} else {
						prop = line.substring(0, line.indexOf("="));
					}

					if (prop.equals(list)) {
						int line_index = lines.indexOf(line);
						int prop_index = propertyList.indexOf(list);
						lines.set(line_index, list + "=" + valueList.get(prop_index));
						flag = true;
						break single;
					}
				}

				if (!flag && getAddOnUnavailable())
					lines.add(list + "=" + valueList.get(propertyList.indexOf(list)));
			}

			writeFile(lines);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void setProperties(final Map<String, String> propertyMap) {

		List<String> propertyList = new ArrayList<String>(propertyMap.keySet());
		List<String> valueList = new ArrayList<String>();

		for (String value : propertyList)
			valueList.add(propertyMap.get(value));

		setPropertyList(propertyList, valueList);

	}

	public final void removeProperty(final String property) {
		List<String> lines = getFileLines();
		List<String> removingLines = new ArrayList<String>();

		try {
			single: for (String line : lines) {
				if (line.indexOf("=") != -1) {

					if (!isWhiteSpaceAllowed()) {
						if (line.substring(0, line.indexOf("=")).trim().equals(property)) {
							removingLines.add(line);
							break single;
						}
					} else {
						if (line.substring(0, line.indexOf("=")).equals(property)) {
							removingLines.add(line);
							break single;
						}
					}

				}
			}

			lines.removeAll(removingLines);

			writeFile(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void removeProperties(final List<String> propertyList) {
		List<String> lines = getFileLines();
		List<String> removingLines = new ArrayList<String>();

		try {

			for (String list : propertyList) {

				single: for (String line : lines) {

					if (!isWhiteSpaceAllowed()) {
						String prop = line.substring(0, line.indexOf("=")).trim();
						if (prop.equals(list)) {
							lines.remove(lines.indexOf(line));
							removingLines.add(line);
							break single;
						}
					} else {
						String prop = line.substring(0, line.indexOf("="));
						if (prop.equals(list)) {
							lines.remove(lines.indexOf(line));
							removingLines.add(line);
							break single;
						}
					}

				}
			}

			lines.removeAll(removingLines);

			writeFile(lines);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
