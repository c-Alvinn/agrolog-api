package br.com.agrologqueue.api.utils;

import br.com.agrologqueue.api.model.entity.Schedule;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportGenerator {

    public static void generateSchedulesPdf(HttpServletResponse response,
                                            List<Schedule> schedules,
                                            String companyName,
                                            String periodLabel) throws Exception {

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        String titleText = String.format("Relatório de Atendimentos - %s\nPeríodo: %s",
                companyName, periodLabel);

        Paragraph title = new Paragraph(titleText, fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1.5f, 2, 2, 1.5f, 2, 2.5f, 2.5f});

        String[] headers = {"Motorista", "Placa", "Caminhão", "Grão", "Operação", "Status", "Chamado em", "Liberado em"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            cell.setBackgroundColor(new Color(230, 230, 230));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Schedule s : schedules) {
            table.addCell(new Phrase(s.getDriver().getName(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getLicensePlate(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getTruckType().toString(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getGrainType().toString(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getOperationType().name().equals("LOAD") ? "Carga" : "Descarga", FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(translateStatus(s.getQueueStatus().name()), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getCalledAt() != null ? s.getCalledAt().format(formatter) : "-", FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getReleasedAt() != null ? s.getReleasedAt().format(formatter) : "-", FontFactory.getFont(FontFactory.HELVETICA, 9)));
        }

        document.add(table);
        document.close();
    }

    private static String translateStatus(String status) {
        return switch (status) {
            case "IN_SERVICE" -> "Em Atendimento";
            case "COMPLETED" -> "Finalizado";
            default -> status;
        };
    }
}