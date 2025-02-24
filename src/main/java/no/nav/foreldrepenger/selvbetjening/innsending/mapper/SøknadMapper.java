package no.nav.foreldrepenger.selvbetjening.innsending.mapper;

import static no.nav.foreldrepenger.selvbetjening.innsending.mapper.EngangsstønadMapper.tilEngangsstønad;
import static no.nav.foreldrepenger.selvbetjening.innsending.mapper.ForeldrepengerMapper.tilForeldrepengesøknad;
import static no.nav.foreldrepenger.selvbetjening.innsending.mapper.SvangerskapspengerMapper.tilSvangerskapspengesøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.selvbetjening.innsending.domain.EngangsstønadFrontend;
import no.nav.foreldrepenger.selvbetjening.innsending.domain.ForeldrepengesøknadFrontend;
import no.nav.foreldrepenger.selvbetjening.innsending.domain.SvangerskapspengesøknadFrontend;
import no.nav.foreldrepenger.selvbetjening.innsending.domain.SøknadFrontend;

public final class SøknadMapper {

    private SøknadMapper() {
    }

    public static no.nav.foreldrepenger.common.domain.Søknad tilSøknad(SøknadFrontend søknadFrontend) {
        var søknad = switch (søknadFrontend) {
            case EngangsstønadFrontend e -> tilEngangsstønad(e);
            case ForeldrepengesøknadFrontend f -> tilForeldrepengesøknad(f);
            case SvangerskapspengesøknadFrontend s -> tilSvangerskapspengesøknad(s);
        };
        søknad.setMottattdato(LocalDate.now());
        søknad.setTilleggsopplysninger(søknadFrontend.getTilleggsopplysninger());
        return søknad;
    }
}
