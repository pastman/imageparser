package com.mpastor.app.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JPGUtils {
	
	private static Logger logger = LoggerFactory.getLogger(JPGUtils.class);
	
	private JPGUtils(){
		
	}

	
	/**
	 * Método que devuelve una lista con todas las descripciones encontradas en el fichero temporal txt
	 * @param txtFile
	 * @return
	 * @throws Exception
	 */
	public static List<String> getDescriptionsFromTXTfile(String txtFile) throws Exception {
		
		List<String> descriptions = new ArrayList<String>();
		String metadata = "";
		
		Path path = Paths.get(txtFile);
		Charset charset = StandardCharsets.ISO_8859_1;
		String content = new String(Files.readAllBytes(path), charset);
		metadata = content.substring(content.indexOf(Constants.INIT_DESCRIPTION_TAG), content.indexOf(Constants.END_DESCRIPTION_TAG));
		//example : <dc:description> <rdf:Alt> <rdf:li xml:lang="x-default">Illustration of a isolated smart watch icon with a whale</rdf:li> </rdf:Alt>
		
		int beginIndex = metadata.indexOf(Constants.BEGIN_DESC_INDEX)+29;
		int endIndex = metadata.indexOf(Constants.END_LI_TAG);
		metadata = metadata.substring(beginIndex, endIndex);
		logger.info("*** Description encontrada en el fichero : "+metadata);
		
		String[] descArray = metadata.split(" ");
		Collections.addAll(descriptions, descArray); 
		
		return descriptions;
	}
	
	
	/**
	 * Método que almacena en una lista el nombre de todos los ficheros .xmp que se encuentran en la carpeta indicada
	 * @param xmpsPath
	 * @return
	 * @throws Exception
	 */
	public static List<String> getAllXMPFilesFromFolder(String xmpsPath) throws Exception {
		
		List<String> allXmpsFromFolder = new ArrayList<String>();
		
		File xmpFolder = new File(xmpsPath);
		File[] listOfFiles = xmpFolder.listFiles();
		
		logger.info("*** Lista de ficheros xmp encontrados en la ruta : {}", xmpsPath);
		
		for (File file : listOfFiles) {
			
			if (file.isFile()) {
				
				if (file.getName().contains(Constants.XMP_EXTENSION)) {
					logger.info(file.getName());
					String xmpWithoutExtension = file.getName().substring(0, file.getName().indexOf(Constants.XMP_EXTENSION)-1);
					allXmpsFromFolder.add(xmpWithoutExtension);
				}
		    }
		}
		
		return allXmpsFromFolder; 
	}
	
	
	/**
	 * Método que devuelve una lista con los nombres de las keywords del fichero de imágen encontradas en la carpeta de ficheros xmp 
	 * @param descriptionKeysFromImgLst
	 * @param xmpFilesFromFolder
	 * @return
	 * @throws Exception
	 */
	public static List<String> getXMPLstToApply(List<String> descriptionKeysFromImgLst, List<String> xmpFilesFromFolder) throws Exception {
		
		// Both list to lower case! :
		List<String> xmpList = new ArrayList<String>();
		descriptionKeysFromImgLst = toLowerCaseLst(descriptionKeysFromImgLst);
		xmpFilesFromFolder = toLowerCaseLst(xmpFilesFromFolder); 
		
		for (String xmpFromFile : descriptionKeysFromImgLst) {
			
			for (String xmpFromFolder : xmpFilesFromFolder) {
				
				if (xmpFromFolder.contains("-") || xmpFromFolder.contains("_")) {
					
					String[] multiXmpValueArray = new String[0];
					
					if (xmpFromFolder.contains("-")) {
						multiXmpValueArray = xmpFromFolder.split("-");
					} else {
						multiXmpValueArray = xmpFromFolder.split("_");
					}
					
					for (String multixmlValue : multiXmpValueArray) {
						if (xmpFromFile.equalsIgnoreCase(multixmlValue)){
							if (!xmpList.contains(xmpFromFolder)) {
								xmpList.add(xmpFromFolder);
								break;
							}	
						}
					}
				}
				
				if (xmpFromFile.equalsIgnoreCase(xmpFromFolder)) {
					if (!xmpList.contains(xmpFromFolder)) {
						xmpList.add(xmpFromFolder);
						break;
					}
				}
			}
		}
		
		logger.info("*** Lista de xmp's que se van a aplicar sobre el fichero de imágen : {}", xmpList);
		return xmpList;
	}
	
	
	/**
	 * Método que devuelve una lista con todos los elementos de la lista inicial en letra minúscula 
	 * @param stringLst
	 * @return
	 * @throws Exception
	 */
	private static List<String> toLowerCaseLst(List<String> stringLst) throws Exception {
		
		List<String> lowerCaseStrLst = new ArrayList<String>();
		
		if (stringLst != null && stringLst.size() > 0) {
			for (String str : stringLst) {
				lowerCaseStrLst.add(str.toLowerCase());
			}
		}
		
		return lowerCaseStrLst; 
	}
	
	
	/**
	 * Método que añade a una lista de xmps los xmp que no contenga ya de otra lista de xmps
	 * @param allMetadataList
	 * @param metadataFromXmpFileList
	 * @return
	 * @throws Exception
	 */
	public static List<String> addMetadataToList(List<String> allMetadataList, List<String> metadataFromXmpFileList) throws Exception {
		
		if (metadataFromXmpFileList != null && metadataFromXmpFileList.size() > 0) {
			
			for (String metadata : metadataFromXmpFileList) {
				
				if (!allMetadataList.contains(metadata)) {
					allMetadataList.add(metadata);	
				}
			}
		}
		
		return allMetadataList;
	}
	
	
	/**
	 * Método que extrae las keywords, si es que tiene, de un fichero txt
	 * @param txtFilePath
	 * @return
	 * @throws Exception
	 */
	public static List<String> getExistingKeywordsFromTXTFile(String txtFilePath) throws Exception {
		
		List<String> existingDescriptionsLst = new ArrayList<String>();
		String keywords = "";
		Path path = Paths.get(txtFilePath);
		Charset charset = StandardCharsets.ISO_8859_1;
		String content = new String(Files.readAllBytes(path), charset);
		if (content.indexOf(Constants.INIT_BAG_TAG) != -1 && content.indexOf(Constants.END_BAG_TAG) != -1) {
			keywords = content.substring(content.indexOf(Constants.INIT_BAG_TAG)+9, content.indexOf(Constants.END_BAG_TAG));
			keywords = keywords.replaceAll("\n", ""); 
			keywords = keywords.replace(Constants.INIT_LI_TAG, "");
			keywords = keywords.replace(Constants.END_LI_TAG, ":");
			keywords = keywords.trim();
			keywords = keywords.substring(0, keywords.length()-1);
			String[] keywordsWithoutTags = keywords.split(":");
			for (String keyword : keywordsWithoutTags) {
				if (!existingDescriptionsLst.contains(keyword)) {
					existingDescriptionsLst.add(keyword.trim());	
				}
			}
		}
		
		return existingDescriptionsLst;
	}
	
	/**
	 * Método que a partir de una lista de Keywords genera la cadena necesaria para incrustar en el fichero de imágen
	 * @param keywordsToInclude
	 * @return 
	 * @throws Exception
	 */
	public static String generateKeywordsWithTags(List<String> keywordsToInclude) throws Exception {
		
		String result = Constants.INIT_SUBJECT_TAG + Constants.BLANK + Constants.INIT_BAG_TAG;
		String metadataLst = "";
		
		if (keywordsToInclude != null) {
			
			for (String metadata : keywordsToInclude) {
				
				metadataLst += Constants.BLANK + Constants.INIT_LI_TAG + metadata + Constants.END_LI_TAG;	
			}
		}
		
		result += metadataLst + Constants.BLANK + Constants.END_BAG_TAG + Constants.BLANK + Constants.END_SUBJECT_TAG;
		logger.info("*** Cadena con los keywords a incrustar en el fichero de imágen : {}", result);
		
		return result;
	}
	
}