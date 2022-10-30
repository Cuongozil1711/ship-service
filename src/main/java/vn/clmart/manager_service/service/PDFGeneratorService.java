package vn.clmart.manager_service.service;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.config.font.FontFactory;
import vn.clmart.manager_service.dto.DetailsItemOrderDto;
import vn.clmart.manager_service.dto.ExportWareHouseListDto;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

@Service
public class PDFGeneratorService {

    @Autowired
    ExportWareHouseService exportWareHouseService;

    @Autowired
    WareHouseService wareHouseService;

    @Autowired
    CompanyService companyService;

    @Autowired
    ItemsService itemsService;

    public void exportWareHouse(HttpServletResponse response, Long idReceiptExport) throws Exception {
        ExportWareHouseListDto exportWareHouseListDto = exportWareHouseService.getByIdReceiptExport(69l, "", idReceiptExport);
        Document document = new Document(PageSize.A4);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OutputStream outputStream = response.getOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTitle.setSize(18);

        Font myfont = FontFactory.getFont("Tahoma", 18, Font.BOLD,Color.BLACK);
        Paragraph paragraph = new Paragraph(new
                String(("Phieu xuat kho").getBytes(StandardCharsets.UTF_8)), myfont);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontParagraph.setSize(12);

        Font fontTitleName = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTitle.setSize(16);
        Paragraph paragraphName = new Paragraph("Kho hàng " + wareHouseService.getById(69l,exportWareHouseListDto.getReceiptExportWareHouseDto().getIdWareHouse()).getName(), fontTitleName);
        paragraphName.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraph2 = new Paragraph("Tên chi nhánh: " + companyService.getById(69l, "", 69l).getName());
        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph3 = new Paragraph("Mã phiếu xuất: " + exportWareHouseListDto.getReceiptExportWareHouseDto().getCode());
        paragraph3.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph4 = new Paragraph("Tên phiếu xuất: " + exportWareHouseListDto.getReceiptExportWareHouseDto().getName());
        paragraph4.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph5 = new Paragraph("Kho xuất tới: " +  wareHouseService.getById(137l,exportWareHouseListDto.getReceiptExportWareHouseDto().getIdWareHouseTo()).getName());
        paragraph5.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph6 = new Paragraph("Chữ ký người xuất kho");
        paragraph6.setAlignment(Paragraph.ALIGN_RIGHT);

        document.add(paragraph);
        document.add(paragraphName);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph4);
        document.add(paragraph5);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table, exportWareHouseListDto.getData());


        File outputfile = new File("C:\\Users\\cuong\\Downloads\\image");
        ImageIO.write(generateEAN13BarcodeImage(idReceiptExport.toString()), "jpg", outputfile);
        Image image = Image.getInstance(outputfile.getAbsolutePath());
        Paragraph paragraph7 = new Paragraph();
        paragraph7.setSpacingBefore(10f);
        paragraph7.setSpacingAfter(10f);
        paragraph7.add(new Chunk("Mã phiếu"));
        paragraph7.add (new Chunk(image, 0, 0, true));;
        paragraph7.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(paragraph7);
        document.add(table);
        document.add(paragraph6);
        document.close();
    }

    private void writeTableData(PdfPTable table, List<DetailsItemOrderDto> dtoList) {
        int i=1;
        for (DetailsItemOrderDto itemOrderDto : dtoList) {
            table.addCell(String.valueOf(i));
            i++;
            table.addCell(itemsService.getById(137l, "", itemOrderDto.getIdItems()).getName());
            table.addCell(itemOrderDto.getDvtCode());
            table.addCell("165000");
            table.addCell(String.valueOf(itemOrderDto.getQuality()));
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("STT", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Tên sản phẩm", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Đơn vị tính", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Giá", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Số lượng", font));
        table.addCell(cell);
    }

    public  BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
        Barcode barcode = BarcodeFactory.createCode128(barcodeText);
        return BarcodeImageHandler.getImage(barcode);
    }
}
