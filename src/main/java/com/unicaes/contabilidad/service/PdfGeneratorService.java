package com.unicaes.contabilidad.service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.unicaes.contabilidad.model.Cuenta;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    private static final DeviceRgb UNICAES_RED = new DeviceRgb(125, 60, 60);
    private static final DeviceRgb UNICAES_GOLD = new DeviceRgb(212, 175, 55);
    private static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(240, 240, 240);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MONEY_FORMAT = NumberFormat.getCurrencyInstance(Locale.of("es", "US"));

    public byte[] generateBalanceGeneral(List<Cuenta> activos, List<Cuenta> pasivos,
                                          List<Cuenta> patrimonio, BigDecimal totalActivos,
                                          BigDecimal totalPasivos, BigDecimal totalPatrimonio,
                                          LocalDate fechaCorte) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        Paragraph title = new Paragraph("BALANCE GENERAL - UNICAES")
                .setFontSize(18)
                .setBold()
                .setFontColor(UNICAES_RED)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Al " + fechaCorte.format(DATE_FORMATTER))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(subtitle);
        document.add(new Paragraph("\n"));

        // ACTIVOS
        addSection(document, "ACTIVOS", activos, totalActivos);
        document.add(new Paragraph("\n"));

        // PASIVOS
        addSection(document, "PASIVOS", pasivos, totalPasivos);
        document.add(new Paragraph("\n"));

        // PATRIMONIO
        addSection(document, "PATRIMONIO", patrimonio, totalPatrimonio);

        document.close();
        return baos.toByteArray();
    }

    public byte[] generateEstadoResultados(List<Cuenta> ingresos, List<Cuenta> gastos,
                                            BigDecimal totalIngresos, BigDecimal totalGastos,
                                            BigDecimal utilidadNeta, LocalDate fechaInicio,
                                            LocalDate fechaFin) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        Paragraph title = new Paragraph("ESTADO DE RESULTADOS - UNICAES")
                .setFontSize(18)
                .setBold()
                .setFontColor(UNICAES_RED)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Del " + fechaInicio.format(DATE_FORMATTER) + 
                                           " al " + fechaFin.format(DATE_FORMATTER))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(subtitle);
        document.add(new Paragraph("\n"));

        // INGRESOS
        addSection(document, "INGRESOS", ingresos, totalIngresos);
        document.add(new Paragraph("\n"));

        // GASTOS
        addSection(document, "GASTOS", gastos, totalGastos);
        document.add(new Paragraph("\n"));

        // UTILIDAD NETA
        Table utilidadTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .setWidth(UnitValue.createPercentValue(100));
        
        utilidadTable.addCell(new Cell().add(new Paragraph("UTILIDAD NETA:").setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));
        utilidadTable.addCell(new Cell().add(new Paragraph(formatMoney(utilidadNeta)).setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));
        
        document.add(utilidadTable);
        document.close();
        return baos.toByteArray();
    }

    public byte[] generateBalanceComprobacion(List<Cuenta> cuentas, BigDecimal totalDebitos,
                                               BigDecimal totalCreditos, LocalDate fechaCorte) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        Paragraph title = new Paragraph("BALANCE DE COMPROBACIÓN - UNICAES")
                .setFontSize(18)
                .setBold()
                .setFontColor(UNICAES_RED)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Al " + fechaCorte.format(DATE_FORMATTER))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(subtitle);
        document.add(new Paragraph("\n"));

        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(createHeaderCell("Código"));
        table.addHeaderCell(createHeaderCell("Cuenta"));
        table.addHeaderCell(createHeaderCell("Debe"));
        table.addHeaderCell(createHeaderCell("Haber"));

        // Datos
        for (Cuenta cuenta : cuentas) {
            table.addCell(new Cell().add(new Paragraph(cuenta.getCodigo())));
            table.addCell(new Cell().add(new Paragraph(cuenta.getNombre())));

            if (cuenta.getNaturaleza().toString().equals("DEUDORA") && cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                table.addCell(new Cell().add(new Paragraph(formatMoney(cuenta.getSaldo())))
                        .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph("-"))
                        .setTextAlignment(TextAlignment.CENTER));
            } else if (cuenta.getNaturaleza().toString().equals("ACREEDORA") && cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                table.addCell(new Cell().add(new Paragraph("-"))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(formatMoney(cuenta.getSaldo())))
                        .setTextAlignment(TextAlignment.RIGHT));
            } else {
                table.addCell(new Cell().add(new Paragraph("-"))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph("-"))
                        .setTextAlignment(TextAlignment.CENTER));
            }
        }

        // Totales
        Cell totalLabelCell = new Cell(1, 2)
                .add(new Paragraph("TOTALES:").setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalLabelCell);
        
        table.addCell(new Cell().add(new Paragraph(formatMoney(totalDebitos)).setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(formatMoney(totalCreditos)).setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    private void addSection(Document document, String sectionName, List<Cuenta> cuentas, BigDecimal total) {
        // Título de sección
        Paragraph sectionTitle = new Paragraph(sectionName)
                .setFontSize(14)
                .setBold()
                .setFontColor(UNICAES_GOLD);
        document.add(sectionTitle);

        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(createHeaderCell("Código"));
        table.addHeaderCell(createHeaderCell("Cuenta"));
        table.addHeaderCell(createHeaderCell("Saldo"));

        // Datos
        for (Cuenta cuenta : cuentas) {
            table.addCell(new Cell().add(new Paragraph(cuenta.getCodigo())));
            table.addCell(new Cell().add(new Paragraph(cuenta.getNombre())));
            table.addCell(new Cell().add(new Paragraph(formatMoney(cuenta.getSaldo())))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        // Total
        Cell totalLabelCell = new Cell(1, 2)
                .add(new Paragraph("TOTAL:").setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalLabelCell);
        
        table.addCell(new Cell().add(new Paragraph(formatMoney(total)).setBold())
                .setBackgroundColor(LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(table);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontColor(WHITE))
                .setBackgroundColor(UNICAES_RED)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private String formatMoney(BigDecimal amount) {
        return MONEY_FORMAT.format(amount);
    }
}
