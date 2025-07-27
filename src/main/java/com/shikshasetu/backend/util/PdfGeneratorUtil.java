package com.shikshasetu.backend.util;

import com.shikshasetu.backend.model.Certificate;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;

public class PdfGeneratorUtil {

    public static ByteArrayOutputStream generateCertificatePDF(Certificate cert) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("🎓 ShikshaSetu Certificate of Completion")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
        );

        document.add(new Paragraph("\nThis is to certify that")
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(cert.getUser().getName())
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("has successfully completed the course:")
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(cert.getCourse().getTitle())
                .setItalic()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\nIssued On: " + cert.getIssuedDate()));
        document.add(new Paragraph("Certificate Code: " + cert.getCertificateCode()));

        document.close();
        return out;
    }
}
