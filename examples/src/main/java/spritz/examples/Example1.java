package spritz.examples;

import spritz.Stream;

public class Example1
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .range( 42, 20 )
                       .peek( v -> System.out.println( "Pre Take Peek: " + v ) )
                       .take( 5 )
                       .peek( v -> System.out.println( "Post Take Peek: " + v ) )
                       .peekTerminate( () -> System.out.println( "onTerminate()" ) ) );
  }
}
