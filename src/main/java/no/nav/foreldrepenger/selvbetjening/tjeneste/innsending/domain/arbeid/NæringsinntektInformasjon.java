package no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain.arbeid;

import java.time.LocalDate;

public class NæringsinntektInformasjon {

    public LocalDate dato;
    public Integer næringsinntektEtterEndring;
    public String forklaring;

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dato=" + dato + ", næringsinntektEtterEndring="
                + næringsinntektEtterEndring + ", forklaring=" + forklaring + "]";
    }
}
