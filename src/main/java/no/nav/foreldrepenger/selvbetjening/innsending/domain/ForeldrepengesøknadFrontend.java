package no.nav.foreldrepenger.selvbetjening.innsending.domain;

import static no.nav.foreldrepenger.common.domain.validation.InputValideringRegex.BARE_TALL;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.common.domain.Saksnummer;

@EqualsAndHashCode(callSuper = true)
public final class ForeldrepengesøknadFrontend extends SøknadFrontend {

    @NotNull
    @Pattern(regexp = BARE_TALL)
    private final String dekningsgrad;
    @Valid
    private final List<UttaksplanPeriode> uttaksplan;

    private final Boolean ønskerJustertUttakVedFødsel;

    @Builder
    @JsonCreator
    public ForeldrepengesøknadFrontend(LocalDateTime opprettet,
                                       String type,
                                       Saksnummer saksnummer,
                                       SøkerFrontend søker,
                                       BarnFrontend barn,
                                       AnnenForelderFrontend annenForelder,
                                       UtenlandsoppholdFrontend informasjonOmUtenlandsopphold,
                                       String situasjon,
                                       Boolean erEndringssøknad,
                                       String tilleggsopplysninger,
                                       List<VedleggFrontend> vedlegg,
                                       String dekningsgrad,
                                       List<UttaksplanPeriode> uttaksplan,
                                       Boolean ønskerJustertUttakVedFødsel) {
        super(opprettet, type, saksnummer, søker, barn, annenForelder, informasjonOmUtenlandsopphold, situasjon,
            erEndringssøknad, tilleggsopplysninger, vedlegg);
        this.dekningsgrad = dekningsgrad;
        this.uttaksplan = uttaksplan;
        this.ønskerJustertUttakVedFødsel = ønskerJustertUttakVedFødsel;
    }

    public String getDekningsgrad() {
        return dekningsgrad;
    }

    public List<UttaksplanPeriode> getUttaksplan() {
        return uttaksplan;
    }

    public Boolean isØnskerJustertUttakVedFødsel() {
        return ønskerJustertUttakVedFødsel;
    }

    @Override
    public String toString() {
        return "Foreldrepengesøknad{" +
            "dekningsgrad='" + dekningsgrad + '\'' +
            ", uttaksplan=" + uttaksplan +
            ", ønskerJustertUttakVedFødsel=" + ønskerJustertUttakVedFødsel +
            "} " + super.toString();
    }
}
