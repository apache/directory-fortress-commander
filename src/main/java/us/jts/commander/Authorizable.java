package us.jts.commander;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */

@Retention( RetentionPolicy.RUNTIME)
@Target( ElementType.TYPE)
@Inherited
public @interface Authorizable
{
}
