package com.mpastor.app;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpastor.app.utils.Constants;
import com.mpastor.app.utils.JPGUtils;


public class EPSImageFile {

	static Logger logger = LoggerFactory.getLogger(EPSImageFile.class);
	
	public EPSImageFile(){
		
	}
	
	public void insertKeywordsIntoFile(String txtTempFilePath, String keywords, List<String> metadataList) throws Exception {
		
		String metadata = "";
		String newMetadata = ""; 
		Path path = Paths.get(txtTempFilePath);
		Charset charset = StandardCharsets.ISO_8859_1;
		String content = new String(Files.readAllBytes(path), charset);
		
		if (content.indexOf(Constants.INIT_SUBJECT_TAG) != -1 && content.indexOf(Constants.END_SUBJECT_TAG) != -1) {
			
			// El fichero tiene Keywords :
			logger.info("*** El fichero original es un EPS y tiene Keywords...");
			List<String> existingKeywordsFromImageFile = JPGUtils.getExistingKeywordsFromTXTFile(txtTempFilePath);
			List<String> notCommonKeywords = getNotCommonKeywords(existingKeywordsFromImageFile, metadataList);
			newMetadata = generateKeywordsFormatToEPSWithKeywordsFile(notCommonKeywords);
			content = content.replaceAll(Constants.END_BAG_TAG, newMetadata);
		
		} else {
			
			// El fichero no tiene Keywords :
			logger.info("*** El fichero no tiene Keywords...");
			metadata = content.substring(content.indexOf(Constants.INIT_DESCRIPTION_TAG), content.indexOf(Constants.END_DESCRIPTION_TAG)+17);
			newMetadata = metadata + Constants.BLANK + keywords;
			content = content.replaceAll(metadata, newMetadata);
		}
		
		Files.write(path, content.getBytes(charset));
		logger.info("*** Las Keywords se han insertado con éxito.");
	}
	
	
	private static String generateKeywordsFormatToEPSWithKeywordsFile(List<String> keywordsToInclude) throws Exception {
		
		String keywords = "";
		if (keywordsToInclude != null) {
			for (String keyword : keywordsToInclude) {
				keywords += "     " + Constants.INIT_LI_TAG + keyword + Constants.END_LI_TAG + "\n"; 
			}
			keywords += "    " + Constants.END_BAG_TAG;
		}
		logger.info("*** Cadena con los Keywords a incrustar en el fichero de imágen EPS con Keywords existentes : {}", keywords.substring(4, keywords.length()));
		return keywords.substring(4, keywords.length());
	}

	
	private static List<String> getNotCommonKeywords(List<String> keywordsFromImageFile, List<String> keywordsFromXMPFiles) throws Exception {
		
		List<String> notCommonKeywordsLst = new ArrayList<String>();
		for (String keywordFromXMP : keywordsFromXMPFiles) {
			if (!keywordsFromImageFile.contains(keywordFromXMP)){
				notCommonKeywordsLst.add(keywordFromXMP);	
			}
		}
		logger.info("*** Lista de keywords no comunes : "+notCommonKeywordsLst);
		return notCommonKeywordsLst;
	}
	
}