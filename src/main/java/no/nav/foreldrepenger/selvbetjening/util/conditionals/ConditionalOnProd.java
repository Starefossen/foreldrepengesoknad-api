package no.nav.foreldrepenger.selvbetjening.util.conditionals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

import no.nav.foreldrepenger.selvbetjening.util.conditionals.OnClusterCondition.OnProdCondition;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnProdCondition.class)
public @interface ConditionalOnProd {

}
