package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class RefCountOperator<T>
  extends AbstractStream<T, T>
{
  RefCountOperator( @Nullable final String name, @Nonnull final ConnectableStream<T> upstream )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "refCount" ) : null, upstream );
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
    extends PassThroughSubscription<T, RefCountOperator<T>>
  {
    WorkerSubscription( @Nonnull final RefCountOperator<T> stream,
                        @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      final ConnectableStream<T> stream = getConnectableStream();
      final boolean needsConnect = subjectHasZeroSubscribers( stream );
      super.onSubscribe( subscription );
      if ( needsConnect )
      {
        stream.connect();
      }
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      super.onError( error );
      onDone();
    }

    @Override
    public void onComplete()
    {
      super.onComplete();
      onDone();
    }

    @Override
    void doCancel()
    {
      super.doCancel();
      onDone();
    }

    private void onDone()
    {
      final ConnectableStream<T> stream = getConnectableStream();
      if ( subjectHasZeroSubscribers( stream ) )
      {
        stream.disconnect();
      }
    }

    boolean subjectHasZeroSubscribers( @Nonnull final ConnectableStream<T> stream )
    {
      return !stream.getHub().hasDownstreamSubscribers();
    }

    private ConnectableStream<T> getConnectableStream()
    {
      return (ConnectableStream<T>) getStream().getUpstream();
    }
  }
}
