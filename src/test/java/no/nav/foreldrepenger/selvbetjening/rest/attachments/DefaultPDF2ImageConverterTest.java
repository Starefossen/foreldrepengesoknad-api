package no.nav.foreldrepenger.selvbetjening.rest.attachments;

import no.nav.foreldrepenger.selvbetjening.SlowTests;
import org.junit.Test;

import no.nav.foreldrepenger.selvbetjening.rest.attachments.DefaultPDF2ImageConverter;
import no.nav.foreldrepenger.selvbetjening.rest.attachments.PDFPageSplitter;
import no.nav.foreldrepenger.selvbetjening.rest.attachments.PDF2ImageConverter;
import org.junit.experimental.categories.Category;

import javax.imageio.ImageIO;
import java.util.List;

import static org.junit.Assert.*;

@Category(SlowTests.class)
public class DefaultPDF2ImageConverterTest {

    @Test
    public void verifyConversion() {
        ImageIO.setUseCache(false);
        PDFPageSplitter splitter = new PDFPageSplitter();
        PDF2ImageConverter converter = new DefaultPDF2ImageConverter();
        List<byte[]> pages = splitter.split("/pdf/test123.pdf");
        List<byte[]> jpgs = converter.convertToImages(pages);
        jpgs.forEach(jpg-> assertTrue(hasJpgSignature(jpg)));
    }

    private boolean hasJpgSignature(byte[] bytes) {
        return bytes[0] == (byte) 0xFF &&
                bytes[1] == (byte) 0xD8 &&
                bytes[2] == (byte) 0xFF;
    }

}
