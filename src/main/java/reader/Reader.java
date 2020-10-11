package reader;


import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;


import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;




public class Reader {
    private boolean sortByPosition = true;

    //--------------------- Open CV
    //"D:\\Programme\\Tesseract-OCR\\tessdata";
    private static String TESS_DATA = System.getProperty("user.dir") + File.separator + "tessdata";

    // Create tess obj
    private static Tesseract tesseract = new Tesseract();

    // Load OPENCV and Tesseract
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        tesseract.setDatapath(TESS_DATA);
    }



    //------------- Private Methods
    private String extractTextFromImage(File image) {
        Mat origin = imread(image.getPath());
        String result = "";
        Mat gray = new Mat();

        // Convert to gray scale
        cvtColor(origin, gray, COLOR_BGR2GRAY);
        imwrite(image.getPath(), gray);

        //Set Language
        tesseract.setLanguage("deu");
        try {
            // Recognize text with OCR
            result = tesseract.doOCR(image);
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String readPDF(File pdfFile, boolean sortByPosition) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);


        PDDocument.load(pdfFile);
        //System.out.println("PDF loaded");


        //get contents of pdfFile
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setSortByPosition(sortByPosition);
        String text = pdfStripper.getText(document);

        //Closing the document
        document.close();

        return text;
    }

    private String readFile(File file) throws IOException{
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String readImage(File image){
        return extractTextFromImage(image);
    }


    //------------- Public Methods
    public String getFileType(File file){
        final String path = file.getPath();
        final List<String> split = Arrays.asList(path.split("\\."));
        final String suffix = split.get(split.size()-1);

        return suffix;
    }

    public String read(File file) throws IOException {
        final String type = getFileType(file);

        switch (type){
            case "jpg":
            case "png":
            case "tiff":
            case "bmp":
                return readImage(file);
            case "pdf":
                return readPDF(file, sortByPosition);
            case "txt":
                return readFile(file);
            default:
                return "File is not of a legal type. [" + type + "]";
        }
    }


    //------------- Getter And Setter
    public boolean isSortByPosition() {
        return sortByPosition;
    }

    public void setSortByPosition(boolean sortByPosition) {
        this.sortByPosition = sortByPosition;
    }

}