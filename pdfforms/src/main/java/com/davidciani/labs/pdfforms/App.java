// App.java
// Copyright 2021 David Ciani. All Rights Reserved.
// Includes example code from Apache PDFBox. Copyright 2014 The Apache Software Foundation
// SPDX-License-Identifier: MIT AND Apache-2.0

package com.davidciani.labs.pdfforms;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;

import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.afm.CharMetric;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

/**
 * Hello world!
 *
 */
public class App {


    public static void addCheckboxField(PDDocument document, PDPage page, PDAcroForm acroForm, Field field) throws IOException {
        PDRectangle rect = new PDRectangle(field.getX(), field.getY(), field.getHeight(), field.getWidth());

        PDCheckBox checkbox = new PDCheckBox(acroForm);
        checkbox.setPartialName(field.getFieldName());
        PDAnnotationWidget widget = checkbox.getWidgets().get(0);
        widget.setPage(page);
        widget.setRectangle(rect);
        widget.setPrinted(true);

        PDAppearanceCharacteristicsDictionary appearanceCharacteristics = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
        appearanceCharacteristics.setBorderColour(new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE));
        appearanceCharacteristics.setBackground(new PDColor(new float[]{1, 1, 1}, PDDeviceRGB.INSTANCE));
        // 8 = cross; 4 = checkmark; H = star; u = diamond; n = square, l = dot
        appearanceCharacteristics.setNormalCaption("8");
        widget.setAppearanceCharacteristics(appearanceCharacteristics);

        PDBorderStyleDictionary borderStyleDictionary = new PDBorderStyleDictionary();
        borderStyleDictionary.setWidth(1);
        borderStyleDictionary.setStyle(PDBorderStyleDictionary.STYLE_SOLID);
        widget.setBorderStyle(borderStyleDictionary);

        PDAppearanceDictionary ap = new PDAppearanceDictionary();
        widget.setAppearance(ap);
        PDAppearanceEntry normalAppearance = ap.getNormalAppearance();

        COSDictionary normalAppearanceDict = (COSDictionary) normalAppearance.getCOSObject();
        normalAppearanceDict.setItem(COSName.Off, createAppearanceStream(document, widget, false));
        normalAppearanceDict.setItem(COSName.YES, createAppearanceStream(document, widget, true));

        // If we ever decide to implement a /D (down) appearance, just 
        // replace the background colors c with c * 0.75
        page.getAnnotations().add(checkbox.getWidgets().get(0));
        acroForm.getFields().add(checkbox);

