package streak;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;

final class CollectionStreamSource<T>
  implements Stream<T>
{
  private final Collection<T> _data;

  CollectionStreamSource( @Nonnull final Collection<T> data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _data );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    private final Subscriber<? super T> _subscriber;
    private final Collection<T> _data;
    private boolean _done;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, @Nonnull final Collection<T> data )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _data = Objects.requireNonNull( data );
    }

    void pushData()
    {
      for ( final T item : _data )
      {
        if ( isDisposed() )
        {
          break;
        }
        _subscriber.onNext( item );
      }
      if ( isNotDisposed() )
      {
        _subscriber.onComplete();
        dispose();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
      _done = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisposed()
    {
      return _done;
    }
  }
}