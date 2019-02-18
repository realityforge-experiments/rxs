package spritz;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * A place to store utility methods that should not be used outside this package.
 */
final class SpritzUtil
{
  private SpritzUtil()
  {
  }

  /**
   * Return the string generated by specified supplier.
   * Typically this is used to delay instantiation of an error message until it is required.
   * If the supplier generates an exception then catch it and generate an explanatory message.
   *
   * @param message the string supplier to generate message.
   * @return the string generated by supplier.
   */
  @Nonnull
  static String safeGetString( @Nonnull final Supplier<String> message )
  {
    try
    {
      return message.get();
    }
    catch ( final Throwable t )
    {
      return "Exception generated whilst attempting to get supplied message.\n" + throwableToString( t );
    }
  }

  /**
   * Return string converted to stack trace.
   * This method uses explicit traversal to be compatible with GWT.
   * Contrast this with the following code that is not compatible with GWT.
   *
   * <pre>
   * final StringWriter out = new StringWriter();
   * t.printStackTrace( new PrintWriter( out ) );
   * </pre>
   *
   * @param throwable the throwable to convert
   * @return the stack trace.
   */
  @Nonnull
  static String throwableToString( @Nonnull final Throwable throwable )
  {
    final StringBuilder sb = new StringBuilder();
    Throwable t = throwable;
    while ( null != t )
    {
      addCausedByPrefix( sb );
      sb.append( t.toString() );
      for ( final StackTraceElement element : t.getStackTrace() )
      {
        sb.append( "\n  at " ).append( element );
      }
      t = t.getCause();
    }

    return sb.toString();
  }

  private static void addCausedByPrefix( @Nonnull final StringBuilder sb )
  {
    if ( 0 != sb.length() )
    {
      sb.append( "\nCaused by: " );
    }
  }
}
