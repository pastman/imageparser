package com.mpastor.app;

import java.io.File;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mpastor.app.utils.Constants;
import com.mpastor.app.utils.JPGUtils;
import com.mpastor.app.utils.ParserFileUtils;


public class Parser {

	static Logger logger = LoggerFactory.getLogger(Parser.class);
	
	public static void main(String[] args) {

		String imgPath = args[0];
		String xmpPath = args[1];
		
		try {
		
			Date beginDate = new Date();
			logger.info("********** Comienza el proceso de tratamiento de imágenes [@mpastor] **********");
			
			File imgPathFile = new File(imgPath);
			File[] listOfImgFiles = imgPathFile.listFiles();

			logger.info("*** Comienza el proceso de tratamiento de ficheros de imágen encontrados en la ruta : {}", imgPath);

			logger.info("Ruta donde se encuentran los ficheros de imágen : {}", imgPath);
			logger.info("Ruta donde se encuentran los ficheros xmp       : {}", xmpPath);
			
			// Se crea la carpeta donde se van a almacenar las imágenes modificadas : /imgPath/modified_img/
			ParserFileUtils.createImageDestinationFolder(imgPath);
			
			// Obtenemos una lista con los ficheros xmp encontrados en la ruta dada
			List<String> xmpFilesList = JPGUtils.getAllXMPFilesFromFolder(xmpPath);
			
			for (File file : listOfImgFiles) {
			
				if (file.isFile()) {
					
					if (file.getName().contains(Constants.JPG_EXTENSION) || file.getName().contains(Constants.EPS_EXTENSION)) {
					
						ImageParser imParser = new ImageParser(imgPath, xmpPath);
						
						imParser.addKeywordsToImageFile(file, xmpFilesList);
					}
				}
			}
			
			Date endDate = new Date();
			long secondsBetween = (endDate.getTime() - beginDate.getTime()) / 1000;
			logger.info("*** El proceso se ha completado en : "+secondsBetween + " segundos");
			logger.info("********** Fin del proceso **********");
			
		} catch (Exception e) {
			logger.error("Se ha producido un error : "+e.getMessage());
		}
	}

}