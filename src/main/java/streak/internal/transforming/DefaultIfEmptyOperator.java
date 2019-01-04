package streak.internal.transforming;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamWithUpstream;
import streak.internal.SubscriptionWithDownstream;

final class DefaultIfEmptyOperator<T>
  extends StreamWithUpstream<T>
{
  @Nonnull
  private final T _defaultValue;

  DefaultIfEmptyOperator( @Nonnull final Flow.Stream<? extends T> upstream, @Nonnull final T defaultValue )
  {
    super( upstream );
    _defaultValue = Objects.requireNonNull( defaultValue );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _defaultValue ) );
  }

  private static final class WorkerSubscription<T>
    extends SubscriptionWithDownstream<T>
  {
    @Nonnull
    private final T _defaultValue;
    private boolean _elementEmitted;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, @Nonnull final T defaultValue )
    {
      super( subscriber );
      _defaultValue = defaultValue;
    }

    @Override
    public void onComplete()
    {
      if ( !_elementEmitted )
      {
        super.onNext( _defaultValue );
      }
      super.onComplete();
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      _elementEmitted = true;
      super.onNext( item );
    }
  }
}
