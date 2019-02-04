package spritz.examples;

import java.util.concurrent.atomic.AtomicBoolean;
import spritz.Stream;

public class Example30
{
  public static void main( String[] args )
    throws Exception
  {
    final AtomicBoolean complete = new AtomicBoolean();
    Stream
      .range( 42, 1 )
      .onSubscribe( s -> System.out.println( "onSubscribe1 on Thread: " + Thread.currentThread().getName() ) )
      .peek( v -> System.out.println( "Peek1 on Thread: " + Thread.currentThread().getName() ) )
      .onComplete( () -> System.out.println( "onComplete1 on Thread: " + Thread.currentThread().getName() ) )
      .subscribeOn( FakeExecutor.VPU1 )
      .onSubscribe( s -> System.out.println( "onSubscribe2 on Thread: " + Thread.currentThread().getName() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + Thread.currentThread().getName() ) )
      .onComplete( () -> System.out.println( "onComplete2 on Thread: " + Thread.currentThread().getName() ) )
      .subscribeOn( FakeExecutor.VPU2 )
      .peek( v -> System.out.println( "Peek3 on Thread: " + Thread.currentThread().getName() ) )
      .onComplete( () -> System.out.println( "onComplete3 on Thread: " + Thread.currentThread().getName() ) )
      .onTerminate( () -> complete.set( true ) )
      .subscribe( new LoggingSubscriber<>() );

    while ( !complete.get() )
    {
      Thread.sleep( 10 );
    }
    System.exit( 0 );
  }
}
