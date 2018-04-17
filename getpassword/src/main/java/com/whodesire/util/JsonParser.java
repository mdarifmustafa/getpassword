package com.whodesire.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.whodesire.data.Resource;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JsonParser {

    private char[] password;
    private static final Type RESOURCE_TYPE = new TypeToken<List<Resource>>() {}.getType();

    private final static Logger logger = Logger.getLogger(JsonParser.class);

    //    private static URL resourceURL = JsonParser.class.getResource("/data/resourcehub.json");
    private static String resourceURL = OneMethod.getFilePath("/data/resourcehub.json");

    public JsonParser() { }

    public JsonParser(char[] password){
        logger.info("Parser Accessed.");
        this.password = password;
    }

    private String readFileData() throws IOException, URISyntaxException {

        List<String> lines = Collections.emptyList();
        StringBuffer buffer = new StringBuffer("");
        
        String value = null;
        
        try {
        	
        	lines = Files.readAllLines(Paths.get(resourceURL), StandardCharsets.UTF_8);

            if(lines.size() > 0){
            	
            	if(lines.get(0).equals("[]") && lines.size() == 1) {
            		lines.clear();
            		value = buffer.toString();
            		return value;
            	}
            	
                Iterator<String> iterator = lines.iterator();
                while (iterator.hasNext()){
                    buffer.append(iterator.next());
                }
            }

            value = buffer.toString();
            
            if(value != null  && value.length() > 0 && password != null){
                Encoder encoder = new Encoder();
                value = String.valueOf(encoder.decryptSentence(password, buffer.toString().toCharArray()));
            }else{
                logger.warn("Password is null");
            }
            
        }catch(NullPointerException npe){
            logger.error(npe.toString());
        	return "[]";
        }catch(IllegalStateException ise){
            logger.error(ise.toString());
            return "[]";
        }

        return value;
    }

    private boolean containsObject(List<Resource> resourceList, Resource resource){

        for(int i = 0; i < resourceList.size(); i++){
            if(resourceList.get(i).getResourceName().equals(resource.getResourceName()))
                return true;
        }

        return false;
    }

    public List<Resource> getResourceList(){

        Gson gson = new Gson();
        List<Resource> resourceList = new ArrayList<>();

        try {

            List<Resource> verifier = gson.fromJson(readFileData(), RESOURCE_TYPE);

            if(verifier != null && verifier.size() > 0){
                Iterator<Resource> iterator = verifier.iterator();
                while(iterator.hasNext()){
                    Resource resource = iterator.next();
                    if(!containsObject(resourceList, resource))
                        resourceList.add(resource);
                }
                verifier.clear();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }catch(JsonSyntaxException e){
            e.printStackTrace();
            logger.error(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }

        return resourceList;
    }

    public int countResourceList(){
        return getResourceList().size();
    }

    public void revalidate(){
        List<Resource> resourceList = getResourceList();
        persistObject(resourceList);
    }

    private void persistObject(List<Resource> resourceList) {

        Gson gson = new GsonBuilder().create();

        try {

            char[] jsonArrayObject = gson.toJson(resourceList, RESOURCE_TYPE).toCharArray();

            if(jsonArrayObject != null && jsonArrayObject.length > 0 && password != null){
                //deleting all contents of file and making the file empty
                new PrintWriter(resourceURL).close();

                Encoder encoder = new Encoder();
                char[] enc_char = encoder.encryptSentence(password, jsonArrayObject);

                Files.write(Paths.get(resourceURL),
                        String.valueOf(enc_char).getBytes(), StandardOpenOption.CREATE);

                resourceList.clear();

            }else{
                logger.warn("Secret Password not provided or Value is Empty.");
                new MessageUtil("Secret Password not provided or Value is Empty.", MessageUtil.LEVEL.CRITICAL);
            }

            jsonArrayObject = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public final char[] getPassword() {
        return password;
    }

    public final void setPassword(char[] password) {
        this.password = password;
    }

    public final void addObject(Resource resource){

        List<Resource> resourceList = getResourceList();

        if(!containsObject(resourceList, resource)){
            resourceList.add(resource);
            persistObject(resourceList);
        }

    }

    public final Resource getResourceObject(Resource resource) {

        List<Resource> resourceList = getResourceList();

        for(int i = 0; i < resourceList.size(); i++){

            if(resourceList.get(i).getResourceName().equals(resource.getResourceName()))
                return resourceList.get(i);

        }
        //In case the List not contained the object, it is null.
        return null;
    }

    public final Resource getResourceObjectByName(String resourceName) {

        List<Resource> resourceList = getResourceList();

        for(int i = 0; i < resourceList.size(); i++){

            if(resourceList.get(i).getResourceName().equals(resourceName))
                return resourceList.get(i);

        }
        //In case the List not contained the object, it is null.
        return null;
    }

    public final int getResourceObjectIndex(Resource resource) {

        List<Resource> resourceList = getResourceList();

        for(int i = 0; i < resourceList.size(); i++){

            if(resourceList.get(i).getResourceName().equals(resource.getResourceName()))
                return i;

        }
        //In case the List not contained the object, it is null.
        return -1;
    }

    public final int getResourceObjectIndexByName(String resourceName) {

        List<Resource> resourceList = getResourceList();

        for(int i = 0; i < resourceList.size(); i++){

            if(resourceList.get(i).getResourceName().equals(resourceName))
                return i;

        }
        //In case the List not contained the object, it is null.
        return -1;
    }

    public final void updateObject(int index, Resource resource){

        List<Resource> resourceList = getResourceList();

        if(index >= 0 && index < resourceList.size()){
            resourceList.set(index, resource);
            persistObject(resourceList);
        }

    }

    public final void deleteObject(Resource resource){

        List<Resource> resourceList = getResourceList();

        try {
            looper:
            for(int i = 0; i < resourceList.size(); i++){
                if(resourceList.get(i).getResourceName().equals(resource.getResourceName())){
                    resourceList.remove(i);
                    persistObject(resourceList);
                    break looper;
                }
            }
        } catch (NullPointerException npe){
            logger.error(npe);
        }

    }

    public final void deleteMultipleObject(List<Resource> deletingList){

        List<Resource> resourceList = getResourceList();

        try {

            Iterator<Resource> resourceIterator = resourceList.iterator();
            while(resourceIterator.hasNext()){
                Resource resourceObj = resourceIterator.next();

                Iterator<Resource> deletingIterator = deletingList.iterator();
                while(deletingIterator.hasNext()){
                    Resource deleteObj = deletingIterator.next();

                    if(deleteObj.getResourceName().equals(resourceObj.getResourceName())){
                        resourceIterator.remove();
                        deletingIterator.remove();
                    }
                }
            }

            persistObject(resourceList);

        } catch (NullPointerException npe){
            logger.error(npe);
        }

    }

}
