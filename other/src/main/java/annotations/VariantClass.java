package annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
public @interface VariantClass {
    String original() default "";
    String variant() default "";
}