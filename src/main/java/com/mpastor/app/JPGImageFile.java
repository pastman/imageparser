package com.mpastor.app;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpastor.app.utils.Constants;


public class JPGImageFile {

	static Logger logger = LoggerFactory.getLogger(JPGImageFile.class);
	
	public JPGImageFile(){
		
	}
	
	public void insertKeywordsIntoJPGFile(String txtTempFilePath, String keywords) throws Exception {
		
		String metadata = "";
		String newMetadata = ""; 
		Path path = Paths.get(txtTempFilePath);
		Charset charset = StandardCharsets.ISO_8859_1;
		String content = new String(Files.readAllBytes(path), charset);
		
		if (content.indexOf(Constants.INIT_SUBJECT_TAG) != -1 && content.indexOf(Constants.END_SUBJECT_TAG) != -1) {
			
			// El fichero ya tiene algún Keyword :
			logger.info("*** El fichero ya tiene Keywords...");
			metadata = content.substring(content.indexOf(Constants.INIT_SUBJECT_TAG), content.indexOf(Constants.END_SUBJECT_TAG)+13);
			logger.info("*** Lista de Keywords encontrados en el fichero : "+metadata);
			
			logger.info("*** El fichero original es un JPG y tiene Keywords...");
			logger.info("*** Lista de Keywords que se deben insertar en el fichero : "+keywords);
			content = content.replaceAll(metadata, keywords);
		
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
	
}