package spritz;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public final class Spritz
{
  private Spritz()
  {
  }

  public static boolean shouldValidateSubscriptions()
  {
    return SpritzConfig.shouldValidateSubscriptions();
  }

  public static boolean purgeTasksWhenRunawayDetected()
  {
    return false;
  }

  /**
   * Creates a stream that emits the parameters as items and then emits the completion signal.
   *
   * @param <T>    the type of items contained in the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> of( @Nonnull final T... values )
  {
    return new StaticStreamSource<>( values );
  }

  /**
   * Creates a stream that emits no items and immediately emits a completion signal.
   *
   * @param <T> the type of items that the stream declared as containing (despite never containing any items).
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> empty()
  {
    return of();
  }

  /**
   * Creates a stream that emits the value parameter as an item if the value is not null.
   *
   * @param <T>   the type of items contained in the stream.
   * @param value the value to emit if non null.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> ofNullable( @Nullable final T value )
  {
    return null == value ? empty() : of( value );
  }

  /**
   * Creates a stream that emits no items and immediately emits an error signal.
   * This is an alias for {@link #error(Throwable)}.
   *
   * @param <T>   the type of items that the stream declared as containing (despite never containing any items).
   * @param error the error to emit.
   * @return the new stream.
   * @see #error(Throwable)
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fail( @Nonnull final Throwable error )
  {
    return new FailStreamSource<>( error );
  }

  /**
   * Creates a stream that emits no items and immediately emits an error signal.
   * This is an alias for {@link #fail(Throwable)}.
   *
   * @param <T>   the type of items that the stream declared as containing (despite never containing any items).
   * @param error the error to emit.
   * @return the new stream.
   * @see #fail(Throwable)
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> error( @Nonnull final Throwable error )
  {
    return new FailStreamSource<>( error );
  }

  /**
   * Creates a stream that emits items of the supplied collection.
   *
   * @param <T>    the type of items contained in the stream.
   * @param values the collection of values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromCollection( @Nonnull final Collection<T> values )
  {
    return new CollectionStreamSource<>( values );
  }

  /**
   * Creates a stream that emits items from the supplied {@link java.util.stream.Stream}.
   *
   * @param <T>    the type of items contained in the stream.
   * @param stream the java.util.stream.Stream stream of values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromStream( @Nonnull final java.util.stream.Stream<T> stream )
  {
    return fromCollection( stream.collect( Collectors.toList() ) );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Callable} parameter.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param callable the function that generates values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromCallable( @Nonnull final Callable<T> callable )
  {
    return new GenerateStreamSource<>( callable );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Supplier} parameter.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param supplier the function that generates values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromSupplier( @Nonnull final Supplier<T> supplier )
  {
    return new GenerateStreamSource<>( supplier::get );
  }

  /**
   * Creates a stream that completes when the {@link Runnable} parameter completes running.
   * The stream will signal an error if the runnable generates an error while running.
   *
   * @param <T>      the type of items that the stream declared as containing (despite never containing any items).
   * @param runnable the runnable to execute.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromRunnable( @Nonnull final Runnable runnable )
  {
    return new RunnableStreamSource<>( runnable );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Supplier} parameter at specified period.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param supplier the function that generates values to emit.
   * @param period   the period with which items are emitted.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> generate( @Nonnull final Supplier<T> supplier, final int period )
  {
    return periodic( period ).map( e -> supplier.get() );
  }

  /**
   * Creates a stream that emits no items, never completes and never fails.
   *
   * @param <T> the type of items that the stream declared as containing (despite never containing any items).
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> never()
  {
    return new NeverStreamSource<>();
  }

  /**
   * Create a stream that emits a sequence of numbers within a specified range.
   * The stream create a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> range( final int start, final int count )
  {
    return new RangeStreamSource( start, count );
  }

  /**
   * Create a stream that emits sequential numbers every specified interval of time.
   * The stream create a sequence of [start, start + count).
   *
   * @param period the period with which items are emitted.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> periodic( final int period )
  {
    return new PeriodicStreamSource( period );
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  @SafeVarargs
  public static <T> Stream<T> concat( @Nonnull final Stream<T>... upstreams )
  {
    return of( upstreams ).concatMap( v -> v );
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  @SafeVarargs
  public static <T> Stream<T> merge( @Nonnull final Stream<T>... upstreams )
  {
    return of( upstreams ).mergeMap( v -> v );
  }
}