package no.nav.foreldrepenger.selvbetjening.innsending.pdf;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.selvbetjening.http.AbstractConfig;

@ConfigurationProperties(prefix = "fppdfgen")
public class PdfGeneratorConfig extends AbstractConfig {

    private static final String DEFAULT_FPPDFGEN_URI = "http://fppdfgen.default.svc.nais.local";
    private static final String TILBAKEBETALING_UTTALELSE = "/api/v1/genpdf/tilbakebetaling/uttalelse";
    private static final String PING = "/is_alive";

    @ConstructorBinding
    public PdfGeneratorConfig(@DefaultValue(DEFAULT_FPPDFGEN_URI) URI uri, @DefaultValue("true") boolean enabled) {
        super(uri, enabled);
    }

    @Override
    protected URI pingURI() {
        return uri(getUri(), PING);
    }

    URI tilbakebetalingURI() {
        return uri(getUri(), TILBAKEBETALING_UTTALELSE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[pingURI=" + pingURI() + ", tilbakebetalingURI="
                + tilbakebetalingURI() + "]";
    }
}
