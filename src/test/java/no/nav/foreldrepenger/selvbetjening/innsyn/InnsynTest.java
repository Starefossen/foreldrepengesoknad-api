package no.nav.foreldrepenger.selvbetjening.innsyn;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.selvbetjening.config.TokenUtilConfiguration;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TokenUtilConfiguration.class, SpringTokenValidationContextHolder.class })
@RestClientTest
@ActiveProfiles("test")
class InnsynTest {
    private static final InnsynConfig CFG = innsynCfg();

    private static InnsynConfig innsynCfg() {
        return new InnsynConfig(URI.create("http://www.mottak.no"), true);
    }

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private RestTemplateBuilder builder;

    private InnsynTjeneste innsyn;

    @BeforeEach
    public void init() {
        innsyn = new InnsynTjeneste(new InnsynConnection(builder
                .build(), CFG));
    }

    @Test
    void uttaksplan() {
        var saksnummer = Saksnummer.valueOf("42");
        server.expect(ExpectedCount.once(), requestTo(CFG.uttakURI(saksnummer)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));
        innsyn.hentUttaksplan(saksnummer);
        server.verify();
    }

}
