package spritz.examples.dom.web_socket;

import com.google.gwt.core.client.EntryPoint;
import elemental2.core.ArrayBuffer;
import elemental2.dom.DomGlobal;
import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.Stream;
import spritz.Subscriber;
import spritz.Subscription;
import spritz.WebSocketHub;
import spritz.dom.WebSocketCloseRequest;
import spritz.dom.WebSocketConfig;
import spritz.dom.WebSocketMessageRequest;
import spritz.dom.WebSocketRequest;
import spritz.dom.WebSocketResponse;
import zemeckis.Zemeckis;

public final class WebSocketExample
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    final WebSocketHub hub =
      Stream.webSocket( new WebSocketConfig( "wss://echo.websocket.org", null, "arraybuffer" ) );

    DomGlobal.console.log( "Construction complete" );
    final LoggingSubscriber<WebSocketResponse> subscriber = new LoggingSubscriber<>( "WS" );
    hub.subscribe( subscriber );
    hub.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( "Hello" ) ) );
    hub.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( new ArrayBuffer( 22 ) ) ) );
    hub.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( "Hello" ) ) );

    Stream.<WebSocketRequest>create( s -> {
      s.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( "ABC" ) ) );
      s.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( "def" ) ) );
      s.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( "GHI" ) ) );
      // Never completes ....
    } ).subscribe( hub );

    DomGlobal.setTimeout( e -> {
      DomGlobal.console.log( "PreCancel" );
      subscriber.getSubscription().cancel();
      DomGlobal.console.log( "PostCancel" );
    }, 2000 );

    DomGlobal.setTimeout( e -> hub.next( new WebSocketMessageRequest( WebSocket.SendDataUnionType.of( "Hello" ) ) ),
                          3000 );
    DomGlobal.setTimeout( e -> hub.subscribe( subscriber ), 3500 );
    DomGlobal.setTimeout( e -> hub.next( new WebSocketCloseRequest( 3000, "Hello" ) ), 4000 );
    DomGlobal.setTimeout( e -> hub.complete(), 5000 );

    DomGlobal.console.log( "Subscribe complete" );
  }

  static final class LoggingSubscriber<T>
    implements Subscriber<T>
  {
    @Nullable
    private final String _prefix;
    private Subscription _subscription;

    LoggingSubscriber( @Nullable final String prefix )
    {
      _prefix = prefix;
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      DomGlobal.console.log( prefix() + "onSubscribe(" + subscription + ")" + suffix() );
      _subscription = subscription;
    }

    @Override
    public void onItem( @Nonnull final T item )
    {
      DomGlobal.console.log( prefix() + "onItem(" + item + ")" + suffix() );
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      DomGlobal.console.log( prefix() + "onError(" + error + ")" + suffix() );
    }

    @Override
    public void onComplete()
    {
      DomGlobal.console.log( prefix() + "onComplete()" + suffix() );
    }

    @Nonnull
    private String prefix()
    {
      return null == _prefix ? "" : _prefix + ":";
    }

    @Nonnull
    private String suffix()
    {
      return " on " + Objects.requireNonNull( Zemeckis.currentVpu() ).getName();
    }

    Subscription getSubscription()
    {
      return _subscription;
    }
  }
}
