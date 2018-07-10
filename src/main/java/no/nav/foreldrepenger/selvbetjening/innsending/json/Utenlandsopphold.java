package no.nav.foreldrepenger.selvbetjening.innsending.json;

import java.util.List;

public class Utenlandsopphold {

    public Boolean fødselINorge;
    public Boolean iNorgeNeste12Mnd;
    public Boolean iNorgeSiste12Mnd;

    public List<UtenlandsoppholdPeriode> tidligereOpphold;
    public List<UtenlandsoppholdPeriode> senereOpphold;

}