        // always call check() or unCheck(), or the box will remain invisible.
        checkbox.unCheck();
    }

    public static void addTextField(PDPage page, PDAcroForm acroForm, Field field) throws IOException {

        // Add a form field to the form.
        PDTextField textBox = new PDTextField(acroForm);
        textBox.setPartialName(field.getFieldName());

        // Acrobat sets the font size to 12 as default
        // This is done by setting the font size to '12' on the
        // field level.
        // The text color is set to blue in this example.
        // To use black, replace "0 0 1 rg" with "0 0 0 rg" or "0 g".
        String defaultAppearanceString = "/Cour 8 Tf 0 0 0 rg";
        textBox.setDefaultAppearance(defaultAppearanceString);

        // add the field to the acroform
        acroForm.getFields().add(textBox);

        // Specify the widget annotation associated with the field
        PDAnnotationWidget widget = textBox.getWidgets().get(0);
        PDRectangle rect = new PDRectangle(field.getX(), field.getY(), field.getWidth(), field.getHeight());
        widget.setRectangle(rect);
        widget.setPage(page);

        // set green border and yellow background
        // if you prefer defaults, delete this code block
        PDAppearanceCharacteristicsDictionary fieldAppearance = new PDAppearanceCharacteristicsDictionary(
                new COSDictionary());
        widget.setAppearanceCharacteristics(fieldAppearance);

        // make sure the widget annotation is visible on screen and paper
        widget.setPrinted(true);

        // Add the widget annotation to the page
        page.getAnnotations().add(widget);

        // set the alignment ("quadding")
        textBox.setQ(PDVariableText.QUADDING_LEFT);

        // set the field value
        // textBox.setValue("Sample field content");

    }

    public static void main(String[] args) {
        System.out.println("Hello World!");

        File srcFile = new File("non-form.pdf");
        File dstFile = new File("form.pdf");

        try (PDDocument document = PDDocument.load(srcFile)) {
            PDPage page = document.getPage(0);

            // Adobe Acrobat uses Helvetica as a default font and
            // stores that under the name '/Helv' in the resources dictionary
            PDFont font = PDType1Font.COURIER;
            PDResources resources = new PDResources();
            resources.put(COSName.getPDFName("Cour"), font);

            // Add a new AcroForm and add that to the document
            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);

            // Add and set the resources and default appearance at the form level
            acroForm.setDefaultResources(resources);

            // Acrobat sets the font size on the form level to be
            // auto sized as default. This is done by setting the font size to '0'
            String defaultAppearanceString = "/Cour 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);

            List<Field> beans;
            try {
                beans = new CsvToBeanBuilder<Field>(new FileReader("form_fields.csv"))
                .withType(Field.class).withSkipLines(1).build().parse();

                for (Field field : beans) {
                    System.out.println(field.toString());
                    if ("text".equals(field.getFieldType())) {
                        addTextField(page, acroForm, field);
                    }
                    else if ("checkbox".equals(field.getFieldType())) {
                        addCheckboxField(document, page, acroForm, field);
                    }
                }

            } catch (IllegalStateException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            

            document.save(dstFile);
            document.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static PDAppearanceStream createAppearanceStream(
            final PDDocument document, PDAnnotationWidget widget, boolean on) throws IOException
    {
        PDRectangle rect = widget.getRectangle();
        PDAppearanceCharacteristicsDictionary appearanceCharacteristics;
        PDAppearanceStream yesAP = new PDAppearanceStream(document);
        yesAP.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
        yesAP.setResources(new PDResources());
        PDPageContentStream yesAPCS = new PDPageContentStream(document, yesAP);
        appearanceCharacteristics = widget.getAppearanceCharacteristics();
        PDColor backgroundColor = appearanceCharacteristics.getBackground();
        PDColor borderColor = appearanceCharacteristics.getBorderColour();
        float lineWidth = getLineWidth(widget);
        yesAPCS.setLineWidth(lineWidth); // border style (dash) ignored
        yesAPCS.setNonStrokingColor(backgroundColor);
        yesAPCS.addRect(0, 0, rect.getWidth(), rect.getHeight());
        yesAPCS.fill();
        yesAPCS.setStrokingColor(borderColor);
        yesAPCS.addRect(lineWidth / 2, lineWidth / 2, rect.getWidth() - lineWidth, rect.getHeight() - lineWidth);
        yesAPCS.stroke();
        if (!on)
        {
            yesAPCS.close();
            return yesAP;
        }

        yesAPCS.addRect(lineWidth, lineWidth, rect.getWidth() - lineWidth * 2, rect.getHeight() - lineWidth * 2);
        yesAPCS.clip();

        String normalCaption = appearanceCharacteristics.getNormalCaption();
        if (normalCaption == null)
        {
            normalCaption = "4"; // Adobe behaviour
        }
        if ("8".equals(normalCaption))
        {
            // Adobe paints a cross instead of using the Zapf Dingbats cross symbol
            yesAPCS.setStrokingColor(0f);
            yesAPCS.moveTo(lineWidth * 2, rect.getHeight() - lineWidth * 2);
            yesAPCS.lineTo(rect.getWidth() - lineWidth * 2, lineWidth * 2);
            yesAPCS.moveTo(rect.getWidth() - lineWidth * 2, rect.getHeight() - lineWidth * 2);
            yesAPCS.lineTo(lineWidth * 2, lineWidth * 2);
            yesAPCS.stroke();
        }
        else
        {
            Rectangle2D bounds = new Rectangle2D.Float();
            String unicode = null;

            // ZapfDingbats font may be missing or substituted, let's use AFM resources instead.
            AFMParser parser = new AFMParser(PDType1Font.class.getResourceAsStream(
                    "/org/apache/pdfbox/resources/afm/ZapfDingbats.afm"));
            FontMetrics metric = parser.parse();
            for (CharMetric cm : metric.getCharMetrics())
            {
                // The caption is not unicode, but the Zapf Dingbats code in the PDF.
                // Assume that only the first character is used.
                if (normalCaption.codePointAt(0) == cm.getCharacterCode())
                {
                    BoundingBox bb = cm.getBoundingBox();
                    bounds = new Rectangle2D.Float(bb.getLowerLeftX(), bb.getLowerLeftY(), 
                                                   bb.getWidth(), bb.getHeight());
                    unicode = PDType1Font.ZAPF_DINGBATS.getGlyphList().toUnicode(cm.getName());
                    break;
                }
            }
            if (bounds.isEmpty())
            {
                throw new IOException("Bounds rectangle for chosen glyph is empty");
            }
            float size = (float) Math.min(bounds.getWidth(), bounds.getHeight()) / 1000;
            // assume that checkmark has square size
            // the calculations approximate what Adobe is doing, i.e. put the glyph in the middle
            float fontSize = (rect.getWidth() - lineWidth * 2) / size * 0.6666f;
            float xOffset = (float) (rect.getWidth() - (bounds.getWidth()) / 1000 * fontSize) / 2;
            xOffset -= bounds.getX() / 1000 * fontSize;
            float yOffset = (float) (rect.getHeight() - (bounds.getHeight()) / 1000 * fontSize) / 2;
            yOffset -= bounds.getY() / 1000 * fontSize;
            yesAPCS.setNonStrokingColor(0f);
            yesAPCS.beginText();
            yesAPCS.setFont(PDType1Font.ZAPF_DINGBATS, fontSize);
            yesAPCS.newLineAtOffset(xOffset, yOffset);
            yesAPCS.showText(unicode);
            yesAPCS.endText();
        }
        yesAPCS.close();
        return yesAP;
    }

    static float getLineWidth(PDAnnotationWidget widget)
    {
        PDBorderStyleDictionary bs = widget.getBorderStyle();
        if (bs != null)
        {
            return bs.getWidth();
        }
        return 1;
    }
}
