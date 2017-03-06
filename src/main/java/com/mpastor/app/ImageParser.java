package com.mpastor.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mpastor.app.utils.Constants;
import com.mpastor.app.utils.JPGUtils;
import com.mpastor.app.utils.ParserFileUtils;
import com.mpastor.app.utils.XMLUtils;


public class ImageParser {

	private String imageFilesPath;
	private String xmpFilesPath;

	static Logger logger = LoggerFactory.getLogger(ImageParser.class);
	
	public ImageParser(String imageFilesPath, String xmpFilesPath){
		this.imageFilesPath = imageFilesPath;
		this.xmpFilesPath = xmpFilesPath;
	}
	
	public void addKeywordsToImageFile(File imageFile, List<String> xmpFilesList) throws Exception {
		
		boolean isEPSFile = imageFile.getName().contains(Constants.EPS_EXTENSION);
		
		// Copiamos el fichero de imágen .jpg o .eps a la ruta /imgPath/modified_img/ con extensión .txt
		String txtTempFilePath = ParserFileUtils.copyFileToTXTtempFile(imageFile, imageFilesPath + Constants.DEST_FOLDER);
		
		// Obtenemos una lista con las descripciones encontradas en el fichero txt temporal
		List<String> descriptions = JPGUtils.getDescriptionsFromTXTfile(txtTempFilePath);
		
		// Obtenemos una lista con los XMP que se deben aplicar al fichero de imágen
		List<String> xmpFilesListToApply = JPGUtils.getXMPLstToApply(descriptions, xmpFilesList);
		
		if (xmpFilesListToApply != null && xmpFilesListToApply.size() > 0) {
			
			List<String> metadataList = new ArrayList<String>();
			
			for (String xmp : xmpFilesListToApply) {
				
				logger.info("*** Se va a aplicar el xmp : {}", xmp);
				
				ParserFileUtils.copyXMPFileToXMLTempFile(xmpFilesPath, xmp);
				
				List<String> metadataLstFromXmlFile = XMLUtils.extractMetadataFromXML(xmpFilesPath + xmp + ".xml");
				logger.info("*** Keywords del xml : {}", metadataLstFromXmlFile);
				ParserFileUtils.deleteXMLTempFile(xmpFilesPath, xmp);
				
				JPGUtils.addMetadataToList(metadataList, metadataLstFromXmlFile);
			}
			
			logger.info("*** Lista total de Keywords que se van a aplicar al fichero de imágen : {}", metadataList);
			logger.info("*** Tamaño de la lista : {}", metadataList.size());
			
			// Comprobamos si el fichero de imágen ya tiene keywords
			List<String> keywordsFromFileList = JPGUtils.getExistingKeywordsFromTXTFile(txtTempFilePath);
			logger.info("*** Lista de Keywords encontradas en el fichero de imágen original : {}", keywordsFromFileList);
			logger.info("*** Tamaño de la lista : {}", keywordsFromFileList.size());
			
			// Añade a la lista de Keywords del fichero de imágen las que no existan de la lista de Keywords generada a través de los xmps
			for (String xmpDescTag : metadataList) {
				if (!keywordsFromFileList.contains(xmpDescTag)) {
					keywordsFromFileList.add(xmpDescTag);	
				}
			}
			
			logger.info("*** Lista final de Keywords que debe llevar el fichero de imágen : {}", keywordsFromFileList);
			logger.info("*** Tamaño de la lista : {}", keywordsFromFileList.size());
			
			// Generamos la cadena entre tags que vamos a insertar en el fichero txt
			String keywordsWithTags = JPGUtils.generateKeywordsWithTags(keywordsFromFileList);
			
			if (isEPSFile) {
				
				// es un fichero eps :
				EPSImageFile epsFile = new EPSImageFile();
				epsFile.insertKeywordsIntoFile(txtTempFilePath, keywordsWithTags, metadataList);
			
			} else {
				
				// es un fichero jpg :
				JPGImageFile jpgFile = new JPGImageFile();
				jpgFile.insertKeywordsIntoJPGFile(txtTempFilePath, keywordsWithTags);
			}
			
			// Quitamos la extensión txt al fichero temporal para dejarlo como el original (jpg o eps)
			ParserFileUtils.renameFile(txtTempFilePath);
		}
	}
	
}