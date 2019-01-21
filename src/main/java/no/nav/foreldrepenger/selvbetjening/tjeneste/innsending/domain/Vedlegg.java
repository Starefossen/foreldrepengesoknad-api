package no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain;

import java.net.URI;

public class Vedlegg {

    public String id;
    public String skjemanummer;
    public URI url;
    public byte[] content;
    public String innsendingsType;

    public Vedlegg kopi() {
        Vedlegg kopi = new Vedlegg();
        kopi.id = this.id;
        kopi.skjemanummer = this.skjemanummer;
        kopi.url = this.url;
        kopi.innsendingsType = this.innsendingsType;

        return kopi;
    }
}
