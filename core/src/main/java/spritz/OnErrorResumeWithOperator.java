package spritz;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class OnErrorResumeWithOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Function<Throwable, Stream<T>> _streamFromErrorFn;

  OnErrorResumeWithOperator( @Nullable final String name,
                             @Nonnull final Stream<T> upstream,
                             @Nonnull final Function<Throwable, Stream<T>> streamFromErrorFn )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "onErrorResumeWith" ) : null, upstream );
    _streamFromErrorFn = Objects.requireNonNull( streamFromErrorFn );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    getUpstream().subscribe( subscription );
    return subscription;
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, OnErrorResumeWithOperator<T>>
  {
    private boolean _downstreamSubscribed;

    WorkerSubscription( @Nonnull final OnErrorResumeWithOperator<T> stream,
                        @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      if ( _downstreamSubscribed )
      {
        setUpstream( subscription );
      }
      else
      {
        _downstreamSubscribed = true;
        super.onSubscribe( subscription );
      }
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      try
      {
        final Stream<T> nextStream = getStream()._streamFromErrorFn.apply( error );
        if ( null != nextStream )
        {
          nextStream.subscribe( this );
        }
        else
        {
          super.onError( error );
        }
      }
      catch ( final Exception e )
      {
        super.onError( error );
      }
    }
  }
}
