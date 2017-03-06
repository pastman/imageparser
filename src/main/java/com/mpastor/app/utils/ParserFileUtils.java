package com.mpastor.app.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParserFileUtils {
	
	private static Logger logger = LoggerFactory.getLogger(ParserFileUtils.class);

	private ParserFileUtils(){
		
	}
	
	
	/**
	 * Método que crea un directorio 'modified_img' a partir del path indicado
	 * @param imgPath
	 * @throws Exception
	 */
	public static void createImageDestinationFolder(String imgPath) throws Exception {
		
		File destFolder = new File(imgPath+Constants.DEST_FOLDER);
		
		if (!destFolder.exists()) {
			if (destFolder.mkdir()) {
				logger.info("Se ha creado correctamente el directorio destino donde se van a almacenar los ficheros de imágen modificados.");
			} else {
				logger.info("No se ha podido crear el directorio destino donde se van a almacenar los ficheros de imágen modificados.");
			}
		}
	}
	
	
	/**
	 * Método que copia un fichero de una ruta específica a un fichero con el mísmo nombre, extensión txt, en la ruta indicada por destPath
	 * @param imgFile
	 * @param destPath
	 * @throws Exception
	 */
	public static String copyFileToTXTtempFile(File originalFile, String destPath) throws Exception {
		
		String fileNameWithoutExtension = originalFile.getName();
		String txtFileCompletePath = destPath+"/"+fileNameWithoutExtension+"."+Constants.TXT_EXTENSION;
		File txtTempFile = new File(txtFileCompletePath);
		FileUtils.copyFile(originalFile, txtTempFile);
		logger.info("Se ha copiado el fichero {} al fichero {}", originalFile.getAbsolutePath(), txtFileCompletePath);
		return txtTempFile.getAbsolutePath();
	}
	
	
	/**
	 * Método que copia un fichero xmp de una ruta específica a un fichero con el mísmo nombre, extensión xml, en la mísma ruta que el fichero original
	 * @param xmpPath
	 * @param xmpFileName
	 * @throws Exception
	 */
	public static void copyXMPFileToXMLTempFile(String xmpPath, String xmpFileName) throws Exception {
		
		String xmpFileCompletePath = xmpPath+xmpFileName+"."+Constants.XMP_EXTENSION;
		String xmlFileCompletePath = xmpPath+xmpFileName+"."+Constants.XML_EXTENSION;
		File xmpFile = new File(xmpFileCompletePath);
		File xmlTempFile = new File(xmlFileCompletePath);
	
		FileUtils.copyFile(xmpFile, xmlTempFile);
		logger.info("Se ha copiado el fichero {} al fichero {}", xmpFileCompletePath, xmlFileCompletePath);
	}
	
	
	/**
	 * Método que elimina el fichero xml temporal creado a partir del fichero xmp
	 * @param path
	 * @param xmlTempFilePath
	 * @throws Exception
	 */
	public static void deleteXMLTempFile(String path, String xmlTempFilePath) throws Exception {
		
		String xmlFileCompletePath = path+xmlTempFilePath+"."+Constants.XML_EXTENSION;
		File xmlTempFile = new File(xmlFileCompletePath);
		
		xmlTempFile.delete();
		logger.info("Se ha eliminado el fichero temporal {}", xmlFileCompletePath);
	}
	
	
	/**
	 * Método que elimina la extensión txt a un fichero dado 
	 * @param filePath
	 * @throws Exception
	 */
	public static void renameFile(String txtFilePath) throws Exception {
		
		File txtFile = new File(txtFilePath);
		String fileNameWithoutTxtExtension = txtFile.getAbsolutePath().substring(0, txtFile.getAbsolutePath().length()-4);
		File imgFile = new File(fileNameWithoutTxtExtension);
		txtFile.renameTo(imgFile);
		logger.info("***** El fichero {} se ha renombrado a {}", txtFilePath, fileNameWithoutTxtExtension);
	}
	
}